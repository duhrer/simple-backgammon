package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.anthonyatkins.simplebackgammon.Constants;

public class Board {
	private Pieces whitePieces;
	private Pieces blackPieces;

	Context context; 
	
	// array of play slots where 0 is the black start point and 20 is the white start point
	private List<Slot> playSlots = new ArrayList<Slot>();

	private Bar bar;
	
	private Dugout whiteOut;
	private Dugout blackOut;
	
	private Pit leftPit;
	private Pit rightPit;
	
	/* an array of piece locations */
	static int[] defaultPieceConfiguration = {0,2,0,0,0,0,-5,0,-3,0,0,0,5,-5,0,0,0,3,0,5,0,0,0,0,-2,0,0,0};

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
		setBar(new Bar(game));  // the holding slot for "bumped" pieces
		setWhiteOut(new Dugout(-1, Constants.WHITE,game)); // the holding slot for white pieces that have made it home
		setBlackOut(new Dugout(24, Constants.BLACK,game)); // ditto for black pieces
		setLeftPit(new Pit(game.getWhitePlayer().getDice()));
		setRightPit(new Pit(game.getBlackPlayer().getDice()));
		setWhitePieces(game.getWhitePlayer().getPieces());
		setBlackPieces(game.getBlackPlayer().getPieces());
		
		for (int a=0; a<6; a++) {
			getPlaySlots().add(new Slot(Slot.UP,a,game));
		}
		for (int a=6; a<12; a++) {
			getPlaySlots().add(new Slot(Slot.UP,a,game));
		}
		for (int a=12; a<18; a++) {
			getPlaySlots().add(new Slot(Slot.DOWN,a,game));
		}
		for (int a=18; a<24; a++) {
			getPlaySlots().add(new Slot(Slot.DOWN,a,game));
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
		boardState.add(this.getWhiteOut().pieces.size());
		for (int a=0;a<=23;a++) {
			Slot slot = this.getPlaySlots().get(a);
			if (slot != null && slot.pieces.size() > 0) {
				boardState.add(slot.pieces.size() * slot.pieces.first().color);
			}
			else {
				boardState.add(0);
			}
		}
		
		boardState.add(this.getBlackOut().pieces.size());
		int blackPiecesOnBar = 0;
		int whitePiecesOnBar = 0;
		Iterator<Piece> pieceIterator = this.getBar().pieces.iterator();
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
			getWhiteOut().pieces.clear();
			if (Math.abs(boardState.get(0)) > 0) {
				getWhiteOut().pieces.addMultiple(Math.abs(boardState.get(0)), Constants.WHITE, 0);
			}
			// Initialize the playSlots
			for (int a=0; a<24; a++) {
				getPlaySlots().get(a).pieces.clear();
				if (Math.abs(boardState.get(a+1)) > 0) {
					getPlaySlots().get(a).pieces.addMultiple(Math.abs(boardState.get(a+1)),(int) Math.signum(boardState.get(a+1)),a);
				}
			}
			// Initialize the Black Dugout
			getBlackOut().pieces.clear();
			if (Math.abs(boardState.get(25)) > 0) {
				getBlackOut().pieces.addMultiple(Math.abs(boardState.get(25)), Constants.BLACK, 0);
			}
			
			// remove any existing pieces from the bar
			getBar().pieces.clear();

			// Initialize the bar for black pieces (slot 26)
			if (Math.abs(boardState.get(26)) > 0) { 
				getBar().pieces.addMultiple(Math.abs(boardState.get(26)), Constants.BLACK, 0);
			}

			// Initialize the bar for white pieces (slot 27)
			if (Math.abs(boardState.get(27)) > 0) {
				getBar().pieces.addMultiple(Math.abs(boardState.get(27)), Constants.WHITE, 0);
			}
		}
		
		
		// Link the newly created pieces to the players who own them
		getWhitePieces().clear();
		getBlackPieces().clear();
		
		// The dugouts are easy, they can only contain one color
		Iterator<Piece> blackOutIterator = getBlackOut().pieces.iterator();
		while (blackOutIterator.hasNext()) { getBlackPieces().add(blackOutIterator.next()); }

