package com.anthonyatkins.simplebackgammon.model;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.activity.SimpleBackgammon;


public class Turn {
	public Moves moves = new Moves();
	public Player player;
	public GameDice dice;
	public Turn(Player player, GameDice dice) {
		this.player = player;
		this.dice = new GameDice(dice);
	}
	
	public Turn(Turn existingTurn) {
		this.player = existingTurn.player;
		this.dice = new GameDice(existingTurn.dice);
		for (Move move: existingTurn.moves) {
			moves.add(new Move(move));
		}
	}
	
	public Turn(Turn existingTurn, Game game) {
		if (existingTurn != null) {
			if (existingTurn.player != null) {
				if (existingTurn.player.color == Constants.BLACK) {
					this.player = game.getBlackPlayer();
				}
				else {
					this.player = game.getWhitePlayer();
				}
			}
			if (existingTurn.dice != null) {
				this.dice = new GameDice(existingTurn.dice);
				for (Move move: existingTurn.moves) {
					moves.add(new Move(move));
				}
			}
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dice == null) ? 0 : dice.hashCode());
		result = prime * result + ((moves == null) ? 0 : moves.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Turn))
			return false;
		Turn other = (Turn) obj;
		if (dice == null) {
			if (other.dice != null)
				return false;
		} else if (!dice.equals(other.dice))
			return false;
		if (moves == null) {
			if (other.moves != null)
				return false;
		} else if (!moves.equals(other.moves))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		return true;
	}
	
	
}
