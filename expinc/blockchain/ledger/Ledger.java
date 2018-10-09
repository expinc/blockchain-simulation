package expinc.blockchain.ledger;

import java.util.*;
import expinc.finance.Money;

public class Ledger
{
	private int minerId;	// one to one key with Miners
	private int nextTransSeq;
	private Money initialBalance;
	private Money currentBalance;
	private List<Transaction> transactions;
	private Set<Transaction.Output> usedOutputs;
	
	
	public Ledger(int minerId, Money initialBalance)
	{
		this.minerId = minerId;
		nextTransSeq = 0;
		this.initialBalance = initialBalance;
		this.currentBalance = initialBalance;
		
		Transaction initTrans = new Transaction(0, getNextTransSeq(), minerId, initialBalance);
		initTrans.addOutput(minerId, initialBalance);
		transactions = new LinkedList<Transaction>();
		transactions.add(initTrans);
		
		usedOutputs = new HashSet<Transaction.Output>();
	}
	
	
	public Money getCurrentBalance()
	{
		return currentBalance;
	}
	
	
	public int getNextTransSeq()
	{
		return nextTransSeq++;
	}
	
	
	// TODO: check concurrent issue
	public List<Transaction.Input> prepareTransactionInputs(Money amount)
	{
		List<Transaction.Input> result = new LinkedList<Transaction.Input>();
		Money sum = Money.ZERO;
		for (Transaction transaction : transactions)
		{
			List<Transaction.Output> outputs = transaction.getOutputs();
			for (Transaction.Output output : outputs)
			{
				if (this.minerId == output.toMinerId && false == usedOutputs.contains(output))
				{
					Transaction.Input input = new Transaction.Input();
					input.minerId = output.toMinerId;
					input.transSeq = output.transSeq;
					input.outputIndex = output.index;
					sum = Money.sum(sum, output.amount);
					result.add(input);
					if (0 <= sum.compareTo(amount))
					{
						return result;
					}
				}
			}
		}
		
		return null;
	}
	
	
	// TODO: check concurrent issue
	public Transaction getTransaction(int fromMinerId, int transSeq)
	{
		for (Transaction transaction : transactions)
		{
			if (fromMinerId == transaction.getFromMinerId() && transSeq == transaction.getTransSeq())
				return transaction;
		}

		return null;
	}
}
