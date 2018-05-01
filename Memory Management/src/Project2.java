// *********************************************************************************
//	Shashank Sundar: sundas6
//	Jonathan Cheng: chengj6
//	Kris Whelan: whelak2
// *********************************************************************************

import java.io.*;
import java.util.ArrayList;

public class Project2 {
	
	final static int Max_Mem_Frames = 256;

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.err.println("ERROR: Invalid arguments\nUSAGE: ./a.out <input-file>");
		}

		//File Read
		File file = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(file));

		if (!file.exists()) {
			System.err.println("ERROR: Invalid input file format");
		}
		
		ArrayList<Process> processes = new ArrayList<Process>();
		ArrayList<Character> memory = new ArrayList<Character>(Max_Mem_Frames);
		
		String st;
		while ((st = br.readLine()) != null) {
			if (st.equals(""))
				continue;
			int newMark = 0;
			int oldMark = 0;
			ArrayList<Integer> arrTimes = new ArrayList<Integer>();
			ArrayList<Integer> runTimes = new ArrayList<Integer>();
			newMark = st.indexOf(' ');
			String id = st.substring(oldMark, newMark);
			//System.out.println(id);
			oldMark = newMark+1;
			newMark = st.indexOf(' ', oldMark);	
			int memFrames = Integer.parseInt(st.substring(oldMark, newMark));
			
			oldMark = newMark+1;
			newMark = st.indexOf('/', oldMark);
			arrTimes.add(Integer.parseInt(st.substring(oldMark, newMark)));
			
			oldMark = newMark+1;
			newMark = st.indexOf(' ', oldMark);
			runTimes.add(Integer.parseInt(st.substring(oldMark, newMark)));
			
			oldMark = newMark+1;
			newMark = st.indexOf('/', oldMark);
			if(newMark==-1) {
				processes.add(new Process(id, memFrames, arrTimes, runTimes));
				continue;
			}
			arrTimes.add(Integer.parseInt(st.substring(oldMark, newMark)));
			
			oldMark = newMark+1;
			newMark = st.indexOf(' ', oldMark);
			runTimes.add(Integer.parseInt(st.substring(oldMark, newMark)));
			
			oldMark = newMark+1;
			newMark = st.indexOf('/', oldMark);
			if(newMark==-1) {
				processes.add(new Process(id, memFrames, arrTimes, runTimes));
				continue;
			}
			arrTimes.add(Integer.parseInt(st.substring(oldMark, newMark)));
			
			oldMark = newMark+1;
			newMark = st.indexOf(' ', oldMark);
			runTimes.add(Integer.parseInt(st.substring(oldMark, newMark)));
			
			
			processes.add(new Process(id, memFrames, arrTimes, runTimes));
		}
		
		
		
	}
	
	private static void defragment(ArrayList<Character> memory){
		
	}
	
	private static void next_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}

	private static void best_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}
	
	private static void worst_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}
	
	private static void non_contiguous(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}
}