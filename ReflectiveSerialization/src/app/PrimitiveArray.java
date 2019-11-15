package app;

public class PrimitiveArray {
	int[] arr;
	
	public void initializeArray(int[] _arr) {
		arr = new int[_arr.length];
		for (int i = 0; i < arr.length; i+=1) {
			arr[i] = _arr[i];
		}
	}
}
