package com.anthonyatkins.simplebackgammon.model;

public class Piece implements Comparable<Piece> {
	public int color;
	public int position;

	public Piece (int color, int position) {
		this.color = color;
		this.position = position;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + color;
		result = prime * result + position;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Piece other = (Piece) obj;
		if (color != other.color)
			return false;
		if (position != other.position)
			return false;
		return true;
	}

	public int compareTo(Piece otherPiece) {
		return this.position - otherPiece.position;
	}
}
