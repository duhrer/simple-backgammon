package com.anthonyatkins.simplebackgammon.model;

public class Pit {
	public SlotBank topSlotBank;
	public GameDice dice;
	public SlotBank bottomSlotBank; 
	
	public Pit(GameDice dice) {		
		topSlotBank = new SlotBank(SlotBank.L_R);
		bottomSlotBank = new SlotBank(SlotBank.R_L);
		this.dice = dice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bottomSlotBank == null) ? 0 : bottomSlotBank.hashCode());
		result = prime * result + ((dice == null) ? 0 : dice.hashCode());
		result = prime * result
				+ ((topSlotBank == null) ? 0 : topSlotBank.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pit))
			return false;
		Pit other = (Pit) obj;
		if (bottomSlotBank == null) {
			if (other.bottomSlotBank != null)
				return false;
		} else if (!bottomSlotBank.equals(other.bottomSlotBank))
			return false;
		if (dice == null) {
			if (other.dice != null)
				return false;
		} else if (!dice.equals(other.dice))
			return false;
		if (topSlotBank == null) {
			if (other.topSlotBank != null)
				return false;
		} else if (!topSlotBank.equals(other.topSlotBank))
			return false;
		return true;
	}
	
	
}

