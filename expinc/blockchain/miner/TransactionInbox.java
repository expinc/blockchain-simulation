package expinc.blockchain.miner;


import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import expinc.blockchain.ledger.Transaction;


class TransactionInbox
{
	Queue<Transaction> transactions;
	private Semaphore mutex;


	TransactionInbox()
	{
		transactions = new LinkedList<Transaction>();
		mutex = new Semaphore(1);
	}


	void add(Transaction trans) throws InterruptedException
	{
		mutex.acquire();
		transactions.add(trans);
		mutex.release();
	}
	
	
	boolean isEmpty() throws InterruptedException
	{
		mutex.acquire();
		boolean result = transactions.isEmpty();
		mutex.release();
		return result;
	}
	
	
	List<Transaction> retrieveAll() throws InterruptedException
	{
		mutex.acquire();
		List<Transaction> result = new LinkedList<Transaction>(transactions);
		mutex.release();
		return result;
	}
}
