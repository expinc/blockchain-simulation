package expinc.blockchain.miner;


import java.util.*;
import java.util.concurrent.Semaphore;
import expinc.blockchain.core.Block;


class BlockDispatcher extends Peasant
{
	private long lifecycle;
	private Queue<Block> blocksToDispatch;
	private List<Miner> peers;
	private Semaphore mutex;
	private Semaphore semBlocks;


	BlockDispatcher(long lifecycle, List<Miner> peers)
	{
		this.lifecycle = lifecycle;
		blocksToDispatch = new LinkedList<Block>();
		this.peers = peers;
		mutex = new Semaphore(1);
		semBlocks = new Semaphore(0);
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
				semBlocks.acquire();
				mutex.acquire();
				Block block = blocksToDispatch.poll();
				mutex.release();

				for (Miner peer : peers)
				{
					peer.receiveBlock(block);
				}
			}
			catch (InterruptedException e)
			{
				interrupted = true;
			}
		}
		while (lifecycle > System.currentTimeMillis() - startTime && false == interrupted);
	}
	
	
	void dispatch(Block block) throws InterruptedException
	{
		mutex.acquire();
		blocksToDispatch.add(block);
		mutex.release();
		semBlocks.release();
	}
}
