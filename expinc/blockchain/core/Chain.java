package expinc.blockchain.core;

import java.util.List;

public class Chain
{
	private class BlockWrapper
	{
		public Block block;
		public BlockWrapper prev;
		public int length;
		
		public BlockWrapper(Block block, BlockWrapper prev)
		{
			this.block = block;
			this.prev = prev;
			if (null == prev)
				length = 1;
			else
				length = prev.length + 1;
		}
	}
	
	
	private List<BlockWrapper> tails;
	private BlockWrapper mainBranchTail;
	
	
	public Chain()
	{
		Block genesis = Block.createGenesis();
		BlockWrapper wrapper = new BlockWrapper(genesis, null);
		tails.add(wrapper);
		mainBranchTail = wrapper;
	}
	
	
	public void refreshBranch()
	{
		BlockWrapper maxTail = mainBranchTail;
		for (BlockWrapper tail : tails)
		{
			if (tail.length > maxTail.length)
				maxTail = tail;
		}
		mainBranchTail = maxTail;
	}
	
	
	public Block getMainTail()
	{
		return mainBranchTail.block;
	}
	
	
	public boolean isValidToAdd(Block block)
	{
		// TODO
		return false;
	}
}
