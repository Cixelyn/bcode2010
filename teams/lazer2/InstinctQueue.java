package lazer2;

import java.util.ArrayList;
import java.util.Iterator;

import lazer2.instincts.Instinct;

public class InstinctQueue {
	private ArrayList<Instinct> instincts;
	
	public InstinctQueue() {
		instincts = new ArrayList<Instinct>();
	}
	
	public void addInstinct(Instinct i) {
		instincts.add(i);
	}
	
	public void executeInstincts() {
		
		Iterator<Instinct> it = this.instincts.iterator();
		while (it.hasNext()) {
			Instinct curr = it.next();
			curr.execute();
		}
		
	}
	
}
