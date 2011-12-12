package com.anthonyatkins.simplebackgammon.model;

public class GameDie extends SimpleDie {
	private final Turn turn;

	public GameDie(int color, Turn turn) {
		super(color);
		this.turn = turn;
	}
	
	public GameDie(int value, int color, Turn turn) {
		super(value,color);
		this.turn = turn;
	}

	public boolean hasMoves() {
		return turn.movesLeftForDie(this);
	}
}
