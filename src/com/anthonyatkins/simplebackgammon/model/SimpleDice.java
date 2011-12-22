package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SimpleDice extends ArrayList<SimpleDie> implements Comparable {
	@Override
	public String toString() {
		return "SimpleDice [color=" + color + ", dice="+super.toString()+"]";
	}

	private int color;

	public SimpleDice(int color) {
		this.color = color;
		
		roll();
	}

	/**
	 * Create a new Dice object based on an existing pair of dice.
	 * @param existingDice The current dice.
	 */
	public SimpleDice(SimpleDice existingDice) {
		this.color = existingDice.color;
		
		for (SimpleDie die: existingDice) {
			this.add(new SimpleDie(die));
		}
	}

	public SimpleDice(int d1, int d2, int color) {
		this.color=color;
		
		roll(d1,d2);
	}

	public int getColor() {
		return this.color;
	}
	
	public void setColor(int color){
		this.color = color;
	}

	protected void roll() {
		int d1 = (int) (Math.round(Math.random() * 5) + 1);
		int d2 = (int) (Math.round(Math.random() * 5) + 1);

		roll(d1,d2);
	}
	
	public void roll(int d1, int d2) {
		this.clear();
		SimpleDie die1 = new SimpleDie(d1,this.color);
		this.add(die1);
		SimpleDie die2 = new SimpleDie(d2,this.color);
		this.add(die2);
		
		checkForDoubles();
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
			SortedSet<SimpleDie> mySortedDice = new TreeSet<SimpleDie>(this);
			SortedSet<SimpleDie> otherSortedDice = new TreeSet<SimpleDie>(other);
			if (!mySortedDice.equals(otherSortedDice)) return false;
		}
		return true;
	}

	public int compareTo(Object another) {
		return 0;
	}
	
	protected void checkForDoubles() {
		// we will use the dice to keep track of what we can still move
		// so, add two more dice if we have doubles
		if (this.get(0).getValue() == this.get(1).getValue() && this.get(0).getValue() != 0) {
			this.add(new SimpleDie(this.get(0).getValue(), this.color));
			this.add(new SimpleDie(this.get(0).getValue(), this.color));
		}
	}
	
	public SimpleDice getDiceByPips(int pips) {
		return getDiceByPips(pips,false);
	}
	
	public SimpleDice getDiceByPips(int pips, boolean isMovingOut) {
		return getDiceByPips(pips,isMovingOut,false);
	}
	
	protected SimpleDice getDiceByPips(int pips, boolean isMovingOut, boolean used) {
		SimpleDice dice = new SimpleDice(this.color);
		dice.clear();

		int runningTotal = 0;
		int position = 0;
		// If we're moving out, we can exceed the roll on the dice required
		if (isMovingOut) {
			// One pass through for single roll moves
			for (SimpleDie die : this) {
				if (die.isUsed() != used) { continue; }
				if (pips == die.getValue()) {
					dice.add(die);
					break;
				}
			}
			
			// Only continue if we haven't already found a solution
			if (dice.size() == 0) {
				for (SimpleDie die : this) {
					if (die.isUsed() != used) { continue; }
					runningTotal += die.getValue();
					position++;
					
					if (runningTotal == pips) {
						for (int a=0; a<position;a++) {
							dice.add(this.get(a));
						}
						break;
					}
				}
			}
		}
		// Otherwise, it's a much simpler process
		else {
			for (SimpleDie die : this) {
				if (die.isUsed() != used) { continue; }
				if (pips == die.getValue()) {
					dice.add(die);
					break;
				}
				
				runningTotal += die.getValue();
				position++;
				
				if (runningTotal == pips) {
					for (int a=0; a<position;a++) {
						dice.add(this.get(a));
					}
					break;
				}
			}
		}
		
		return dice;
	}

	// Only used for "undo"
	public SimpleDice getUsedDiceByPips(int pips) {
		return getUsedDiceByPips(pips,false);
	}
	public SimpleDice getUsedDiceByPips(int pips, boolean isMovingOut) {
		return getDiceByPips(pips,isMovingOut,true);
	}

	public void setUsed() {
		setUsed(true);
	}

	public void setUsed(boolean isUsed) {
		for (SimpleDie die : this) {
			die.setUsed(isUsed);
		}
	}
}
