package expinc.blockchain;


import expinc.finance.Money;


public class Configure
{
	private int countMiners;
	private long lifecycle;
	private long maxTransactionInterval;
	private float maxEvilRatio;
	private Money maxInitialBalance;


	public Configure()
	{
		countMiners = 5;
		lifecycle = 1000 * 60;
		maxTransactionInterval = 1000 * 10;
		maxEvilRatio = 0.2f;
		maxInitialBalance = new Money(10000);
	}


	public Configure(Configure other)
	{
		this.countMiners = other.countMiners;
		this.lifecycle = other.lifecycle;
		this.maxTransactionInterval = other.maxTransactionInterval;
		this.maxEvilRatio = other.maxEvilRatio;
		this.maxInitialBalance = other.maxInitialBalance;
	}


	public int getCountMiners()
	{
		return countMiners;
	}


	public void setCountMiners(int value)
	{
		if (0 <= value && 100 >= value)
			countMiners = value;
	}


	public long getLifecycle()
	{
		return lifecycle;
	}


	public void setLifecycle(long value)
	{
		if (1000 <= value)
			lifecycle = value;
	}


	public long getMaxTransactionInterval()
	{
		return maxTransactionInterval;
	}


	public void setMaxTransactionInterval(long value)
	{
		if (0 < value && value < lifecycle)
			maxTransactionInterval = value;
	}


	public float getMaxEvilRatio()
	{
		return maxEvilRatio;
	}


	public void setMaxEvilRatio(float value)
	{
		if (0.0f <= value && 1.0f >= value)
			maxEvilRatio = value;
	}


	public Money getMaxInitialBalance()
	{
		return maxInitialBalance;
	}


	public void setMaxInitialBalance(Money value)
	{
		if (0 <= value.compareTo(Money.ZERO))
			maxInitialBalance = value;
	}
}
