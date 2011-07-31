package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Player {
	public final int color;
	public GameDice dice;
	public Pieces pieces = new Pieces();
	public Moves moves = new Moves();
	public Move selectedMove = null;
	private Game game;
	private boolean active = false;
	
	public Player(int color, Game game) {
		this.color = color;
		this.game = game;
		this.dice = new GameDice(color, game, this);
	}

	public Player(Player existingPlayer, Game game) {
		this.color = existingPlayer.color;
		this.dice = new GameDice(existingPlayer.dice);
		
		this.game = game;
	}

	public ArrayList<String> getDiceState() {
		ArrayList<String> moveStateList = new ArrayList<String>();
		
		Iterator<SimpleDie> dieIterator = this.dice.iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			moveStateList.add(die.getValue() + ":" + die.isUsed());
		}
		
		return moveStateList;
	}

	public void setDiceState(ArrayList<String> diceState) {
		if (diceState != null) {
			Iterator<String> diceStringIterator = diceState.iterator();
			Iterator<SimpleDie> diceIterator = dice.iterator();
			while (diceIterator.hasNext()) {
				SimpleDie die = diceIterator.next();
				if (diceStringIterator.hasNext()) {
					String diceString = diceStringIterator.next();
					String[] diceStringParts = diceString.split(":");
					Integer dieValue = new Integer(diceStringParts[0]);
					die.setValue(dieValue.intValue());
					Boolean dieIsUsed = new Boolean(diceStringParts[1]);
					die.setUsed(dieIsUsed.booleanValue());
				}
			}
		}
	}
	
	
	public Set<Integer> getSlots() {
		Set<Integer> mySlots = new TreeSet<Integer>();
		
		for (Piece piece: pieces) {
			// exclude the dugouts and the bar
			if (piece.position >= 0 && piece.position <=23) {
				mySlots.add(piece.position);
			}
		}
		
		return mySlots;
	}
	
	public boolean hasMovesLeft() {
		Iterator<SimpleDie> dieIterator = dice.iterator();
		while (dieIterator.hasNext()) {
			if (!dieIterator.next().isUsed()) { return true; }
		}
		return false;
	}

	public List<Move> movesForSlot(Slot sourceSlot) {
		List<Move> movesForSlot = new ArrayList<Move>();
		
		Iterator<Move> moveIterator = moves.iterator();
		while (moveIterator.hasNext()) {
			Move move = moveIterator.next();
			if (move.startSlot.equals(sourceSlot)) { movesForSlot.add(move); }
		}
		
		return movesForSlot;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + color;
		result = prime * result + ((dice == null) ? 0 : dice.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (color != other.color)
			return false;
		if (dice == null) {
			if (other.dice != null)
				return false;
		} else if (!dice.equals(other.dice))
			return false;
		return true;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
}
