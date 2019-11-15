package app;

import java.util.ArrayList;

public class Queue {
	private ArrayList<Object> _queue = new ArrayList<Object>();
	int head = 0;
	int tail = 0;
	
	public void add(Object object) {
		this._queue.add(object);
		tail += 1;
	}
	
	public Object poll() {
		Object o;
		
		if (head == tail) {
			return null;
		} else {
			o = this._queue.get(head);
			this._queue.set(head , null);
			head +=1;
		}
		
		return o;
	}
}
