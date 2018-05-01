// *********************************************************************************
//	Shashank Sundar: sundas6
//	Jonathan Cheng: chengj6
//	Kris Whelan: whelak2
// *********************************************************************************

import java.util.ArrayList;

public class Process {

	private String id;
	private int memoryFrames;
	private ArrayList<Integer> arrivalTimes;
	private ArrayList<Integer> runTimes;
	
	
	public Process(String procId, int memFrames, ArrayList<Integer> arrTimes, ArrayList<Integer> rTimes) {
		id = procId;
		memoryFrames = memFrames;
		arrivalTimes = arrTimes;
		runTimes = rTimes;
	}
	
	public Process(Process cpy) {
		id = cpy.getID();
		memoryFrames = cpy.getMemFrames();
		arrivalTimes = cpy.getArrTimes();
		runTimes = cpy.getRTimes();
	}
	
//	dont know if we need this
//	//Resetting Data
//	public void reset() {
//		
//	}
	
	//Getting Data
	public String getID() {
		return id;
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
	
}