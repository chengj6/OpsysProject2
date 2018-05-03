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
		while ( (st = br.readLine()) != null) {
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
		next_fit(processes, memory, null);
		//non_contiguous(processes, memory, null);
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
	
	private static int defragment(ArrayList<Character> memory, ArrayList<Process> activeProc){
		int framesMoved = 0;
		for (int i = 0; i < Max_Mem_Frames; i++) {
			if (memory.get(i) != '.') {
				int currentMemSpace = 0;
				for (int k = 0; k < activeProc.size(); k++) {	//Check which process we are dealing with
					if (activeProc.get(k).getID().charAt(0) == memory.get(i)) {
						currentMemSpace = activeProc.get(k).getMemFrames();
						break;
					}
				}
				
				int freeSpace = 0;
				for (int j = i; j > 0; j--) {
					//System.out.println(memory.get(j-1)+":::"+j);
					if (memory.get(j-1) == '.')
						freeSpace++;
					else {
						memory.subList(i-freeSpace, i).clear();
						ArrayList<Character> addFreeSpace = new ArrayList<Character>(Collections.nCopies(freeSpace, '.'));
						memory.addAll(i-freeSpace+currentMemSpace, addFreeSpace);
						if (freeSpace > 0)
							framesMoved+=currentMemSpace;
						i = i-freeSpace+currentMemSpace;
						freeSpace = 0;
						break;
					}
					if (j == 1) {
						memory.subList(i-freeSpace, i).clear();
						ArrayList<Character> addFreeSpace = new ArrayList<Character>(Collections.nCopies(freeSpace, '.'));
						memory.addAll(i-freeSpace+currentMemSpace, addFreeSpace);
						if (freeSpace > 0)
							framesMoved+=currentMemSpace;
						i = i-freeSpace+currentMemSpace;
						freeSpace = 0;
						break;
					}
				}
			}
		}
		return framesMoved;
	}
	
	private static void addNextFit(Process process, ArrayList<Character> memory, ArrayList<Process> activeProc, ArrayList<Process> processes){
		int spaceNeeded = process.getMemFrames();
		int freeSpace = 0;
		int totalFreeSpace = 0;
		int loop = 0;
		if (justPlaced == Max_Mem_Frames)
			justPlaced = 255;
		for (int i = 0; i < Max_Mem_Frames; i++) {
			if (memory.get(i) == '.')
				totalFreeSpace++;
		}
		for (int i = justPlaced; i < Max_Mem_Frames; i++) {
			if (memory.get(i) == '.') {
				freeSpace++;
				if(freeSpace >= spaceNeeded && loop == 0) {
					int x = 0;
					for (int j = i-freeSpace+1; x < process.getMemFrames(); j++) {
						memory.set(j, process.getID().charAt(0));
						x++;
					}
					
					justPlaced = i-freeSpace+spaceNeeded;
				
					System.out.println("time "+time+"ms: Placed process "+process.getID());
					printMemory(memory);
					return;
				}
				else if (freeSpace >= spaceNeeded && loop == 1) {
					int k = 0;
					for (int l = 0; l < memory.size(); l++) {
						if (memory.get(l) == '.') {
							k = l;
							break;
						}
					}
					
					for (int j = k; j < k+spaceNeeded; j++) {
						memory.set(j, process.getID().charAt(0));
					}
					
					justPlaced = k+spaceNeeded;
					System.out.println("time "+time+"ms: Placed process "+process.getID());
					printMemory(memory);
					return;
				}
			}
			else {
				freeSpace = 0;
			}
			
			if (i == Max_Mem_Frames-1 && loop == 0) {
				i = -1;
				loop = 1;
				freeSpace = 0;
			}
		}
		System.out.println(totalFreeSpace);
		if (totalFreeSpace < spaceNeeded) {
			System.out.println("time "+time+"ms: Cannot place process "+process.getID()+" -- skipped!");
			process.incrementBLA();
			return;
		}
		//defragment and increment times
		System.out.println("time "+time+"ms: Cannot place process "+process.getID()+" -- starting defragmentation");
		int framesMoved = defragment(memory, activeProc);
		time+=framesMoved;
		for (int i = 0; i < processes.size(); i++) {
			processes.get(i).incrementArrivalTimes(framesMoved);
		}
		System.out.print("time "+time+"ms: Defragmentation complete (moved "+framesMoved+" frames: ");
		for (int i = 0; i < activeProc.size()-1; i++) {
			System.out.print(activeProc.get(i).getID().charAt(0)+", ");
		}
		System.out.println(activeProc.get(activeProc.size()-1).getID().charAt(0)+")");
		
		// move ptr after defragmentation
		for (int i = 0; i < memory.size(); i++) {
			if (memory.get(i) == '.') {
				justPlaced = i;
				break;
			}
		}
		
		//try again to place
		printMemory(memory);
		for (int i = justPlaced; i < justPlaced+spaceNeeded; i++) {
			memory.set(i, process.getID().charAt(0));
		}
		System.out.println("time "+time+"ms: Placed process "+process.getID());
		justPlaced += spaceNeeded;
		printMemory(memory);
		
	}
	
	public static void arrival(ArrayList<Process> processes, ArrayList<Character> memory, ArrayList<Process> activeProc) {
		for(int i=0; i<processes.size();i++) {
			ArrayList<Integer> arrTimes = processes.get(i).getArrTimes();
			int currentBurst = processes.get(i).getBLA();
			if (currentBurst<arrTimes.size() && arrTimes.get(currentBurst) == time) {
				activeProc.add(processes.get(i));
				System.out.println("time "+time+"ms: Process "+processes.get(i).getID()+" arrived (requires "+processes.get(i).getMemFrames()+" frames)");
				addNextFit(processes.get(i), memory, activeProc, processes);
				//printMemory(memory);
			}
		}
	}
	
	public static void remove (Process process, ArrayList<Character> memory) {
		int once = 0;
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
			if(currentBurst<arrTimes.size() && time == arrTimes.get(currentBurst)+runTimes.get(currentBurst)){
				System.out.println("time "+time+"ms: Process "+ activeProc.get(i).getID()+" removed:");
				activeProc.get(i).incrementBLA(); //need to add way to make sure this doesn't go out of bounds
				remove(activeProc.get(i), memory);
				activeProc.remove(activeProc.get(i));
				i--;
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
			time++;
			int n = 0;
			for (int i = 0; i < processes.size(); i++) {
				if (processes.get(i).getBLA() == processes.get(i).getArrTimes().size())
					n++;
			}
			if (n == processes.size())
				break;
		}
		
	}

	private static void best_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}
	
	private static void worst_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		
	}
	
	public static void addNonContiguous(Process p, ArrayList<Character> memory, ArrayList<Process> activeProc, ArrayList<Process> processes, ArrayList<ArrayList<Pair>> pTable, int time){
		int fNeeded = p.getMemFrames();
		int freeFrames = 0;
		int startOfFreeFrames = -1;
		for(int i=0; i<Max_Mem_Frames; i++) {
			if(memory.get(i)=='.') {
				startOfFreeFrames = i;
				break;
			}
		}
		for(int i=0;i<Max_Mem_Frames;i++) {
			if(memory.get(i)=='.') {
				freeFrames++;
			}
		}
		if(freeFrames<fNeeded) {
			System.out.println("time "+time+"ms: Cannot place process "+p.getID()+" -- skipped!");
			return;
		}
		
	}
	
	public static void NonContiguousArrival(ArrayList<Process> processes, ArrayList<Character> memory, ArrayList<Process> activeProc, ArrayList<ArrayList<Pair>> pTable, int time) {
		for(int i=0; i<processes.size();i++) {
			ArrayList<Integer> arrTimes = processes.get(i).getArrTimes();
			int currentBurst = processes.get(i).getBLA();
			if (currentBurst<arrTimes.size() && arrTimes.get(currentBurst) == time) {
				activeProc.add(processes.get(i));
				System.out.println("time "+time+"ms: Process "+processes.get(i).getID()+" arrived (requires "+processes.get(i).getMemFrames()+" frames)");
				addNonContiguous(processes.get(i), memory, activeProc, processes, pTable, time);
				//printMemory(memory);
			}
		}
	}
	
	public static void NonContiguousRemoval(ArrayList<Process> activeProc, ArrayList<Character> memory, ArrayList<ArrayList<Pair>> pTable, int time) {
		for(int i=0;i<activeProc.size();i++) {
			ArrayList<Integer> arrTimes = activeProc.get(i).getArrTimes();
			ArrayList<Integer> runTimes = activeProc.get(i).getRTimes();
			int currentBurst = activeProc.get(i).getBLA();
			if(currentBurst<arrTimes.size() && time == arrTimes.get(currentBurst)+runTimes.get(currentBurst)){
				System.out.println("time "+time+"ms: Process "+ activeProc.get(i).getID()+" removed");
				activeProc.get(i).incrementBLA(); //need to add way to make sure this doesn't go out of bounds
				remove(activeProc.get(i), memory);
				activeProc.remove(activeProc.get(i));
				i--;
				printMemory(memory);
			}
		}
	}
	
	private static void non_contiguous(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		ArrayList<ArrayList<Pair>> pTable = new ArrayList<ArrayList<Pair>>();
		ArrayList<Process> activeProcesses = new ArrayList<Process>();
		int time =0;
		while(true) {
			NonContiguousArrival(processes, memory, activeProcesses , pTable, time);
			NonContiguousRemoval(activeProcesses, memory, pTable, time);
			time++;
		}
	}
}