package expinc.blockchain.miner;

import java.util.LinkedList;
import java.util.List;
import expinc.blockchain.core.Block;
import expinc.blockchain.ledger.Transaction;


class Solver extends Peasant
{
	private enum Status
	{
		BEGIN,
		SOLVING,
		CHECKING_BLOCK,
		BROADCASTING,
		VALIDATING,
		SLEEPING,
		REFRESHING,
		END
	}
	
	
	private static long idleDuration = 1000 * 1;
	
	
	private long lifecycle;
	private LedgerWrapper ledgerWrapper;
	private TransactionInbox transactionInbox;
	private BlockInbox blockInbox;
	private BlockDispatcher blockDispatcher;
	private List<Miner> peers;
	
	
	Solver(
			long lifecycle,
			LedgerWrapper ledgerWrapper,
			TransactionInbox transactionInbox,
			BlockInbox blockInbox,
			BlockDispatcher blockDispatcher,
			List<Miner> peers)
	{
		this.lifecycle = lifecycle;
		this.ledgerWrapper = ledgerWrapper;
		this.transactionInbox = transactionInbox;
		this.blockInbox = blockInbox;
		this.blockDispatcher = blockDispatcher;
		this.peers = peers;
	}
	

	@Override
	public void run()
	{
		long startTime = System.currentTimeMillis();
		boolean interrupted = false;
		List<Transaction> transToSolve = new LinkedList<Transaction>();
		int nonce = 0;
		do
		{
			Status status = Status.BEGIN;
			Block block = null;
			boolean solving = false;
			try
			{
				while (Status.END != status)
				{
					switch (status)
					{
						case BEGIN:
							if (!transactionInbox.isEmpty())
							{
								transToSolve.addAll(transactionInbox.retrieveAll());
								status = Status.SOLVING;
								solving = true;
							}
							else
								status = Status.CHECKING_BLOCK;
						break;

						case SOLVING:
							block = solve(transToSolve, nonce++);
							if (null != block)
							{
								status = Status.BROADCASTING;
								solving = false;
							}
							else
								status = Status.CHECKING_BLOCK;
						break;

						case CHECKING_BLOCK:
							if (!blockInbox.isEmpty())
							{
								block = blockInbox.poll();
								status = Status.VALIDATING;
							}
							else
							{
								if (solving)
									status = Status.SOLVING;
								else
									status = Status.SLEEPING;
							}
						break;

						case BROADCASTING:
							blockDispatcher.dispatch(block);
							status = Status.REFRESHING;
						break;

						case VALIDATING:
							if (ledgerWrapper.isValidBlock(block))
							{
								ledgerWrapper.addBlock(block);
								status = Status.REFRESHING;
							}
							else
								status = Status.END;
						break;

						case SLEEPING:
							Thread.sleep(idleDuration);
							status = Status.END;
						break;
						
						case REFRESHING:
							transToSolve.addAll(transactionInbox.retrieveAll());
							clearTransByBlock(transToSolve, block);
							ledgerWrapper.getChain().refreshBranch();
							status = Status.END;
						break;

						default:
						break;
					}
				}
			}
			catch (InterruptedException e)
			{
				interrupted = true;
			}
		}
		while (false == interrupted && lifecycle > System.currentTimeMillis() - startTime);
	}
	
	
	private Block solve(List<Transaction> trans, int nonce)
	{
		Block block = new Block();
		Block tail = ledgerWrapper.getChain().getMainTail();
		block.setPrevious(tail);
		block.setTrans(trans);
		block.setNonce(nonce);
		if (block.calculateHash(true))
			return block;
		else
			return null;
	}
	
	
	private void clearTransByBlock(List<Transaction> trans, Block block)
	{
		// TODO
	}

}
