package expinc.blockchain.miner;


import java.util.*;
import java.util.concurrent.Semaphore;
import expinc.blockchain.ledger.Transaction;


class TransactionDispatcher extends Peasant
{
	private long lifecycle;
	private Queue<Transaction> transToDispatch;
	private List<Miner> peers;
	private Semaphore mutex;
	private Semaphore semTrans;


	TransactionDispatcher(long lifecycle, List<Miner> peers)
	{
		this.lifecycle = lifecycle;
		transToDispatch = new LinkedList<Transaction>();
		this.peers = peers;
		mutex = new Semaphore(1);
		semTrans = new Semaphore(0);
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
				semTrans.acquire();
				mutex.acquire();
				Transaction trans = transToDispatch.poll();
				mutex.release();

				for (Miner peer : peers)
				{
					peer.receiveTransaction(trans);
				}
			}
			catch (InterruptedException e)
			{
				interrupted = true;
			}
		}
		while (lifecycle > System.currentTimeMillis() - startTime && false == interrupted);
	}


	void dispatch(Transaction transaction) throws InterruptedException
	{
		mutex.acquire();
		transToDispatch.add(transaction);
		mutex.release();
		semTrans.release();
	}
}
