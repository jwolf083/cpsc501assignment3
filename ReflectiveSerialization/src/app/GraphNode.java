package app;

public class GraphNode {
	private GraphNode next;
	private int prim_int;
	private boolean prim_bool;
	private GraphNode uninitialized;
	private GraphNode _null = null;
	
	GraphNode() {
		
	}
	
	GraphNode(int _prim_int, boolean _prim_bool) {
		this.next = null;
		this.prim_int = _prim_int;
		this.prim_bool = _prim_bool;
	}
	
	public void setNext(GraphNode _next) {
		this.next = _next;
	}
}