		Iterator<Piece> whiteOutIterator = getWhiteOut().pieces.iterator();
		while (whiteOutIterator.hasNext()) { getWhitePieces().add(whiteOutIterator.next()); }
		
		
		// The slots can contain either color, so we have to check it
		for (int b=0; b <24; b++) {
			Iterator<Piece> slotIterator = getPlaySlots().get(b).pieces.iterator();
			while (slotIterator.hasNext()) {
				Piece tempPiece = slotIterator.next();
				if (tempPiece.color == Constants.BLACK) { getBlackPieces().add(tempPiece); }
				else { getWhitePieces().add(tempPiece); }
			}
		}
		
		// The bar can contain both colors, so we have to check the color
		Iterator<Piece> slotIterator = getBar().pieces.iterator();
		while (slotIterator.hasNext()) {
			Piece tempPiece = slotIterator.next();
			if (tempPiece.color == Constants.BLACK) { getBlackPieces().add(tempPiece); }
			else { getWhitePieces().add(tempPiece); }
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getBar() == null) ? 0 : getBar().hashCode());
		result = prime * result
				+ ((getBlackOut() == null) ? 0 : getBlackOut().hashCode());
		result = prime * result
				+ ((getBlackPieces() == null) ? 0 : getBlackPieces().hashCode());
		result = prime * result + ((getLeftPit() == null) ? 0 : getLeftPit().hashCode());
		result = prime * result
				+ ((getPlaySlots() == null) ? 0 : getPlaySlots().hashCode());
		result = prime * result
				+ ((getRightPit() == null) ? 0 : getRightPit().hashCode());
		result = prime * result
				+ ((getWhiteOut() == null) ? 0 : getWhiteOut().hashCode());
		result = prime * result
				+ ((getWhitePieces() == null) ? 0 : getWhitePieces().hashCode());
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
		if (getBar() == null) {
			if (other.getBar() != null)
				return false;
		} else if (!getBar().equals(other.getBar()))
			return false;
		if (getBlackOut() == null) {
			if (other.getBlackOut() != null)
				return false;
		} else if (!getBlackOut().equals(other.getBlackOut()))
			return false;
		if (getBlackPieces() == null) {
			if (other.getBlackPieces() != null)
				return false;
		} else if (!getBlackPieces().equals(other.getBlackPieces()))
			return false;
		if (getLeftPit() == null) {
			if (other.getLeftPit() != null)
				return false;
		} else if (!getLeftPit().equals(other.getLeftPit()))
			return false;
		if (getPlaySlots() == null) {
			if (other.getPlaySlots() != null)
				return false;
		} else if (!getPlaySlots().equals(other.getPlaySlots()))
			return false;
		if (getRightPit() == null) {
			if (other.getRightPit() != null)
				return false;
		} else if (!getRightPit().equals(other.getRightPit()))
			return false;
		if (getWhiteOut() == null) {
			if (other.getWhiteOut() != null)
				return false;
		} else if (!getWhiteOut().equals(other.getWhiteOut()))
			return false;
		if (getWhitePieces() == null) {
			if (other.getWhitePieces() != null)
				return false;
		} else if (!getWhitePieces().equals(other.getWhitePieces()))
			return false;
		return true;
	}

	public List<Slot> getPlaySlots() {
		return playSlots;
	}

	public void setPlaySlots(List<Slot> playSlots) {
		this.playSlots = playSlots;
	}

	public Pit getLeftPit() {
		return leftPit;
	}

	public void setLeftPit(Pit leftPit) {
		this.leftPit = leftPit;
	}

	public Pit getRightPit() {
		return rightPit;
	}

	public void setRightPit(Pit rightPit) {
		this.rightPit = rightPit;
	}

	public Dugout getWhiteOut() {
		return whiteOut;
	}

	public void setWhiteOut(Dugout whiteOut) {
		this.whiteOut = whiteOut;
	}

	public Bar getBar() {
		return bar;
	}

	public void setBar(Bar bar) {
		this.bar = bar;
	}

	public Dugout getBlackOut() {
		return blackOut;
	}

	public void setBlackOut(Dugout blackOut) {
		this.blackOut = blackOut;
	}

	public Pieces getBlackPieces() {
		return blackPieces;
	}

	public void setBlackPieces(Pieces blackPieces) {
		this.blackPieces = blackPieces;
	}

	public Pieces getWhitePieces() {
		return whitePieces;
	}

	public void setWhitePieces(Pieces whitePieces) {
		this.whitePieces = whitePieces;
	}
	
	
}
