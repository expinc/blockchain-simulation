package test;

public class MyRunnable implements Runnable
{
	private Thread thread;
	private int i;
	
	public void start(int i)
	{
		this.i = i;
		thread = new Thread(this);
		thread.start();
	}
	
	public void join()
	{
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getI()
	{
		return i;
	}

	@Override
	public void run()
	{
		
	}
}
