package cis262ec;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


public class Algorithms {
	private Set<String> finalStates;
	private List<ArrayList<String>> DFA;
	public List<String[]> queries;
	private List<Integer[]> partition;
	
	
	public void initPartition() {
		//create partition - initialized to identity relation on the states of the DFA
		partition = new ArrayList<Integer[]>();
		for (int i = 1; i <= DFA.size(); i++) {
			Integer[] arr = {0, 1};
			partition.add(arr);
		}
	}

	/**
	 * reads the input of DFA.txt and initializes DFA and finalStates
	 */
	public void readDFAInput() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("DFA.txt"));
			DFA = new ArrayList<ArrayList<String>>();
			finalStates = new HashSet<String>(); 
			String line = in.readLine();
			while (line != null) {
				String arrParts[] = line.split(" ");
				if (!arrParts[0].equals("F")) {
					//create an ArrayList and add each of the parts of the line to it
					ArrayList<String> L = new ArrayList<String>();
					for (int i = 0; i < arrParts.length; i++) {
						L.add(arrParts[i]);
					}
					DFA.add(L);
				} else {
					//add the final states into a set
					for (int i = 1; i < arrParts.length; i++) {
						finalStates.add(arrParts[i]);
					}
				}
				line = in.readLine();
			}
			in.close();
			
			initPartition();
			
		} catch (FileNotFoundException e) {
			System.out.println("DFA.txt not found!");
		} catch (IOException e) {
			System.out.println("An error occurred while reading DFA.txt");
		} catch (Exception e) {
			System.out.println("Something went wrong when reading DFA.txt");
		}
		
	}
	
	
	
	public void readQueryInput() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("Query.txt"));
			queries = new ArrayList<String[]>();
			String line = in.readLine();
			while (line != null) {
				String arrParts[] = line.split(" ");
				
				if (arrParts.length == 2) {
					queries.add(arrParts);
				} else {
					throw new IllegalArgumentException();
				}
				
				line = in.readLine();
			}
			in.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("Query.txt not found!");
		} catch (IOException e) {
			System.out.println("An error occurred while reading Query.txt");
		} catch (Exception e) {
			System.out.println("Something went wrong when reading Query.txt");
		}
	}
	
	/**
	 * finds the root of the tree containing the Node with name as a node 
	 * @param name The name of the node which is being looked for
	 * @return the Node at the root of the tree containing the node with name
	 */
	public Integer find(Integer name) {
		Integer[] part = partition.get(name-1);
		if (part[0] == 0) {
			return name;
		} else {
			int x = find(part[0]);
			part[0] = x;
			partition.set(name-1, part);
			return x;
		}
	}
	
	/**
	 *  forms a new partition by merging the two trees with roots p and q as follows: 
	 *  if the counter of p is smaller than that of q, then let the root of p point to q
	 *  else let the root of q point to p.
	 * @param n1 the name of the first node
	 * @param n2 the name of the second node
	 */
	public void union(Integer n1, Integer n2) {
		Integer[] node1 = partition.get(n1-1);
		Integer[] node2 = partition.get(n2-1);
		
		if (node1[1] < node2[1]) {
			node1[0] = n2;
			node2[1] += node1[1];
		} else {
			node2[0] = n1; 
			node1[1] += node2[1];
		}
		
		partition.set(n1-1, node1);
		partition.set(n2-1, node2);
	}
	
	/**
	 * a pair p, q is bad iff either both p is in F and q is not in F, or both p is not in F and q is in F
	 * @param n1 name of first state
	 * @param n2 name of second state
	 * @return true if exactly one of the states is in F, false otherwise
	 */
	public boolean bad(String n1, String n2) {
		return ((finalStates.contains(n1) && !finalStates.contains(n2)) 
				|| (!finalStates.contains(n1) && finalStates.contains(n2)));
		}
	
	public String delta(List<ArrayList<String>> trans, int i, int j) {
		return trans.get(i-1).get(j-1);
	}
	
	public String[] unif(String n1, String n2) {
		List<ArrayList<String>> trans = DFA;
		Stack<String[]> st = new Stack<String[]>();
		st.push(new String[] {n1, n2});
		boolean flag = true;
		int k = trans.get(0).size();
		String[] output = new String[2];
		String partition = this.toString();
		String test = "";
		
		while (!st.isEmpty() && flag) {
			String[] uv = st.pop();
			Integer uu = Integer.parseInt(uv[0]); 
			Integer vv = Integer.parseInt(uv[1]);
			if (bad(uu.toString(), vv.toString())) {
				flag = false;
				if (test.isEmpty()) {
					test += uu + " " + vv;
				} else {
					test += "\n" + uu + " " + vv;
				}
			} else {
				Integer u = find(uu);
				Integer v = find(vv);
				if (u != v) {
					union(u, v);
					for (int i = 1; i <= k; i++) {
						String u1 = delta(trans, uu, k - i + 1);
						String v1 = delta(trans, vv, k - i + 1);
						uv = new String[] {u1, v1};
						st.push(uv);
					}
				}
			}
			
			partition = partition + "\n" + this.toString();
		}
		
		if (flag) {
			if (test.isEmpty()) {
				test += "G";
			} else {
				test += "\n" + "G";
			}
		}
		
		//first index in output holds string for printing to Partition.txt, second is for Test.txt
		output[0] = partition;
		output[1] = test;
		
		return output;
	}

	@Override
	public String toString() {
		Map<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
		for (int i = 0; i < partition.size(); i++) {
			Integer p = find(i+1);
			ArrayList<Integer> arr;
			if (map.containsKey(p)) {
				arr = map.get(p);
			} else {
				arr = new ArrayList<Integer>();
			}
			arr.add(i+1);
			map.put(p, arr);
			
		}
		
		String str = "";
		for (Integer x : map.keySet()) {
			for (Integer a : map.get(x)) {
				if (str.isEmpty()) {
					str += a;
				} else {
					str += " " + a;
				}
			}
			str += ";"; 
		}
		str = str.substring(0, str.length()-1);
		return str;
	}
	
}
