package com.anthonyatkins.simplebackgammon.model;

public class Move implements Comparable {
	public final Slot startSlot;
	public final Slot endSlot;
	public final SimpleDie die;
	public boolean pieceBumped = false;

	public Move(Slot startSlot, Slot endSlot, SimpleDie die) {
		this.startSlot = startSlot;
		this.endSlot = endSlot;
		this.die = die;
	}

	public Move (Move existingMove) {
		this.startSlot = existingMove.startSlot;
		this.endSlot = existingMove.endSlot;
		this.die = new SimpleDie(existingMove.die);
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Move)) return false;
		
		Move otherMove = (Move) obj;
		
		if (this.startSlot.equals(otherMove.startSlot) && 
			this.endSlot.equals(otherMove.endSlot) &&
			this.die.equals(otherMove.die)) {
			return true;
		}
		
		return false;
	}

	public int compareTo(Object another) {
		if (another instanceof Move) {
			Move anotherMove = (Move) another;
			/* sort by startSlot position if there is a difference */
			if (anotherMove.startSlot.position > this.startSlot.position) { return 1; }
			else if (anotherMove.startSlot.position < this.startSlot.position) { return -1; }
			/* otherwise, sort by endSlot position if there is a difference */
			else if (anotherMove.endSlot.position > this.endSlot.position) { return 1; }
			else if (anotherMove.endSlot.position < this.endSlot.position) { return -1; }

			return this.die.getValue() - anotherMove.die.getValue();
		}

		/* otherwise, they are sorted at the same level (should not be possible) */
		return 0;
	}
	
}
