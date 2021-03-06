package com.anthonyatkins.simplebackgammon.model;

import java.util.Iterator;


public class Slot implements Comparable<Slot>{
	public final static int UP = -1;
	@Override
	public String toString() {
		return "Slot [position=" + position + "]";
	}

	public final static int DOWN = 1;
	public final static int NONE = 0;
	
	public final static int TEXT_SIZE = 8;
	public static final int INVALID_POSITION = -666;
	
	private Pieces pieces = new Pieces();
	private final int direction;
	private final int position;
	public Pieces getPieces() {
		return pieces;
	}

	private boolean isSourceSlot;
	private boolean isDestSlot;
	private final Game game;
	
	public Slot(int direction, int position, Game game) {
		this.direction = direction;
		this.position = position;
		this.game = game;
	}
	
	public void addPiece(Piece piece) {
		piece.position=this.position;
		pieces.add(piece);
	}

	public Piece removePiece(Piece piece) {
		pieces.remove(piece);
		return piece;
	}
	
	public Piece removePiece() {
		if (pieces.size() > 0) {
			Piece piece = pieces.get(0);
			pieces.remove(piece);
			return piece;
		}

		return null;
	}
	
	public int getDirection() {
		return this.direction;
	}
	/**
	 * 	Whether or not a player can land on a particular slot.
	 * 
	 * @param color The color of the player who'd like to move to this slot
	 * @return True if there are two or more pieces of the opposing color, false if the slot is empty or contains our own color.
	 */
	public boolean isBlocked(int color) {
		boolean isBlocked = false;
		
		int opposingPieces = 0;
		Iterator<Piece> pieceIterator = pieces.iterator();
		while (pieceIterator.hasNext()) {
			Piece piece = pieceIterator.next();
			if (piece.color != color) { opposingPieces++; }
		}
		
		if (opposingPieces > 1) { 
			isBlocked = true;
		}

		return isBlocked;
	}

	public int getPosition() {
		return this.position;
	}

	public int compareTo(Slot anotherSlot) throws ClassCastException{
		return (this.position - ((Slot) anotherSlot).position);
	}

	public boolean isSourceSlot() {
		return isSourceSlot;
	}

	public void setSourceSlot(boolean isSourceSlot) {
		this.isSourceSlot = isSourceSlot;
	}

	public boolean isDestSlot() {
		return isDestSlot;
	}

	public void setDestSlot(boolean isDestSlot) {
		this.isDestSlot = isDestSlot;
	}

	public Game getGame() {
		return game;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + direction;
		result = prime * result + (isDestSlot ? 1231 : 1237);
		result = prime * result + (isSourceSlot ? 1231 : 1237);
		result = prime * result + ((pieces == null) ? 0 : pieces.hashCode());
		result = prime * result + position;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Slot))
			return false;
		Slot other = (Slot) obj;
		if (direction != other.direction)
			return false;
		if (isDestSlot != other.isDestSlot)
			return false;
		if (isSourceSlot != other.isSourceSlot)
			return false;
		if (pieces == null) {
			if (other.pieces != null)
				return false;
		} else if (!pieces.equals(other.pieces))
			return false;
		if (position != other.position)
			return false;
		return true;
	}

	public Moves getMoves() {
		Moves moves = new Moves();
		moves.addAll(game.getCurrentTurn().getPotentialMoves().getMovesForStartSlot(this));
		moves.addAll(game.getCurrentTurn().getPotentialMoves().getMovesForEndSlot(this));
		return moves;
	}
}
