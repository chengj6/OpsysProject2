// *********************************************************************************
//	Shashank Sundar: sundas6
//	Jonathan Cheng: chengj6
//	Kris Whelan: whelak2
// *********************************************************************************

public class Pair {
	private int key;
	private int value;
	private String pID;
	
	public Pair(int k, int v, String id) {
		key = k;
		value = v;
		pID = id;
	}
	
	public String toString() {
		String str = "["+Integer.toString(key)+","+Integer.toString(value)+"]";
		return str;
	}
	public String getID() {
		return pID;
	}
}
