package expinc.finance;


import java.math.BigInteger;


public class Money
{
	public static final Money ZERO;
	private static final int scale;

	static
	{
		ZERO = new Money();
		scale = 1000000;
	}

	private BigInteger value;


	public static Money product(Money l, Money r)
	{
		return new Money(l.value.multiply(r.value));
	}


	public static Money quotient(Money l, Money r)
	{
		return new Money(l.value.divide(r.value));
	}


	public static Money sum(Money l, Money r)
	{
		return new Money(l.value.add(r.value));
	}


	public static Money difference(Money l, Money r)
	{
		return new Money(l.value.subtract(r.value));
	}


	public Money()
	{
		this.value = BigInteger.ZERO;
	}


	public Money(int value)
	{
		String valueStr = Integer.toString(value * scale);
		this.value = new BigInteger(valueStr);
	}


	public Money(float value)
	{
		String valueStr = Float.toString(value * scale);
		this.value = new BigInteger(valueStr);
	}


	public Money(BigInteger value)
	{
		this.value = value;
	}


	public int compareTo(Money other)
	{
		return this.value.compareTo(other.value);
	}


	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (false == (o instanceof Money))
			return false;

		Money other = (Money) o;
		return this.value.equals(other.value);
	}
}
