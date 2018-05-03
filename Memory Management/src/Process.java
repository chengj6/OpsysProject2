// *********************************************************************************
//	Shashank Sundar: sundas6
//	Jonathan Cheng: chengj6
//	Kris Whelan: whelak2
// *********************************************************************************

import java.util.ArrayList;

public class Process implements Comparable<Process> {

	private String id;
	private int memoryFrames;
	private ArrayList<Integer> arrivalTimes;
	private ArrayList<Integer> runTimes;
	private ArrayList<Integer> originalArrivalTimes;
	private ArrayList<Integer> originalRunTimes;
	private int burstLookingAt;
	
	
	public Process(String procId, int memFrames, ArrayList<Integer> arrTimes, ArrayList<Integer> rTimes) {
		id = procId;
		memoryFrames = memFrames;
		arrivalTimes = new ArrayList<Integer>(arrTimes);
		runTimes = new ArrayList<Integer>(rTimes);
		originalArrivalTimes = new ArrayList<Integer>(arrTimes);
		originalRunTimes = new ArrayList<Integer>(rTimes);
		burstLookingAt = 0;
	}
	
	public Process(Process cpy) {
		id = cpy.getID();
		memoryFrames = cpy.getMemFrames();
		arrivalTimes = cpy.getArrTimes();
		runTimes = cpy.getRTimes();
		burstLookingAt = cpy.getBLA();
	}
	
//	dont know if we need this
//	//Resetting Data
	public void reset() {
		burstLookingAt = 0;
		arrivalTimes = new ArrayList<>(originalArrivalTimes);
		runTimes = new ArrayList<>(originalRunTimes);
	}
	
	public void incrementBLA() {
		burstLookingAt++;
	}
	
	public void incrementArrivalTimes(int increment) {
		for (int i = 0; i < arrivalTimes.size(); i++) {
			arrivalTimes.set(i, arrivalTimes.get(i)+increment);
		}
	}
	//Getting Data
	public String getID() {
		return id;
	}
	
	public int getBLA() {
		return burstLookingAt;
	}
	
	public int getMemFrames() {
		return memoryFrames;
	}
	
	public ArrayList<Integer> getArrTimes(){
		return arrivalTimes;
	}
	
	public ArrayList<Integer> getRTimes(){
		return runTimes;
	}
	
	@Override
	public int compareTo(Process p) {
        // Compare the two processes based on remaining burst time
        return this.getID().compareTo(p.getID());
	}
	
}