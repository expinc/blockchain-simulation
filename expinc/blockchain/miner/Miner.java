package expinc.blockchain.miner;

import java.util.List;
import expinc.blockchain.core.Block;
import expinc.blockchain.ledger.Transaction;
import expinc.finance.Money;

public class Miner
{
	class Configure
	{
		public long lifecycle;
		public long transactionInterval;
		public float evilRatio;
	}
	
	
	private int id;
	private Configure configure;
	private List<Miner> peers;	// include self
	private LedgerWrapper ledgerWrapper;
	private TransactionInbox transactionInbox;
	private BlockInbox blockInbox;
	private TransactionDispatcher transactionDispatcher;
	private Accountant accountant;
	private BlockDispatcher blockDispatcher;
	private Solver solver;
	
	
	public Miner(int id, long lifecycle, long transactionInterval, float evilRatio, Money initialBalance, List<Miner> peers)
	{
		this.id = id;
		configure = new Configure();
		configure.lifecycle = lifecycle;
		configure.transactionInterval = transactionInterval;
		configure.evilRatio = evilRatio;
		this.peers = peers;
		
		ledgerWrapper = new LedgerWrapper(id, initialBalance);
		transactionInbox = new TransactionInbox();
		blockInbox = new BlockInbox();
		
		transactionDispatcher = new TransactionDispatcher(lifecycle, peers);
		accountant = new Accountant(id, configure, ledgerWrapper, (TransactionDispatcher) transactionDispatcher, peers);
		blockDispatcher = new BlockDispatcher(lifecycle, peers);
		solver = new Solver(lifecycle, ledgerWrapper, transactionInbox, blockInbox, (BlockDispatcher) blockDispatcher, peers);
	}
	
	
	public void startRun()
	{
		accountant.start();
		transactionDispatcher.start();
		blockDispatcher.start();
		solver.start();
	}
	
	
	public void shutOff()
	{
		accountant.stop();
		transactionDispatcher.stop();
		blockDispatcher.stop();
		solver.stop();
	}
	
	
	public void waitForEnd()
	{
		accountant.waitForEnd();
		transactionDispatcher.waitForEnd();
		blockDispatcher.waitForEnd();
		solver.waitForEnd();
	}
	
	
	int getId()
	{
		return id;
	}
	
	
	void receiveBlock(Block block) throws InterruptedException
	{
		blockInbox.add(block);
	}
	
	
	void receiveTransaction(Transaction trans) throws InterruptedException
	{
		transactionInbox.add(trans);
	}
	
	
	public void printResults(String folder)
	{
		// TODO
	}
}
