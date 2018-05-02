// *********************************************************************************
//	Shashank Sundar: sundas6
//	Jonathan Cheng: chengj6
//	Kris Whelan: whelak2
// *********************************************************************************

import java.io.*;
import java.util.*;

public class Project2 {
	
	static int justPlaced;
	static int time;
	
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
		ArrayList<Character> memory = new ArrayList<Character>(Collections.nCopies(Max_Mem_Frames, '.'));
		
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
			oldMark = newMark+1;
			newMark = st.indexOf(' ', oldMark);	
			int memFrames = Integer.parseInt(st.substring(oldMark, newMark));
			while (true) {
				oldMark = newMark+1;
				newMark = st.indexOf('/', oldMark);
				arrTimes.add(Integer.parseInt(st.substring(oldMark, newMark)));
				
				oldMark = newMark+1;
				newMark = st.indexOf(' ', oldMark);
				if(newMark==-1) {
					runTimes.add(Integer.parseInt(st.substring(oldMark)));
					processes.add(new Process(id, memFrames, arrTimes, runTimes));
					break;
				}
				runTimes.add(Integer.parseInt(st.substring(oldMark, newMark)));
			}
		}
		
	}
	
	private static void printMemory(ArrayList<Character> memory) {
		System.out.println("================================");
		for(int i=0; i<8;i++) {
			String str;
			for(int j=32*i;j<(i+1)*32;j++) {
				System.out.print(memory.get(j));
			}
			System.out.println("");
		}	
		System.out.println("================================");
	}
	
	private static void defragment(ArrayList<Character> memory){
		
	}
	
	public static void arrival(ArrayList<Process> processes, ArrayList<Character> memory, ArrayList<Process> activeProc) {
		for(int i=0; i<processes.size();i++) {
			ArrayList<Integer> arrTimes = processes.get(i).getArrTimes();
			int currentBurst = processes.get(i).getBLA();
			
			if (arrTimes.get(currentBurst) == time) {
				activeProc.add(processes.get(i));
				System.out.println("time "+time+"ms: Process "+processes.get(i).getID()+" arrived (required "+processes.get(i).getMemFrames()+" frames)");
				//addNextFit(processes.get(i), memory);
				printMemory(memory);
			}
		}
	}
	
	public static void remove (Process process, ArrayList<Character> memory) {
		for(int i=0;i<Max_Mem_Frames;i++) {
			if (memory.get(i).equals(process.getID().charAt(0))){
				memory.set(i, '.');
			}
		}
	}
	
	public static void removal(ArrayList<Process> activeProc, ArrayList<Character> memory) {
		for(int i=0;i<activeProc.size();i++) {
			ArrayList<Integer> arrTimes = activeProc.get(i).getArrTimes();
			ArrayList<Integer> runTimes = activeProc.get(i).getRTimes();
			int currentBurst = activeProc.get(i).getBLA();
			if(time == arrTimes.get(currentBurst)+runTimes.get(currentBurst)){
				System.out.println("time "+time+"ms: Process "+ activeProc.get(i).getID()+" removed");
				activeProc.get(i).incrementBLA(); //need to add way to make sure this doesn't go out of bounds
				remove(activeProc.get(i), memory);
				printMemory(memory);
			}
		}
	}
	
	private static void next_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		ArrayList<Process> activeProcesses = new ArrayList<Process>();
		justPlaced = 0;
		time = 0;
		
		while (true) {
			arrival(processes, memory, activeProcesses);
			removal(activeProcesses, memory);
		}
		
	}

	private static void best_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}
	
	private static void worst_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}
	
	private static void non_contiguous(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}
}