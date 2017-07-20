package cis262ec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
	
	
	public static void main(String[] args) {
		
		Algorithms a = new Algorithms();
		a.readDFAInput();
		a.readQueryInput();
		
		String partitions = "";
		String tests = "";
		
		for (int i = 0; i < a.queries.size(); i++) {
			a.initPartition();
			String[] output = a.unif(a.queries.get(i)[0], a.queries.get(i)[1]);
			if (partitions.isEmpty()) {
				partitions = output[0];
			} else {
				partitions += "\n\n" + output[0];
			}
			
			if (tests.isEmpty()) {
				tests = output[1];
			} else {
				tests += "\n" + output[1];
			}
		}
		
		try{
			Writer out = new BufferedWriter(new FileWriter("Partition.txt"));
			out.write(partitions);
			out.flush();
			out.close();
		} catch (IOException e){
			System.out.println("error with writing to Partition.txt");
		}
		
		try{
			Writer out1 = new BufferedWriter(new FileWriter("Test.txt"));
			out1.write(tests);
			out1.flush();
			out1.close();
		} catch (IOException e){
			System.out.println("error with writing to Test.txt");
		}

	}
	
}
