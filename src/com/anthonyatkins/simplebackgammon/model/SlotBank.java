package com.anthonyatkins.simplebackgammon.model;


public class SlotBank {
	public final static int L_R = 0;
	public final static int R_L = 1;
	int direction;
	
	private SlotSet slots = new SlotSet();
	
	public SlotBank(int direction) {
		this.direction = direction;
	}
	
	public void addSlot(Slot slot) {
		slots.add(slot);
	}
	
	public void removeSlot(Slot slot) {
		slots.remove(slot);
	}
	
	public int getDirection() {
		return this.direction;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + direction;
		result = prime * result + ((slots == null) ? 0 : slots.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SlotBank))
			return false;
		SlotBank other = (SlotBank) obj;
		if (direction != other.direction)
			return false;
		if (slots == null) {
			if (other.slots != null)
				return false;
		} else if (!slots.equals(other.slots))
			return false;
		return true;
	}
	
	
}
