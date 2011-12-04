package com.anthonyatkins.simplebackgammon.model;

public class Pit {
	public SlotBank topSlotBank;
	private final GameDice dice;
	public SlotBank bottomSlotBank;
	private final Game game; 
	
	public Pit(GameDice dice, Game game) {		
		topSlotBank = new SlotBank(SlotBank.L_R);
		bottomSlotBank = new SlotBank(SlotBank.R_L);
		this.dice = dice;
		this.game = game;
	}

	public GameDice getDice() {
		return dice;
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

	public Game getGame() {
		return this.game;
	}
	
	
}

