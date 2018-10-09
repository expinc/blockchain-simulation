package expinc.blockchain.miner;

import expinc.blockchain.core.Block;
import expinc.blockchain.core.Chain;
import expinc.blockchain.ledger.Ledger;
import expinc.finance.Money;

class LedgerWrapper
{
	private Ledger ledger;
	private Chain chain;
	
	
	LedgerWrapper(int id, Money initialBalance)
	{
		ledger = new Ledger(id, initialBalance);
		chain = new Chain();
	}
	
	
	Ledger getLedger()
	{
		return ledger;
	}
	
	
	Chain getChain()
	{
		return chain;
	}
	
	
	boolean isValidBlock(Block block)
	{
		if (!chain.isValidToAdd(block) || !block.isValidHash(block.getHash()))
			return false;
		else
			return true;
	}
	
	
	boolean addBlock(Block block)
	{
		// TODO
		return false;
	}
}
