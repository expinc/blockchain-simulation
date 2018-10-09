package expinc.blockchain.miner;


import java.util.List;
import java.util.Random;
import expinc.blockchain.ledger.Ledger;
import expinc.blockchain.ledger.Transaction;
import expinc.blockchain.miner.Miner.Configure;
import expinc.finance.Money;


class Accountant extends Peasant
{
	private int minerId; // one to one key with Miners
	private Configure configure;
	private LedgerWrapper ledgerWrapper;
	private TransactionDispatcher transactionDispatcher;
	private List<Miner> peers;
	private Random random;


	Accountant(int minerId, Configure configure, LedgerWrapper ledgerWrapper,
			TransactionDispatcher transactionDispatcher, List<Miner> peers)
	{
		this.minerId = minerId;
		this.configure = configure;
		this.ledgerWrapper = ledgerWrapper;
		this.transactionDispatcher = transactionDispatcher;
		this.peers = peers;
		random = new Random();
	}


	@Override
	public void run()
	{
		long startTime = System.currentTimeMillis();
		boolean interrupted = false;
		do
		{
			try
			{
				Transaction newTransaction = generateTransaction();
				transactionDispatcher.dispatch(newTransaction);
				Thread.sleep(configure.transactionInterval);
			}
			catch (InterruptedException e)
			{
				interrupted = true;
			}
		}
		while (false == interrupted && configure.lifecycle > System.currentTimeMillis() - startTime);
	}


	private Transaction generateTransaction()
	{
		float randomFloat = random.nextFloat();
		boolean evil = true;
		if (randomFloat > configure.evilRatio)
			evil = false;

		randomFloat = random.nextFloat();
		Money amount = Money.product(ledgerWrapper.getLedger().getCurrentBalance(), new Money(randomFloat));
		if (true == evil)
			amount = Money.sum(amount, ledgerWrapper.getLedger().getCurrentBalance());

		int payeeIndex = random.nextInt(peers.size());
		if (minerId == peers.get(payeeIndex).getId()) // avoid transfer to self
														// but not guaranteed
			payeeIndex = (payeeIndex + 1) % peers.size();

		return transfer(peers.get(payeeIndex), amount);
	}


	private Transaction transfer(Miner payee, Money amount)
	{
		Ledger ledger = ledgerWrapper.getLedger();
		int transSeq = ledger.getNextTransSeq();
		Transaction trans = new Transaction(minerId, transSeq, payee.getId(), amount);

		List<Transaction.Input> inputs = ledger.prepareTransactionInputs(amount);
		Money inputSum = Money.ZERO;
		for (Transaction.Input input : inputs)
		{
			trans.addInput(input);
			Transaction inputTrans = ledger.getTransaction(input.minerId, input.transSeq);
			inputSum = Money.sum(inputSum, inputTrans.getOutput(input.outputIndex).amount);
		}

		trans.addOutput(payee.getId(), amount);
		Money remain = Money.difference(inputSum, amount);
		if (false == remain.equals(Money.ZERO))
			trans.addOutput(minerId, remain);

		return trans;
	}
}
