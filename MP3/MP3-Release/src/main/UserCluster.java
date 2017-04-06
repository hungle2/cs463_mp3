package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserCluster {
	private Map<Long, List<String>> userMap; // Map a user id to a list of
												// bitcoin addresses
	private Map<String, Long> keyMap; // Map a bitcoin address to a user id
	private Map<Long, List<String>> tempMap;
	public UserCluster() {
		userMap = new HashMap<Long, List<String>>();
		keyMap = new HashMap<String, Long>();
	}

	/**
	 * Read transactions from file
	 *
	 * @param file
	 * @return true if read succeeds; false otherwise
	 */
	public boolean readTransactions(String file) {
		BufferedReader bw = null;
		try {
			bw = new BufferedReader(new FileReader(file));
		} catch (IOException x){
			System.err.format("IOException");
			return false;
		}
		String temp = "";
		while (true){
			try {
				temp = bw.readLine();
				if (temp == null)
					break;
				String[] split_str = temp.split(" ");
				if (split_str[4].equals("in"))
					// add in address to the transaction map
          addAddress(split_str[2],Long.parseLong(split_str[0]));
			} catch (IOException x){
				System.err.format("IOException");
				return false;
			}
		}

		return true;
	}


	public void addAddress(String addr, long id){
		/*
    Long dict_id = keyMap.get(addr);
    //Add to keyMap

		if (dict_id == null){
			keyMap.put(addr, id);
			dict_id = id;
		}
	  */
		List<String> dict_addr = tempMap.get(id);
    if (dict_addr == null){
      dict_addr = new ArrayList<String>();
    }
	  dict_addr.add(addr);
    tempMap.put(id,dict_addr);
		return;
	}

	/**
	 * Merge addresses based on joint control
	 */
	public void mergeAddresses() {
    Long user_count = 0L;
    for (Map.Entry<Long, List<String>> entry : tempMap.entrySet()) {
      ArrayList<Long> users = new ArrayList<Long>();
      for (String address : entry.getValue()) {
        if (keyMap.containsKey(address)) {
          long user_id = keyMap.get(address);
          if (!users.contains(user_id)) {
          	users.add(user_id);
          }
        }
      }
      if (users.isEmpty()) {
        // create new user
        userMap.put(user_count, new ArrayList<String>());
        for (String address : entry.getValue()) {
			List<String> tmp = userMap.get(user_count);
			tmp.add(address);
        	userMap.put(user_count, tmp);
          keyMap.put(address, user_count);
        }
        user_count++;
      } else if (users.size() == 1) {
        for (String address : entry.getValue()) {
		  List<String> tmp = userMap.get(users.get(0));
  		  tmp.add(address);
  		  userMap.put(users.get(0), tmp);
          if (!keyMap.containsKey(address)) {
            keyMap.put(address, users.get(0));
          }
        }
      } else {
      	mergeIDs(users, entry.getValue());
      }
    }
	}

  public void mergeIDs(ArrayList<Long> users, List<String> addresses){
    Long new_user_id = users.get(0);
    for (Long user : users) {
      if (user == new_user_id) {
        continue;
      }
      for (String address : userMap.get(user)) {
		List<String> tmp = userMap.get(new_user_id);
		tmp.add(address);
        userMap.put(new_user_id, tmp);
        keyMap.put(address, new_user_id);
      }
      userMap.remove(user);
    }
    for (String address : addresses) {
      if (!userMap.get(new_user_id).contains(address)) {
		List<String> tmp = userMap.get(new_user_id);
  		tmp.add(address);
        userMap.put(new_user_id, tmp);
        keyMap.put(address, new_user_id);
      }
    }
  }

	/**
	 * Return number of users (i.e., clusters) in the transaction dataset
	 *
	 * @return number of users (i.e., clusters)
	 */
	public int getUserNumber() {
		return userMap.size();
	}

	/**
	 * Return the largest cluster size
	 *
	 * @return size of the largest cluster
	 */
	public int getLargestClusterSize() {
		int max = 0;
		for (List<String> i : userMap.values() ){
			if (max < i.size())
				max = i.size();
		}
		return max;
	}

	public boolean writeUserMap(String file) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			for (long user : userMap.keySet()) {
				List<String> keys = userMap.get(user);
				w.write(user + " ");
				for (String k : keys) {
					w.write(k + " ");
				}
				w.newLine();
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			System.err.println("Error in writing user list!");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean writeKeyMap(String file) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			for (String key : keyMap.keySet()) {
				w.write(key + " " + keyMap.get(key) + "\n");
				w.newLine();
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			System.err.println("Error in writing key map!");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean writeUserGraph(String txFile, String userGraphFile) {
		try {
			BufferedReader r1 = new BufferedReader(new FileReader(txFile));
			Map<String, Long> txUserMap = new HashMap<String, Long>();
			String nextLine;
			while ((nextLine = r1.readLine()) != null) {
				String[] s = nextLine.split(" ");
				if (s.length < 5) {
					System.err.println("Invalid format: " + nextLine);
					r1.close();
					return false;
				}
				if (s[4].equals("in") && !txUserMap.containsKey(s[0])) { // new transaction
					Long user;
					if ((user=keyMap.get(s[2])) == null) {
						System.err.println(s[2] + " is not in the key map!");
						r1.close();
						return false;
					}
					txUserMap.put(s[0], user);
				}
			}
			r1.close();

			BufferedReader r2 = new BufferedReader(new FileReader(txFile));
			BufferedWriter w = new BufferedWriter(new FileWriter(userGraphFile));
			while ((nextLine = r2.readLine()) != null) {
				String[] s = nextLine.split(" ");
				if (s.length < 5) {
					System.err.println("Invalid format: " + nextLine);
					r2.close();
					w.flush();
					w.close();
					return false;
				}
				if (s[4].equals("out")) {
					if(txUserMap.get(s[0]) == null) {
						System.err.println("Did not find input transaction for Tx: " + s[0]);
						r2.close();
						w.flush();
						w.close();
						return false;
					}
					long inputUser = txUserMap.get(s[0]);
					Long outputUser;
					if ((outputUser=keyMap.get(s[2])) == null) {
						System.err.println(s[2] + " is not in the key map!");
						r2.close();
						w.flush();
						w.close();
						return false;
					}
					w.write(inputUser + "," + outputUser + "," + s[3] + "\n");
				}
			}
			r2.close();
			w.flush();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}
