package expinc.blockchain.miner;


abstract class Peasant implements Runnable
{
	protected Thread thread;


	void start()
	{
		thread = new Thread(this);
		thread.start();
	}


	void stop()
	{
		thread.interrupt();
	}


	void waitForEnd()
	{
		boolean joined = false;
		while (false == joined)
		{
			try
			{
				thread.join();
				joined = true;
			}
			catch (InterruptedException e)
			{}
		}
	}


	@Override
	abstract public void run();
}
