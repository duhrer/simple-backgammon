package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.anthonyatkins.simplebackgammon.Constants;

public class Board {
	public Pieces whitePieces;
	public Pieces blackPieces;

	Context context; 
	
	// array of play slots where 0 is the black start point and 20 is the white start point
	public List<Slot> playSlots = new ArrayList<Slot>();

	public Bar bar;
	
	public Dugout whiteOut;
	public Dugout blackOut;
	
	public Pit leftPit;
	public Pit rightPit;
	
	/* an array of piece locations */
	int[] defaultPieceConfiguration = {0,2,0,0,0,0,-5,0,-3,0,0,0,5,-5,0,0,0,3,0,5,0,0,0,0,-2,0,0,0};

	// initialize the board
	public Board(Game game) {
		initialize(game);
		
		try {
			initializeSlots();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initialize(Game game) {
		bar = new Bar(game);  // the holding slot for "bumped" pieces
		whiteOut = new Dugout(-1, Constants.WHITE,game); // the holding slot for white pieces that have made it home
		blackOut = new Dugout(24, Constants.BLACK,game); // ditto for black pieces
		leftPit  = new Pit(game.getWhitePlayer().dice);
		rightPit = new Pit(game.getBlackPlayer().dice);
		whitePieces = game.getWhitePlayer().pieces;
		blackPieces = game.getBlackPlayer().pieces;
		
		for (int a=0; a<6; a++) {
			playSlots.add(new Slot(Slot.UP,a,game));
		}
		for (int a=6; a<12; a++) {
			playSlots.add(new Slot(Slot.UP,a,game));
		}
		for (int a=12; a<18; a++) {
			playSlots.add(new Slot(Slot.DOWN,a,game));
		}
		for (int a=18; a<24; a++) {
			playSlots.add(new Slot(Slot.DOWN,a,game));
		}
	}

	public Board(Board board, Game game) {
		initialize(game);
		try {
			initializeSlots(board.getBoardState());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void initializeSlots() throws Exception {
		initializeSlots(defaultPieceConfiguration);
	}
	public ArrayList<Integer> getBoardState() {
		ArrayList<Integer> boardState = new ArrayList<Integer>();
		boardState.add(this.whiteOut.pieces.size());
		for (int a=0;a<=23;a++) {
			Slot slot = this.playSlots.get(a);
			if (slot != null && slot.pieces.size() > 0) {
				boardState.add(slot.pieces.size() * slot.pieces.first().color);
			}
			else {
				boardState.add(0);
			}
		}
		
		boardState.add(this.blackOut.pieces.size());
		int blackPiecesOnBar = 0;
		int whitePiecesOnBar = 0;
		Iterator<Piece> pieceIterator = this.bar.pieces.iterator();
		while (pieceIterator.hasNext()) {
			Piece piece = pieceIterator.next();
			if (piece.color == Constants.BLACK) { blackPiecesOnBar++; }
			else { whitePiecesOnBar++; }
		}
		
		
		boardState.add(blackPiecesOnBar);
		boardState.add(whitePiecesOnBar);
		return boardState;
	}


	/**
	 * Convenience method to allow initializing board state from int[] instead of List<Integer>
	 * @param boardState An array of integers representing the state of the pieces on the board.
	 */
	public void initializeSlots(int[] boardState) {
		List<Integer> boardStateList = new ArrayList<Integer>();
		for (int a=0; a<boardState.length; a++) {
			boardStateList.add(new Integer(boardState[a]));
		}
		try {
			initializeSlots(boardStateList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initializeSlots(List<Integer> boardState) throws Exception {
		if (boardState.size() != 28) {
			throw(new Exception("Board can only be initialized with exactly 28 values (24 slots, 2 dugouts, the black bar, and the white bar)."));
		}
		else {
			// Initialize the White dugout
			whiteOut.pieces.clear();
			if (Math.abs(boardState.get(0)) > 0) {
				whiteOut.pieces.addMultiple(Math.abs(boardState.get(0)), Constants.WHITE, 0);
			}
			// Initialize the playSlots
			for (int a=0; a<24; a++) {
				playSlots.get(a).pieces.clear();
				if (Math.abs(boardState.get(a+1)) > 0) {
					playSlots.get(a).pieces.addMultiple(Math.abs(boardState.get(a+1)),(int) Math.signum(boardState.get(a+1)),a);
				}
			}
			// Initialize the Black Dugout
			blackOut.pieces.clear();
			if (Math.abs(boardState.get(25)) > 0) {
				blackOut.pieces.addMultiple(Math.abs(boardState.get(25)), Constants.BLACK, 0);
			}
			
			// remove any existing pieces from the bar
			bar.pieces.clear();

			// Initialize the bar for black pieces (slot 26)
			if (Math.abs(boardState.get(26)) > 0) { 
				bar.pieces.addMultiple(Math.abs(boardState.get(26)), Constants.BLACK, 0);
			}

			// Initialize the bar for white pieces (slot 27)
			if (Math.abs(boardState.get(27)) > 0) {
				bar.pieces.addMultiple(Math.abs(boardState.get(27)), Constants.WHITE, 0);
			}
		}
		
		
		// Link the newly created pieces to the players who own them
		whitePieces.clear();
		blackPieces.clear();
		
		// The dugouts are easy, they can only contain one color
		Iterator<Piece> blackOutIterator = blackOut.pieces.iterator();
		while (blackOutIterator.hasNext()) { blackPieces.add(blackOutIterator.next()); }

		Iterator<Piece> whiteOutIterator = whiteOut.pieces.iterator();
		while (whiteOutIterator.hasNext()) { whitePieces.add(whiteOutIterator.next()); }
		
		
		// The slots can contain either color, so we have to check it
		for (int b=0; b <24; b++) {
			Iterator<Piece> slotIterator = playSlots.get(b).pieces.iterator();
			while (slotIterator.hasNext()) {
				Piece tempPiece = slotIterator.next();
				if (tempPiece.color == Constants.BLACK) { blackPieces.add(tempPiece); }
				else { whitePieces.add(tempPiece); }
			}
		}
		
		// The bar can contain both colors, so we have to check the color
		Iterator<Piece> slotIterator = bar.pieces.iterator();
		while (slotIterator.hasNext()) {
			Piece tempPiece = slotIterator.next();
			if (tempPiece.color == Constants.BLACK) { blackPieces.add(tempPiece); }
			else { whitePieces.add(tempPiece); }
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bar == null) ? 0 : bar.hashCode());
		result = prime * result
				+ ((blackOut == null) ? 0 : blackOut.hashCode());
		result = prime * result
				+ ((blackPieces == null) ? 0 : blackPieces.hashCode());
		result = prime * result + ((leftPit == null) ? 0 : leftPit.hashCode());
		result = prime * result
				+ ((playSlots == null) ? 0 : playSlots.hashCode());
		result = prime * result
				+ ((rightPit == null) ? 0 : rightPit.hashCode());
		result = prime * result
				+ ((whiteOut == null) ? 0 : whiteOut.hashCode());
		result = prime * result
				+ ((whitePieces == null) ? 0 : whitePieces.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Board))
			return false;
		Board other = (Board) obj;
		if (bar == null) {
			if (other.bar != null)
				return false;
		} else if (!bar.equals(other.bar))
			return false;
		if (blackOut == null) {
			if (other.blackOut != null)
				return false;
		} else if (!blackOut.equals(other.blackOut))
			return false;
		if (blackPieces == null) {
			if (other.blackPieces != null)
				return false;
		} else if (!blackPieces.equals(other.blackPieces))
			return false;
		if (leftPit == null) {
			if (other.leftPit != null)
				return false;
		} else if (!leftPit.equals(other.leftPit))
			return false;
		if (playSlots == null) {
			if (other.playSlots != null)
				return false;
		} else if (!playSlots.equals(other.playSlots))
			return false;
		if (rightPit == null) {
			if (other.rightPit != null)
				return false;
		} else if (!rightPit.equals(other.rightPit))
			return false;
		if (whiteOut == null) {
			if (other.whiteOut != null)
				return false;
		} else if (!whiteOut.equals(other.whiteOut))
			return false;
		if (whitePieces == null) {
			if (other.whitePieces != null)
				return false;
		} else if (!whitePieces.equals(other.whitePieces))
			return false;
		return true;
	}
	
	
}
