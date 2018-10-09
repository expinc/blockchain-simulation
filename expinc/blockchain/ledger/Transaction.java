package expinc.blockchain.ledger;

import java.util.LinkedList;
import java.util.List;
import expinc.finance.Money;

public class Transaction
{
	public static class Input
	{
		public int minerId;
		public int transSeq;
		public int outputIndex;
	}
	
	
	public static class Output
	{
		public int minerId;
		public int transSeq;
		public int index;
		public int toMinerId;
		public Money amount;
		
		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			
			if (false == (o instanceof Output))
				return false;
			
			Output other = (Output) o;
			if (
				this.minerId == other.minerId &&
				this.transSeq == other.transSeq &&
				this.index == other.index &&
				this.toMinerId == other.toMinerId &&
				this.amount.equals(other.amount))
				return true;
			else
				return false;
		}
	}
	
	
	private int fromMinerId;
	private int transSeq;
	private int toMinerId;
	private Money amount;
	private List<Input> inputs;
	private List<Output> outputs;
	
	
	public Transaction(int fromMinerId, int transSeq, int toMinerId, Money amount)
	{
		this.fromMinerId = fromMinerId;
		this.transSeq = transSeq;
		this.toMinerId = toMinerId;
		this.amount = amount;
		inputs = new LinkedList<Input>();
		outputs = new LinkedList<Output>();
	}
	
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		
		if (false == (o instanceof Transaction))
			return false;
		
		Transaction other = (Transaction) o;
		if (this.fromMinerId == other.fromMinerId && this.transSeq == other.transSeq)
			return true;
		else
			return false;
	}
	
	
	public int getFromMinerId()
	{
		return fromMinerId;
	}
	
	
	public int getTransSeq()
	{
		return transSeq;
	}
	
	
	public void addInput(Input input)
	{
		inputs.add(input);
	}
	
	
	public void addOutput(int minerId, Money amount)
	{
		Output output = new Output();
		output.minerId = fromMinerId;
		output.transSeq = this.transSeq;
		output.index = getMaxOutputIndex() + 1;
		output.toMinerId = minerId;
		output.amount = amount;
	}
	
	
	public Output getOutput(int index)
	{
		Output result = null;
		for (Output output : outputs)
		{
			if (output.index == index)
				result = output;
		}
		return result;
	}
	
	
	List<Output> getOutputs()
	{
		return outputs;
	}
	
	
	private int getMaxOutputIndex()
	{
		int maxIndex = 0;
		for (Output output : outputs)
		{
			if (output.index > maxIndex)
				maxIndex = output.index;
		}
		return maxIndex;
	}
}
