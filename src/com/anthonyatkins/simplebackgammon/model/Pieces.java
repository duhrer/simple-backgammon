package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;

public class Pieces extends ArrayList<Piece> {
	public boolean addMultiple(int pieces, int color, int position) {
		boolean status = true;
		for (int a=0; a<pieces; a++) {
			boolean currentStatus = super.add(new Piece(color, position));
			if (!currentStatus) { status = currentStatus; }
		}
		return status;
	}
	public Piece first() {
		double minPosition = 23;
		Piece minPiece = null;
		for (Piece piece: this) {
			if (piece.position <= minPosition) {
				minPosition = piece.position;
				minPiece = piece;
			}
		}
		return minPiece;
	}
	
	public Piece last() {
		double maxPosition = 0;
		Piece maxPiece = null;
		for (Piece piece: this) {
			if (piece.position >= maxPosition) {
				maxPosition = piece.position;
				maxPiece = piece;
			}
		}
		return maxPiece;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Pieces))
			return false;
		Pieces other = (Pieces) obj;
		
		if (other.size() != this.size()) return false;
		
		// Since pieces are ordered by position, we should be able to compare each piece in the list in turn
		for (int a=0; a<this.size(); a++) {
			if (!this.get(a).equals(other.get(a))) { return false; }
		}
		
		return true;
	}
	
	
}
