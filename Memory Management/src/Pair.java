
public class Pair {
	private int key;
	private int value;
	
	public Pair(int k, int v) {
		key = k;
		value = v;
	}
	
	public String toString() {
		String str = "["+Integer.toString(key)+","+Integer.toString(value);
		return str;
	}
}
