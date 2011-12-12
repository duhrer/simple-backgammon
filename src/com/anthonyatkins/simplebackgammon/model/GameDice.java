package com.anthonyatkins.simplebackgammon.model;


public class GameDice extends SimpleDice{
	private final Turn turn;
	
	public GameDice(int color, Turn turn) {
		super(color);
		this.turn = turn;
		roll();
	}
	
	public GameDice(SimpleDice existingDice, Turn turn) {
		super(existingDice);
		this.turn = turn;
	}

	@Override
	protected void roll() {
		this.clear();
		GameDie die1 = new GameDie(this.getColor(),turn);
		die1.roll();
		this.add(die1);
		GameDie die2 = new GameDie(this.getColor(),turn);
		die2.roll();
		this.add(die2);
		// we will use the dice to keep track of what we can still move
		// so, add two more dice if we have doubles
		if (this.get(0).getValue() == this.get(1).getValue() && this.get(0).getValue() != 0) {
			this.add(new GameDie(this.get(0).getValue(), this.getColor(),turn));
			this.add(new GameDie(this.get(0).getValue(), this.getColor(),turn));
		}
	}
}
