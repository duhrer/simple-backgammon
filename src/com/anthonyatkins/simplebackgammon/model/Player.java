package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Player {
	private long id;
	private final int color;
	private GameDice dice;
	private Pieces pieces = new Pieces();
	private Moves moves = new Moves();
	private Move selectedMove = null;
	private Game game;
	private boolean active = false;
	private String name = null;
	
	// Database setup information
	public static final String _ID            = "_id";
	public static final String NAME           = "name";
	
	public static final String TABLE_NAME = "player";
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		NAME + " varchar(20) " +
		");";
	
	public static final String[] COLUMNS = {
			_ID,
			NAME
	};
	
	public Player(int color, Game game) {
		this.color = color;
		this.game = game;
		this.setDice(new GameDice(color, game, this));
	}

	public Player(Player existingPlayer, Game game) {
		this.color = existingPlayer.getColor();
		this.setDice(new GameDice(existingPlayer.getDice()));
		
		this.game = game;
	}

	public ArrayList<String> getDiceState() {
		ArrayList<String> moveStateList = new ArrayList<String>();
		
		Iterator<SimpleDie> dieIterator = this.getDice().iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			moveStateList.add(die.getValue() + ":" + die.isUsed());
		}
		
		return moveStateList;
	}

	public void setDiceState(ArrayList<String> diceState) {
		if (diceState != null) {
			Iterator<String> diceStringIterator = diceState.iterator();
			Iterator<SimpleDie> diceIterator = getDice().iterator();
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
		
		for (Piece piece: getPieces()) {
			// exclude the dugouts and the bar
			if (piece.position >= 0 && piece.position <=23) {
				mySlots.add(piece.position);
			}
		}
		
		return mySlots;
	}
	
	public boolean hasMovesLeft() {
		Iterator<SimpleDie> dieIterator = getDice().iterator();
		while (dieIterator.hasNext()) {
			if (!dieIterator.next().isUsed()) { return true; }
		}
		return false;
	}

	public List<Move> movesForSlot(Slot sourceSlot) {
		List<Move> movesForSlot = new ArrayList<Move>();
		
		Iterator<Move> moveIterator = getMoves().iterator();
		while (moveIterator.hasNext()) {
			Move move = moveIterator.next();
			if (move.getStartSlot().equals(sourceSlot)) { movesForSlot.add(move); }
		}
		
		return movesForSlot;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getColor();
		result = prime * result + ((getDice() == null) ? 0 : getDice().hashCode());
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
		if (getColor() != other.getColor())
			return false;
		if (getDice() == null) {
			if (other.getDice() != null)
				return false;
		} else if (!getDice().equals(other.getDice()))
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

	public void setName(String playerName) {
		this.name = playerName;
	}

	public long getId() {
		return this.id;
	}

	public int getColor() {
		return color;
	}

	public Pieces getPieces() {
		return pieces;
	}

	public void setPieces(Pieces pieces) {
		this.pieces = pieces;
	}

	public Move getSelectedMove() {
		return selectedMove;
	}

	public void setSelectedMove(Move selectedMove) {
		this.selectedMove = selectedMove;
	}

	public Moves getMoves() {
		return moves;
	}

	public void setMoves(Moves moves) {
		this.moves = moves;
	}

	public GameDice getDice() {
		return dice;
	}

	public void setDice(GameDice dice) {
		this.dice = dice;
	}
}
