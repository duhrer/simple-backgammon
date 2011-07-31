package com.anthonyatkins.simplebackgammon.model;

import java.util.Iterator;


public class Bar extends Slot{
	public Bar(Game game) {
		super(Slot.NONE,0, game);
	}
	
	public boolean containsPlayerPieces(int playerColor) {
		Iterator<Piece> pieceIterator = pieces.iterator();
		boolean hasPieces = false;
		
		while (pieceIterator.hasNext()) {
			Piece piece = pieceIterator.next();
			if (piece.color == playerColor) { hasPieces = true; } 
		}
		return hasPieces;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Bar))
			return false;
		
		return super.equals(obj);
	}
}
