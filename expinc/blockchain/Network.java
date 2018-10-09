package expinc.blockchain;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Semaphore;
import expinc.blockchain.miner.Miner;
import expinc.finance.Money;


public class Network
{
	public enum Status
	{
		INITIALIZED, EXECUTING, EXECUTED
	}

	private String name;
	private Configure configure;
	private List<Miner> miners;
	private Status status;
	private Semaphore mutex;


	public static void main(String[] args)
	{
		Configure configure = new Configure();
		Network network = new Network("my-block-chain-network", configure);
		network.boot();
		network.waitForEnd();
		network.printResults("D:/my-git/blockchain-simulation/BlockchainSimulation/output/");
	}


	public Network(String name, Configure configure)
	{
		this.name = name;
		this.configure = new Configure(configure);
		miners = new LinkedList<Miner>();
		Random random = new Random();
		for (int i = 0; i < configure.getCountMiners(); ++i)
		{
			long transactionInterval = Math.abs(random.nextLong() % configure.getMaxTransactionInterval()) + 1;
			float evilRatio = Math.abs(random.nextInt(100)) / 100.0f * configure.getMaxEvilRatio();
			Money initialBalance = Money.quotient(
					Money.product(configure.getMaxInitialBalance(), new Money(Math.abs(random.nextInt(100)))),
					new Money(100));
			miners.add(
					new Miner(i + 1, configure.getLifecycle(), transactionInterval, evilRatio, initialBalance, miners));
		}
		status = Status.INITIALIZED;
		mutex = new Semaphore(1);
	}


	public boolean boot()
	{
		boolean result = false;

		try
		{
			mutex.acquire();
			{
				if (Status.INITIALIZED == status)
				{
					for (Miner miner : miners)
					{
						miner.startRun();
					}
					status = Status.EXECUTING;
					result = true;
				}
				System.out.println("Block chain network " + name + " has launched.");
			}
			mutex.release();
		}
		catch (InterruptedException e)
		{}

		return result;
	}


	public boolean shutOff()
	{
		boolean result = false;

		try
		{
			mutex.acquire();
			{
				if (Status.EXECUTING == status)
				{
					for (Miner miner : miners)
					{
						miner.shutOff();
					}
					status = Status.EXECUTED;
					result = true;
				}
				System.out.println("Block chain network " + name + " has shut off.");
			}
			mutex.release();
		}
		catch (InterruptedException e)
		{}

		return result;
	}


	public boolean waitForEnd()
	{
		boolean result = false;

		try
		{
			mutex.acquire();
			{
				System.out.println("Waiting for block chain network " + name + " to end...");
				if (Status.EXECUTING == status)
				{
					for (Miner miner : miners)
					{
						miner.waitForEnd();
					}
					status = Status.EXECUTED;
					result = true;
				}
				System.out.println("Block chain network " + name + " has shut off.");
			}
			mutex.release();
		}
		catch (InterruptedException e)
		{}

		return result;
	}


	public boolean printResults(String outputPath)
	{
		boolean result = true;
		try
		{
			mutex.acquire();
			{
				if (Status.EXECUTED != status)
					result = false;
			}
			mutex.release();
		}
		catch (InterruptedException e)
		{
			result = false;
		}
		if (false == result)
			return result;

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		LocalDateTime now = LocalDateTime.now();
		String folderName = outputPath + "blockchain-simulation-results-" + dateTimeFormatter.format(now);

		try
		{
			Files.createDirectories(Paths.get(folderName));
			for (Miner miner : miners)
			{
				miner.printResults(folderName);
			}
			System.out.println("Block chain network " + name + " has printed its results into " + folderName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			result = false;
		}

		return result;
	}
}
