package app;

import java.util.ArrayList;

public class ReferenceGraph {
	/*
	private ArrayList<GraphNode> node_list = new ArrayList<GraphNode>();
	
	public void addNode(int int_val, boolean bool_val) {
		GraphNode new_node = new GraphNode(int_val, bool_val);
		node_list.add(new_node);
	}
	
	public void createConnections(int[][] adj_matrix) {
		
	}
	
	*/
	
	public GraphNode createDefaultGraph() {
		GraphNode a = new GraphNode(0, true);
		GraphNode b = new GraphNode(1, false);
		GraphNode c = new GraphNode(2, true);
		
		a.setNext(b);
		b.setNext(c);
		c.setNext(a);
		
		return a;
	}
	
	public static void main(String[] args) {
    	ReferenceGraph ref = new ReferenceGraph();
    	ref.createDefaultGraph();
    }

	
	
	
}
