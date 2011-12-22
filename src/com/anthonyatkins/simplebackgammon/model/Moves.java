package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Iterator;

public class Moves extends ArrayList<Move> implements Comparable<Move> {
	private static final long serialVersionUID = -7094672094325795661L;

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Moves))
			return false;
		Moves other = (Moves) obj;
		
		if (this.size() != other.size()) return false;
		
		Iterator<Move> moveIterator = this.iterator();
		Iterator<Move> otherMoveIterator = this.iterator();
		
		while (moveIterator.hasNext()) {
			Move myMove = moveIterator.next();
			Move otherMove = otherMoveIterator.next();
			
			if (!myMove.equals(otherMove)) { return false; }
		}
		
		return true;
	}

	public int compareTo(Move move) {
		return 0;
	}
	
	public Moves getMovesForStartSlot(Slot slot) {
		Moves movesForSlot = new Moves();
		
		for (Move move : this) {
			if (move.getStartSlot().equals(slot)) movesForSlot.add(move);
		}
		
		return movesForSlot;
	}
	
	public Moves getMovesForEndSlot(Slot slot) {
		Moves movesForSlot = new Moves();
		
		for (Move move : this) {
			if (move.getEndSlot().equals(slot)) movesForSlot.add(move);
		}
		
		return movesForSlot;
	}
}
