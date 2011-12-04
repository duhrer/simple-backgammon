package com.anthonyatkins.simplebackgammon.model;

public class GameDie extends SimpleDie {
	private Player player;
	
	private boolean used = false;

	public GameDie(int color, Player player) {
		super(color);
		this.player = player;
	}
	
	public GameDie(int value, int color, Player player) {
		super(value,color);
		this.player = player;
	}

	public GameDie(GameDie existingDie) {
		super(existingDie.getValue(),existingDie.getColor());
	}
	
	public boolean hasMoves() {
		for (Move move: player.getMoves()) {
			if (move.getDie().equals(this)) return true;
		}
		return false;
	}
}
