package expinc.blockchain.miner;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import expinc.blockchain.core.Block;


class BlockInbox
{
	private Queue<Block> blocks;
	private Semaphore mutex;
	
	
	BlockInbox()
	{
		blocks = new LinkedList<Block>();
		mutex = new Semaphore(1);
	}
	
	
	void add(Block block) throws InterruptedException
	{
		mutex.acquire();
		blocks.add(block);
		mutex.release();
	}
	
	
	boolean isEmpty() throws InterruptedException
	{
		mutex.acquire();
		boolean result = blocks.isEmpty();
		mutex.release();
		return result;
	}
	
	
	Block poll() throws InterruptedException
	{
		mutex.acquire();
		Block result = blocks.poll();
		mutex.release();
		return result;
	}
}
