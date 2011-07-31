package com.anthonyatkins.simplebackgammon.model;

import java.util.Iterator;
import java.util.TreeSet;

public class SlotSet extends TreeSet<Slot> {
	private static final long serialVersionUID = -5745176199412363080L;
	public int foo;
	
	public SlotSet() {
		super(new SlotSetComparator());
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SlotSet))
			return false;
		SlotSet other = (SlotSet) obj;

		if (this.size() != other.size()) { return false; }
		
		Iterator<Slot> thisSlotIterator = this.iterator();
		Iterator<Slot> otherSlotIterator = other.iterator();
		
		while (thisSlotIterator.hasNext()) {
			if (!otherSlotIterator.hasNext()) { return false; }
			
			if (!thisSlotIterator.next().equals(otherSlotIterator.next()));
		}
		
		return true;
	}
	
	
}


