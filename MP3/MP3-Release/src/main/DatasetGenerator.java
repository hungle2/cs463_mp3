package main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import info.blockchain.api.blockexplorer.*;
import java.util.List;
import java.util.ArrayList;


public class DatasetGenerator {
	String file;

	public DatasetGenerator(String file) {
		this.file = file;
	}

	public boolean writeTransactions() {

		//long tx = getIndex()

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
		} catch (IOException x){
			System.err.format("IOException");
			return false;
		}


		BlockExplorer be = new BlockExplorer();

		List<Block> lb = new ArrayList<Block>();
		
		try{
			for (long i = 265852; i <= 266085; i++)
				lb.addAll(be.getBlocksAtHeight(i));
		}
		catch (Exception e){
			System.err.format("Error fetching blocks!\n");
		}
		for (Block j : lb){
			List<Transaction> t = j.getTransactions();

			for (Transaction i : t){
				boolean isBase = false;
				List<Input> in   = i.getInputs();
				List<Output> out = i.getOutputs();
				String hash = i.getHash();
				long idx = i.getIndex();

				for (Input k : in){
					if (k.getPreviousOutput() == null && in.size() == 1){
						isBase = true;
						break;
					}

				}
				if (isBase == true)
					continue;

				try {
					for (Input k : in){
						if (k.getPreviousOutput() == null)
							break;
						bw.write(generateInputRecord(idx, hash, k.getPreviousOutput().getAddress(), k.getPreviousOutput().getValue()));
						bw.newLine();
					}
					for (Output l : out){
						bw.write(generateOutputRecord(idx, hash, l.getAddress(), l.getValue()));
						bw.newLine();
					}
				} catch (IOException x){
					System.err.format("IOException");
					return false;
				}
			}
		}
		/**/
		try {
			bw.close();
		}
		catch (IOException ex){
			System.err.format("IOException");
			return false;
		}
		return true;
	}

	/**
	 * Generate a record in the transaction dataset
	 *
	 * @param txIndex
	 *            Transaction index
	 * @param txHash
	 *            Transaction hash
	 * @param address
	 *            Previous output address of the input
	 * @param value
	 *            Number of Satoshi transferred
	 * @return A record of the input
	 */
	private String generateInputRecord(long txIndex, String txHash,
			String address, long value) {
		return txIndex + " " + txHash + " " + address + " " + value + " in";
	}

	/**
	 * Generate a record in the transaction dataset
	 *
	 * @param txIndex
	 *            Transaction index
	 * @param txHash
	 *            Transaction hash
	 * @param address
	 *            Output bitcoin address
	 * @param value
	 *            Number of Satoshi transferred
	 * @return A record of the output
	 */
	private String generateOutputRecord(long txIndex, String txHash,
			String address, long value) {
		return txIndex + " " + txHash + " " + address + " " + value + " out";
	}

}
