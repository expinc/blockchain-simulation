package expinc.blockchain.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import expinc.blockchain.ledger.Transaction;


public class Block
{
	private static MessageDigest digest;
	private static byte[] hashComplyMask;
	
	
	static
	{
		try
		{
			digest = MessageDigest.getInstance("SHA-256");
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		hashComplyMask = new byte[1];
		hashComplyMask[0] = (byte) 0xff;
	}
	
	
	private byte[] previousHash;
	private List<Transaction> transactions;
	private int nonce;
	private byte[] hash;
	
	
	static Block createGenesis()
	{
		Block genesis = new Block();
		genesis.calculateHash(false);
		return genesis;
	}
	
	
	public static byte[] calculateHash(Block block, boolean needComply)
	{
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutStream;
		try
		{
			objectOutStream = new ObjectOutputStream(byteOutStream);
			objectOutStream.writeObject(block.previousHash);
			objectOutStream.writeObject(block.transactions);
			objectOutStream.writeObject(block.nonce);
			objectOutStream.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
		byte[] result = byteOutStream.toByteArray();
		// TODO: use digest to calc hash
		if (!needComply || isCompliedHash(result))
			return result;
		else
			return null;
	}
	
	
	private static boolean isCompliedHash(byte[] hash)
	{
		for (int i = 0; i < hashComplyMask.length; ++i)
		{
			byte andResult = (byte) (hashComplyMask[i] & hash[i]);
			if (0 != andResult)
				return false;
		}
		return true;
	}
	
	
	public boolean calculateHash(boolean needComply)
	{
		hash = calculateHash(this, needComply);
		return (null == hash) ? false : true;
	}
	
	
	public boolean setPrevious(Block prev)
	{
		if (null == prev)
			return false;
		else
		{
			previousHash = prev.hash;
			return true;
		}
	}
	
	
	public boolean setTrans(List<Transaction> trans)
	{
		transactions = new LinkedList<Transaction>(trans);
		return true;
	}
	
	
	public void setNonce(int value)
	{
		nonce = value;
	}
	
	
	public boolean isValidHash(byte[] hash)
	{
		if (null == hash)
			return false;
		
		byte[] expectedHash = calculateHash(this, true);
		if (null == expectedHash)
			return false;
		
		if (expectedHash.length == hash.length)
		{
			for (int i = 0; i < expectedHash.length; ++i)
			{
				if (expectedHash[i] != hash[i])
					return false;
			}
		}
		else
			return false;
		
		return true;
	}
	
	
	public byte[] getHash()
	{
		byte[] result = null;
		if (null != hash)
		{
			result = new byte[hash.length];
			System.arraycopy(hash, 0, result, 0, hash.length);
		}
		return result;
	}
}
