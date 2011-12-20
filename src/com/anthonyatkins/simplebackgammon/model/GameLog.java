package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Iterator;

public class GameLog extends ArrayList<Turn> {
	public GameLog() {
	}

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GameLog))
			return false;
		GameLog other = (GameLog) obj;
	
		if (this.size() != other.size()) return false;
		
		Iterator<Turn> myTurnIterator = this.iterator();
		Iterator<Turn> otherTurnIterator = other.iterator();
		
		while (myTurnIterator.hasNext()) {
			Turn myTurn = myTurnIterator.next();
			Turn otherTurn = otherTurnIterator.next();
			
			if (!myTurn.equals(otherTurn)) return false;
		}
		
		return true;
	}

}
