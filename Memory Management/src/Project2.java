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
		//next_fit(processes, memory, null);
		//best_fit(processes, memory, null);
//		worst_fit(processes, memory, null);
		non_contiguous(processes, memory, null);
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
	
	private static int defragment(ArrayList<Character> memory, ArrayList<Process> activeProc, ArrayList<Character> processesDealtWith){
		int framesMoved = 0;
		for (int i = 0; i < Max_Mem_Frames; i++) {
			if (memory.get(i) != '.') {
				int currentMemSpace = 0;
				char currentID = ' ';
				for (int k = 0; k < activeProc.size(); k++) {	//Check which process we are dealing with
					if (activeProc.get(k).getID().charAt(0) == memory.get(i)) {
						//processesDealtWith.add(activeProc.get(k).getID().charAt(0));
						currentMemSpace = activeProc.get(k).getMemFrames();
						currentID = activeProc.get(k).getID().charAt(0);
						break;
					}
				}
				
				int freeSpace = 0;
				for (int j = i; j > 0; j--) {
					if (memory.get(j-1) == '.')
						freeSpace++;
					else {
						memory.subList(i-freeSpace, i).clear();
						ArrayList<Character> addFreeSpace = new ArrayList<Character>(Collections.nCopies(freeSpace, '.'));
						memory.addAll(i-freeSpace+currentMemSpace, addFreeSpace);
						if (freeSpace > 0) {
							framesMoved+=currentMemSpace;
							processesDealtWith.add(currentID);
						}
						i = i-freeSpace+currentMemSpace;
						
						freeSpace = 0;
						break;
					}
					if (j == 1) {
						memory.subList(i-freeSpace, i).clear();
						ArrayList<Character> addFreeSpace = new ArrayList<Character>(Collections.nCopies(freeSpace, '.'));
						memory.addAll(i-freeSpace+currentMemSpace, addFreeSpace);
						if (freeSpace > 0) {
							framesMoved+=currentMemSpace;
							processesDealtWith.add(currentID);
						}
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
				
					System.out.println("time "+time+"ms: Placed process "+process.getID()+":");
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
					System.out.println("time "+time+"ms: Placed process "+process.getID()+":");
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
		if (totalFreeSpace < spaceNeeded) {
			System.out.println("time "+time+"ms: Cannot place process "+process.getID()+" -- skipped!");
			process.incrementBLA();
			return;
		}
		//defragment and increment times
		System.out.println("time "+time+"ms: Cannot place process "+process.getID()+" -- starting defragmentation");
		ArrayList<Character> processesDealtWith = new ArrayList<>();
		int framesMoved = defragment(memory, activeProc, processesDealtWith);
		time+=framesMoved;
		for (int i = 0; i < processes.size(); i++) {
			processes.get(i).incrementArrivalTimes(framesMoved);
		}
		System.out.print("time "+time+"ms: Defragmentation complete (moved "+framesMoved+" frames: ");
		for (int i = 0; i < processesDealtWith.size()-1; i++) {
			System.out.print(processesDealtWith.get(i)+", ");
		}
		System.out.println(processesDealtWith.get(processesDealtWith.size()-1)+")");
		
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
		System.out.println("time "+time+"ms: Placed process "+process.getID()+":");
		justPlaced += spaceNeeded;
		printMemory(memory);
		
	}
	
	public static void arrivalNF(ArrayList<Process> processes, ArrayList<Character> memory, ArrayList<Process> activeProc) {
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
	
	public static void remove(Process process, ArrayList<Character> memory) {
		int once = 0;
		for(int i=0;i<Max_Mem_Frames;i++) {
			if (memory.get(i).equals(process.getID().charAt(0))){
				memory.set(i, '.');
			}
		}

	}
	
	public static void removal(ArrayList<Process> activeProc, ArrayList<Character> memory) {
		Collections.sort(activeProc);
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
		System.out.println("time "+time+"ms: Simulator started (Contiguous -- Next-Fit)");
		ArrayList<Process> activeProcesses = new ArrayList<Process>();
		justPlaced = 0;
		time = 0;
		
		while (true) {
			removal(activeProcesses, memory);
			arrivalNF(processes, memory, activeProcesses);
			time++;
			int n = 0;
			for (int i = 0; i < processes.size(); i++) {
				if (processes.get(i).getBLA() == processes.get(i).getArrTimes().size())
					n++;
			}
			if (n == processes.size())
				break;
		}
		System.out.print("time "+(time-1)+"ms: Simulator ended (Contiguous -- Next-Fit)");
	}
	
	public static void arrivalBF(ArrayList<Process> processes, ArrayList<Character> memory, ArrayList<Process> activeProc) {
		for(int i=0; i<processes.size();i++) {
			ArrayList<Integer> arrTimes = processes.get(i).getArrTimes();
			int currentBurst = processes.get(i).getBLA();
			if (currentBurst<arrTimes.size() && arrTimes.get(currentBurst) == time) {
				activeProc.add(processes.get(i));
				System.out.println("time "+time+"ms: Process "+processes.get(i).getID()+" arrived (requires "+processes.get(i).getMemFrames()+" frames)");
				addBestFit(processes.get(i), memory, activeProc, processes);
			}
		}
	}
	
	private static void addBestFit(Process process, ArrayList<Character> memory, ArrayList<Process> activeProc, ArrayList<Process> processes){
		int spaceNeeded = process.getMemFrames();
		int freeSpace = 0;
		int totalFreeSpace = 0;
		ArrayList<Partition> freePartitions = new ArrayList<>();
		
		//get partitions
		for (int i = 0; i < Max_Mem_Frames; i++) {
			if (memory.get(i) == '.') {
				for (int j = i; j < Max_Mem_Frames; j++) {
					if (memory.get(j) != '.' || j == Max_Mem_Frames-1) {
						freePartitions.add(new Partition(i, j-i));	
						i += j;
						break;
					}
				}
			}
		}
		for (int i = 0; i < Max_Mem_Frames; i++)
			if (memory.get(i) == '.')
				totalFreeSpace++;
			
		int start = -1;
		if (totalFreeSpace < spaceNeeded) {
			System.out.println("time "+time+"ms: Cannot place process "+process.getID()+" -- skipped!");
			process.incrementBLA();
			return;
		}
		else {
			int minPartition = Max_Mem_Frames;
			for (int i = 0; i < freePartitions.size(); i++) {
				if (freePartitions.get(i).getSize() <= minPartition && freePartitions.get(i).getSize() >= spaceNeeded) {
					minPartition = freePartitions.get(i).getSize();
					start = freePartitions.get(i).getStart();
				}
			}
		}
		if (start == -1) {
			//defragment and increment times
			System.out.println("time "+time+"ms: Cannot place process "+process.getID()+" -- starting defragmentation");
			ArrayList<Character> processesDealtWith = new ArrayList<>();
			int framesMoved = defragment(memory, activeProc, processesDealtWith);
			time+=framesMoved;
			for (int i = 0; i < processes.size(); i++) 
				processes.get(i).incrementArrivalTimes(framesMoved);
			System.out.print("time "+time+"ms: Defragmentation complete (moved "+framesMoved+" frames: ");
			for (int i = 0; i < processesDealtWith.size()-1; i++) 
				System.out.print(processesDealtWith.get(i)+", ");
			System.out.println(processesDealtWith.get(processesDealtWith.size()-1)+")");

			//try again to place
			printMemory(memory);
			for (int i = 0; i < Max_Mem_Frames; i++) {
				if (memory.get(i) == '.') {
					start = i;
					break;
				}
			}
			for (int i = start; i < start+spaceNeeded; i++) 
				memory.set(i, process.getID().charAt(0));			
			System.out.println("time "+time+"ms: Placed process "+process.getID()+":");
			printMemory(memory);
		}
		else {
			//System.out.println("Start: "+start);
			for (int i = start; i < start+spaceNeeded; i++) 
				memory.set(i, process.getID().charAt(0));
			System.out.println("time "+time+"ms: Placed process "+process.getID()+":");
			printMemory(memory);
		}
	}

	private static void best_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		System.out.println("time "+time+"ms: Simulator started (Contiguous -- Best-Fit)");
		ArrayList<Process> activeProcesses = new ArrayList<Process>();
		time = 0;
		
		while (true) {
			removal(activeProcesses, memory);
			arrivalBF(processes, memory, activeProcesses);
			time++;
			int n = 0;
			for (int i = 0; i < processes.size(); i++) {
				if (processes.get(i).getBLA() == processes.get(i).getArrTimes().size())
					n++;
			}
			if (n == processes.size())
				break;
		}
		System.out.print("time "+(time-1)+"ms: Simulator ended (Contiguous -- Best-Fit)");
	}
	
	public static void arrivalWF(ArrayList<Process> processes, ArrayList<Character> memory, ArrayList<Process> activeProc) {
		for(int i=0; i<processes.size();i++) {
			ArrayList<Integer> arrTimes = processes.get(i).getArrTimes();
			int currentBurst = processes.get(i).getBLA();
			if (currentBurst<arrTimes.size() && arrTimes.get(currentBurst) == time) {
				activeProc.add(processes.get(i));
				System.out.println("time "+time+"ms: Process "+processes.get(i).getID()+" arrived (requires "+processes.get(i).getMemFrames()+" frames)");
				addWorstFit(processes.get(i), memory, activeProc, processes);
			}
		}
	}
	
	private static void addWorstFit(Process process, ArrayList<Character> memory, ArrayList<Process> activeProc, ArrayList<Process> processes){
		int spaceNeeded = process.getMemFrames();
		int freeSpace = 0;
		int totalFreeSpace = 0;
		ArrayList<Partition> freePartitions = new ArrayList<>();
		
		//get partitions
		for (int i = 0; i < Max_Mem_Frames; i++) {
			if (memory.get(i) == '.') {
				for (int j = i; j < Max_Mem_Frames; j++) {
					if (memory.get(j) != '.' || j == Max_Mem_Frames-1) {
						freePartitions.add(new Partition(i, j-i));	
						i += j-1; //didn't do this in best fit
						break;
					}
				}
			}
		}
		for (int i = 0; i < Max_Mem_Frames; i++)
			if (memory.get(i) == '.')
				totalFreeSpace++;
			
		int start = -1;
		if (totalFreeSpace < spaceNeeded) {
			System.out.println("time "+time+"ms: Cannot place process "+process.getID()+" -- skipped!");
			process.incrementBLA();
			return;
		}
		else {
			int maxPartition = 0;
			for (int i = 0; i < freePartitions.size(); i++) {
				if (freePartitions.get(i).getSize() >= maxPartition && freePartitions.get(i).getSize() >= spaceNeeded) {
					maxPartition = freePartitions.get(i).getSize();
					start = freePartitions.get(i).getStart();
				}
			}
		}
		if (start == -1) {
			//defragment and increment times
			System.out.println("time "+time+"ms: Cannot place process "+process.getID()+" -- starting defragmentation");
			ArrayList<Character> processesDealtWith = new ArrayList<>();
			int framesMoved = defragment(memory, activeProc, processesDealtWith);
			time+=framesMoved;
			for (int i = 0; i < processes.size(); i++) 
				processes.get(i).incrementArrivalTimes(framesMoved);
			System.out.print("time "+time+"ms: Defragmentation complete (moved "+framesMoved+" frames: ");
			for (int i = 0; i < processesDealtWith.size()-1; i++) 
				System.out.print(processesDealtWith.get(i)+", ");
			System.out.println(processesDealtWith.get(processesDealtWith.size()-1)+")");

			//try again to place
			printMemory(memory);
			for (int i = 0; i < Max_Mem_Frames; i++) {
				if (memory.get(i) == '.') {
					start = i;
					break;
				}
			}
			for (int i = start; i < start+spaceNeeded; i++) 
				memory.set(i, process.getID().charAt(0));			
			System.out.println("time "+time+"ms: Placed process "+process.getID()+":");
			printMemory(memory);
		}
		else {
			//System.out.println("Start: "+start);
			for (int i = start; i < start+spaceNeeded; i++) 
				memory.set(i, process.getID().charAt(0));
			System.out.println("time "+time+"ms: Placed process "+process.getID()+":");
			printMemory(memory);
		}
	}
	
	private static void worst_fit(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		System.out.println("time "+time+"ms: Simulator started (Contiguous -- Worst-Fit)");
		ArrayList<Process> activeProcesses = new ArrayList<Process>();
		time = 0;
		
		while (true) {
			removal(activeProcesses, memory);
			arrivalWF(processes, memory, activeProcesses);
			time++;
			int n = 0;
			for (int i = 0; i < processes.size(); i++) {
				if (processes.get(i).getBLA() == processes.get(i).getArrTimes().size())
					n++;
			}
			if (n == processes.size())
				break;
		}
		System.out.print("time "+(time-1)+"ms: Simulator ended (Contiguous -- Worst-Fit)");
	}
	
	public static void addPage(ArrayList<ArrayList<Pair>> pTable, String id, int procIndex, int page, int frame) {
		Pair p = new Pair(page, frame, id);
		pTable.get(procIndex).add(p);
	}
	
	public static void printPTable(ArrayList<ArrayList<Pair>> pTable, int size) {
//		System.out.println(size);
		System.out.println("PAGE TABLE [page,frame]:");
		for(int i=0;i<size;i++) {
//			System.out.println("i: "+i);
			Pair p = pTable.get(i).get(0);
			String id = pTable.get(i).get(0).getID();
//			if(id=="") {
//				break;
//			}
			System.out.print(id+":");
			for(int j=0;j<pTable.get(i).size();j++) {
				if(j%10==0&&j!=0) {
					System.out.print(pTable.get(i).get(j).toString());
				}else {
					System.out.print(" "+pTable.get(i).get(j).toString());					
				}
				if(j%10==9&&j!=0) {
					System.out.println("");
				}
			}
			System.out.println("");
		}
	}
	
	public static void addNonContiguous(Process p, ArrayList<Character> memory, ArrayList<Process> activeProc, ArrayList<Process> processes, ArrayList<ArrayList<Pair>> pTable, int time){
		int fNeeded = p.getMemFrames();
		char id = p.getID().charAt(0);
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
			p.incrementBLA();
			return;
		}
		boolean inserted = false;
		for(int i=0;i<activeProc.size();i++) {
			if(p.getID().compareTo(pTable.get(i).get(0).getID())<0) {
				pTable.add(i, new ArrayList<Pair>());
				activeProc.add(i, p);
				inserted = true;
//				System.out.println("Inserted");
				break;
			}
		}
		if(!inserted) {
			pTable.add(new ArrayList<Pair>());
			activeProc.add(p);
		}
		int procIndex = activeProc.indexOf(p);
		int page = 0;
		for(int i=0;i<fNeeded;i++, page++) {
//			System.out.println(i+startOfFreeFrames);
			if(i+startOfFreeFrames>=Max_Mem_Frames) {
				for(int j=0;j<Max_Mem_Frames;j++) {
					if(memory.get(j)=='.') {
						startOfFreeFrames = j;
						break;
					}
				}
			}
			if(memory.get(i+startOfFreeFrames)!='.') {
				for(int j=i; j<Max_Mem_Frames; j++) {
					if(memory.get(j)=='.') {
						startOfFreeFrames = j;
//						System.out.println("j"+j);
						fNeeded-=i;
						i=0;
						break;
					}
				}
			}
			if(memory.get(i+startOfFreeFrames)=='.') {
				memory.set(i+startOfFreeFrames, id);
				addPage(pTable, p.getID(), procIndex, page, i+startOfFreeFrames);
			}
		}
		System.out.println("time "+time+"ms: Placed process "+p.getID()+":");
		printMemory(memory);
		printPTable(pTable, activeProc.size());
	}
	
	public static void NonContiguousArrival(ArrayList<Process> processes, ArrayList<Character> memory, ArrayList<Process> activeProc, ArrayList<ArrayList<Pair>> pTable, int time) {
		for(int i=0; i<processes.size();i++) {
			ArrayList<Integer> arrTimes = processes.get(i).getArrTimes();
			int currentBurst = processes.get(i).getBLA();
			if (currentBurst<arrTimes.size() && arrTimes.get(currentBurst) == time) {
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
				System.out.println("time "+time+"ms: Process "+ activeProc.get(i).getID()+" removed:");
				activeProc.get(i).incrementBLA(); //need to add way to make sure this doesn't go out of bounds
				remove(activeProc.get(i), memory);
				pTable.remove(i);
				activeProc.remove(activeProc.get(i));
				i--;
				printMemory(memory);
				int size = activeProc.size();
				printPTable(pTable, size);
			}
		}
	}
	
	private static void non_contiguous(ArrayList<Process> processes, ArrayList<Character> memory, BufferedWriter writer){
		ArrayList<ArrayList<Pair>> pTable = new ArrayList<ArrayList<Pair>>();
		ArrayList<Process> activeProcesses = new ArrayList<Process>();
		int time =0;
		System.out.println("time "+ time+ "ms: Simulator started (Non-contiguous)");
		while(true) {
			NonContiguousArrival(processes, memory, activeProcesses , pTable, time);
			NonContiguousRemoval(activeProcesses, memory, pTable, time);
			time++;
			int n = 0;
			for (int i = 0; i < processes.size(); i++) {
				if (processes.get(i).getBLA() == processes.get(i).getArrTimes().size())
					n++;
			}
			if (n == processes.size())
				break;
		}
		System.out.print("time "+(time-1)+"ms: Simulator ended (Non-contiguous)");
	}
}