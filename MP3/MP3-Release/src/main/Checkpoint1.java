package main;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import info.blockchain.api.blockexplorer.*;

public class Checkpoint1 {

	/**
	 * Blocks-Q1: What is the size of this block?
	 * 
	 * Hint: Use method getSize() in Block.java
	 * 
	 * @return size of the block
	 */
	BlockExplorer be = new BlockExplorer();
	public long getBlockSize() {
		// TODO implement me
		Block a = null;
		try{
			a = be.getBlock("000000000000000f5795bfe1de0381a44d4d5ea2ad81c21d77f275bffa03e8b3");	
		}
		catch (Exception e){

		}
		return a.getSize();
	}

	/**
	 * Blocks-Q2: What is the Hash of the previous block?
	 * 
	 * Hint: Use method getPreviousBlockHash() in Block.java
	 * 
	 * @return hash of the previous block
	 */
	public String getPrevHash() {
		// TODO implement me
		Block a = null;
		try{
			a = be.getBlock("000000000000000f5795bfe1de0381a44d4d5ea2ad81c21d77f275bffa03e8b3");	
		}
		catch (Exception e){

		}
		return a.getPreviousBlockHash();
	}

	/**
	 * Blocks-Q3: How many transactions are included in this block?
	 * 
	 * Hint: To get a list of transactions in a block, use method
	 * getTransactions() in Block.java
	 * 
	 * @return number of transactions in current block
	 */
	public int getTxCount() {
		Block a = null;
		try{
			a = be.getBlock("000000000000000f5795bfe1de0381a44d4d5ea2ad81c21d77f275bffa03e8b3");	
		}
		catch (Exception e){

		}
		List<Transaction> tx = a.getTransactions();
		return tx.size();
	}

	/**
	 * Transactions-Q1: Find the transaction with the most outputs, and list the
	 * Bitcoin addresses of all the outputs.
	 * 
	 * Hint: To get the bitcoin address of an Output object, use method
	 * getAddress() in Output.java
	 * 
	 * @return list of output addresses
	 */
	public List<String> getOutputAddresses() {
		Block a = null;
		try{
			a = be.getBlock("000000000000000f5795bfe1de0381a44d4d5ea2ad81c21d77f275bffa03e8b3");	
		}
		catch (Exception e){

		}
		List<Transaction> tx = a.getTransactions();
		int max = -1;
		List<Output> max_list = null;
		for (Transaction i : tx){
			List<Output> lo = i.getOutputs();	
			if (lo.size() > max)
				max_list = lo;
		}
		List<String> result = new ArrayList<String>();
		for (Output j : max_list){
			result.add(j.getAddress());
		}

		return result;
	}

	/**
	 * Transactions-Q2: Find the transaction with the most inputs, and list the
	 * Bitcoin addresses of the previous outputs linked with these inputs.
	 * 
	 * Hint: To get the previous output of an Input object, use method
	 * getPreviousOutput() in Input.java
	 * 
	 * @return list of input addresses
	 */
	public List<String> getInputAddresses() {
		Block a = null;
		try{
			a = be.getBlock("000000000000000f5795bfe1de0381a44d4d5ea2ad81c21d77f275bffa03e8b3");	
		}
		catch (Exception e){

		}
		List<Transaction> tx = a.getTransactions();
		int max = -1;
		List<Input> max_list = null;
		for (Transaction i : tx){
			List<Input> lo = i.getInputs();	
			if (lo.size() > max)
				max_list = lo;
		}
		List<String> result = new ArrayList<String>();
		for (Input j : max_list){
			result.add(j.getPreviousOutput().getAddress());
		}		

		return result;
	}

	/**
	 * Transactions-Q3: What is the bitcoin address that has received the
	 * largest amount of Satoshi in a single transaction?
	 * 
	 * Hint: To get the number of Satoshi received by an Output object, use
	 * method getValue() in Output.java
	 * 
	 * @return the bitcoin address that has received the largest amount of Satoshi
	 */
	public String getLargestRcv() {
		// TODO implement me
		Block a = null;
		try{
			a = be.getBlock("000000000000000f5795bfe1de0381a44d4d5ea2ad81c21d77f275bffa03e8b3");	
		}
		catch (Exception e){

		}
		List<Transaction> tx = a.getTransactions();
		int max = -1;
		Long sum_max = new Long(0);
		String max_address = "";
		
		for (Transaction i : tx){
			Map<String, Long> lm = new HashMap<String, Long>();
			List<Output> ou = i.getOutputs();
			for (Output j : ou){
				Long val = lm.get(j.getAddress());
				if (val == null)
					lm.put(j.getAddress(), j.getValue());	
				else
					lm.put(j.getAddress(), j.getValue() + val);
			}
			for (Map.Entry<String,Long> k : lm.entrySet()){
				if (k.getValue() > sum_max){
					max_address = k.getKey();
					sum_max = k.getValue();
				}
			}
		}
		return max_address;
	}

	/**
	 * Transactions-Q4: How many coinbase transactions are there in this block?
	 * 
	 * Hint: In a coinbase transaction, getPreviousOutput() == null
	 * 
	 * @return number of coin base transactions
	 */
	public int getCoinbaseCount() {
		Block a = null;
		try{
			a = be.getBlock("000000000000000f5795bfe1de0381a44d4d5ea2ad81c21d77f275bffa03e8b3");	
		}
		catch (Exception e){

		}
		int coinBases = 0;
		List<Transaction> tx = a.getTransactions();
		for(Transaction i: tx){
			List<Input> inputs = i.getInputs();
			for(Input k: inputs){
				if(k.getPreviousOutput() == null)
					coinBases +=1;
			}
		}
		return coinBases;
	}

	/**
	 * Transactions-Q5: What is the number of Satoshi generated in this block?
	 * 
	 * @return number of Satoshi generated
	 */
	public long getSatoshiGen() {
		Block a = null;
		try{
			a = be.getBlock("000000000000000f5795bfe1de0381a44d4d5ea2ad81c21d77f275bffa03e8b3");	
		}
		catch (Exception e){

		}
		long satoshiGenerated = 0;
		List<Transaction> tx = a.getTransactions();
		for(Transaction i: tx){
			List<Output> outputs = i.getOutputs();
			List<Input> inputs	= i.getInputs();
			satoshiGenerated += (outputs.size() - inputs.size());	//maybe more complicated, not sure yet
		}
	
		
		return satoshiGenerated;
	}

}
