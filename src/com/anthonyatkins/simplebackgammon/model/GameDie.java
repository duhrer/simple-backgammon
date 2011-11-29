package com.anthonyatkins.simplebackgammon.model;

public class GameDie extends SimpleDie {
	private Game game;
	private Player player;
	
	private boolean used = false;

	public GameDie(int color, Game game, Player player) {
		super(color);
		this.game = game;
		this.player = player;
	}
	
	public GameDie(int value, int color, Game game, Player player) {
		super(value,color);
		this.game = game;
		this.player = player;
	}

	public GameDie(GameDie existingDie) {
		super(existingDie.getValue(),existingDie.getColor());
		this.game = existingDie.game;
	}
	
	public Game getGame() {
		return game;
	}

	public boolean hasMoves() {
		for (Move move: player.getMoves()) {
			if (move.getDie().equals(this)) return true;
		}
		return false;
	}
}
