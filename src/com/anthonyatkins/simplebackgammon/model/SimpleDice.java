package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class SimpleDice extends ArrayList<SimpleDie> implements Comparable {
	private int color;

	public SimpleDice(int color) {
		this.color = color;
		
		this.add(new SimpleDie(0,color));
		this.add(new SimpleDie(0,color));
	}

	/**
	 * Create a new Dice object based on an existing pair of dice.
	 * @param existingDice The current dice.
	 */
	public SimpleDice(SimpleDice existingDice) {
		this.color = existingDice.color;
		
		for (SimpleDie die: existingDice) {
			this.add(new SimpleDie(die.getValue(),die.getColor()));
		}
	}

	public int getColor() {
		return this.color;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	public void roll() {
		this.clear();
		SimpleDie die1 = new SimpleDie(this.color);
		die1.roll();
		this.add(die1);
		SimpleDie die2 = new SimpleDie(this.color);
		die2.roll();
		this.add(die2);
		// we will use the dice to keep track of what we can still move
		// so, add two more dice if we have doubles
		if (this.get(0).getValue() == this.get(1).getValue() && this.get(0).getValue() != 0) {
			this.add(new SimpleDie(this.get(0).getValue(), this.color));
			this.add(new SimpleDie(this.get(0).getValue(), this.color));
		}
	}
	
	public void roll(int d1Value, int d2Value) {
		this.clear();
		this.add(new SimpleDie(d1Value,color));
		this.add(new SimpleDie(d2Value,color));
		
		if (d1Value == d2Value && d1Value != 0) {
			this.add(new SimpleDie(this.get(0).getValue(), this.color));
			this.add(new SimpleDie(this.get(0).getValue(), this.color));
		}
	}
	

	/**
	 * Return only the unique numbers in our dice (used for move lookups, etc.)
	 * @return The unique integer values of each die (in order).
	 */
	public Set<Integer> getValues() {
		Set<Integer> values = new TreeSet<Integer>();
		for (int a=0; a<this.size(); a++) {
			SimpleDie die = this.get(a);
			if (!die.isUsed()) {
				values.add(die.getValue());
			}
		}
		
		return values;
	}
	public int getTotal() {
		int total = 0;
		Iterator<SimpleDie> dieIterator = this.iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			total += die.getValue();
		}		
		return total;
	}
	
	public void flagUsedDie(int dieValue) {
		Iterator<SimpleDie> dieIterator = this.iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			if (!die.isUsed() && die.getValue() == dieValue) { 
				die.setUsed();
				return;
			}
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		int sum = 0;
		
		// Since die values are less than ten we can just store one per digit
		for (SimpleDie die : this) {
			if (sum > 0) { sum *= 10; }
			sum += die.getValue();
		}
		
		result = prime * result + color + sum;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleDice other = (SimpleDice) obj;
		if (color != other.color)
			return false;
	
		if (this.size() != other.size()) {
			return false;
		}
		else {
			Iterator<SimpleDie> myDieIterator = this.iterator();
			Iterator<SimpleDie> otherDieIterator = other.iterator();
			while (myDieIterator.hasNext()) {
				if (!myDieIterator.next().equals(otherDieIterator.next())) return false;
			}
		}
		return true;
	}

	public int compareTo(Object another) {
		return 0;
	}
}
