package app;

public class ReferenceArray {
	GraphNode[] arr;
	
	public void initializeArray(GraphNode[] _arr) {
		arr = new GraphNode[_arr.length];
		for (int i = 0; i < arr.length; i+=1) {
			arr[i] = _arr[i];
		}
	}

}
