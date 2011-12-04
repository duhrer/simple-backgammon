package com.anthonyatkins.simplebackgammon.model;


public class GameDice extends SimpleDice{
	private Player player;

	public GameDice(int color, Player player) {
		super(color);
		this.player = player;
		
		// We must get rid of the default dice
		this.clear();
		
		this.add(new GameDie(0,color,player));
		this.add(new GameDie(0,color,player));
		roll();
	}

	/**
	 * Create a new Dice object based on an existing pair of this.
	 * @param existingDice The current this.
	 */
	public GameDice(GameDice existingDice) {
		super(existingDice.getColor());
		
		this.player = existingDice.player;
		
		// remove the dice created by the parent constructor
		this.clear();
		
		for (SimpleDie die: existingDice) {
			this.add(new GameDie(die.getValue(),die.getColor(),player));
		}
	}
	
	@Override
	public void roll() {
		this.clear();
		GameDie die1 = new GameDie(this.getColor(), player);
		die1.roll();
		this.add(die1);
		GameDie die2 = new GameDie(this.getColor(), player);
		die2.roll();
		this.add(die2);
		// we will use the dice to keep track of what we can still move
		// so, add two more dice if we have doubles
		if (this.get(0).getValue() == this.get(1).getValue() && this.get(0).getValue() != 0) {
			this.add(new GameDie(this.get(0).getValue(), this.getColor(), player));
			this.add(new GameDie(this.get(0).getValue(), this.getColor(), player));
		}
	}
	
	@Override
	public void roll(int d1Value, int d2Value) {
		this.clear();
		this.add(new GameDie(d1Value,getColor(), player));
		this.add(new GameDie(d2Value,getColor(), player));
		
		if (d1Value == d2Value && d1Value != 0) {
			this.add(new GameDie(this.get(0).getValue(), this.getColor(), player));
			this.add(new GameDie(this.get(0).getValue(), this.getColor(), player));
		}
	}
}
