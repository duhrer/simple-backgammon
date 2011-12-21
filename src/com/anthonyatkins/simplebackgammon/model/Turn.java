package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.exception.InvalidMoveException;

import android.util.Log;

public class Turn implements Comparable {
	// Database setup information
	public static final String _ID = "_id";
	public static final String GAME = "game";
	public static final String PLAYER = "player";
	public static final String COLOR = "color";
	public static final String DIE_ONE = "d1";
	public static final String DIE_TWO = "d2";
	public static final String START_SLOT = "start_slot";
	public static final String CREATED = "created";

	public static final String TABLE_NAME = "turn";
	public static final String TABLE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + " (" +
			_ID + " integer primary key, " + 
			GAME + " integer, "
			+ PLAYER + " integer, " + 
			COLOR + " integer, " + 
			DIE_ONE + " integer, " + 
			DIE_TWO + " integer, " + 
			START_SLOT + " integer, " + 
			CREATED + " datetime "
			+ ");";

	public static final String[] COLUMNS = { 
		_ID, 
		GAME, 
		PLAYER, 
		COLOR, 
		DIE_ONE, 
		DIE_TWO, 
		START_SLOT,
		CREATED 
	};

	private long id = -1;
	private Moves moves = new Moves();
	private Moves potentialMoves = new Moves();

	private final Player player;
	private final Game game;
	private final SimpleDice dice;
	private final int color;
	private final Date created;

	private Slot startSlot;

	public Turn(Player player, Game game, int color) {
		this.player = player;
		this.color = color;
		this.dice = new SimpleDice(color);
		this.game = game;
		this.created = new Date();
		game.getGameLog().add(this);
	}
	
	public Turn(Player player, Game game, int color, Date created) {
		this.player = player;
		this.color = color;
		this.dice = new SimpleDice(color);
		this.game = game;
		this.created = created;
		game.getGameLog().add(this);
	}

	public Turn(Turn existingTurn, Game game) {
		this.player = existingTurn.getPlayer();
		this.game = game;
		this.color = existingTurn.getColor();
		game.getGameLog().add(this);
		this.dice = new SimpleDice(existingTurn.dice);
		this.created = new Date();
		for (Move move : existingTurn.moves) {
			new TurnMove(move,this);
		}

		this.startSlot = existingTurn.getStartSlot();
	}

	public Turn(Player player, Game game, int color, Date created, SimpleDice dice) {
		this.player = player;
		this.game = game;
		this.color = color;
		this.created = created;
		this.dice = new SimpleDice(dice);
		game.getGameLog().add(this);
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + color;
		result = prime * result + ((dice == null) ? 0 : dice.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((moves == null) ? 0 : moves.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result
				+ ((potentialMoves == null) ? 0 : potentialMoves.hashCode());
		result = prime * result
				+ ((startSlot == null) ? 0 : startSlot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Turn other = (Turn) obj;
		if (color != other.color)
			return false;
		if (dice == null) {
			if (other.dice != null)
				return false;
		} else if (!dice.equals(other.dice))
			return false;
		if (id != other.id)
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
		if (potentialMoves == null) {
			if (other.potentialMoves != null)
				return false;
		} else if (!potentialMoves.equals(other.potentialMoves))
			return false;
		if (startSlot == null) {
			if (other.startSlot != null)
				return false;
		} else if (!startSlot.equals(other.startSlot))
			return false;
		return true;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void addMoves(List<Move> moves2) {
		for (Move move : moves2) {
			if (move instanceof TurnMove) {
				moves.add(move);
			}
			else {
				moves.add(new TurnMove(move,this));
			}
		}
	}

	public Date getCreated() {
		return created;
	}

	public Moves getMoves() {
		return moves;
	}

	public Player getPlayer() {
		return player;
	}

	public SimpleDice getDice() {
		return dice;
	}

	public int getColor() {
		return color;
	}

	public boolean movesLeft() {
		return potentialMoves.size() > 0;
	}

	public void setSelectedMove(Move potentialMove) {

	}

	public Slot getStartSlot() {
		return startSlot;
	}

	public void setStartSlot(Slot startSlot) {
		this.startSlot = startSlot;
	}

	public void clearStartSlot() {
		this.startSlot = null;
	}

	public void pickMove(Move potentialMove) {
		try {
			makeMove(potentialMove);
		} catch (InvalidMoveException e) {
			Log.e(getClass().getName(), "Can't pick move:",e);
		}
	}

	public Moves getPotentialMoves() {
		return this.potentialMoves;
	}

	public void makeMove(int startSlotPosition, int endSlotPosition) throws InvalidMoveException {
		for (Move potentialMove : getPotentialMoves()) {
			if (!potentialMove.getDie().isUsed() && potentialMove.getStartSlot().getPosition() == startSlotPosition && potentialMove.getEndSlot().getPosition() == endSlotPosition) {
				makeMove(potentialMove);
				return;
			}
		}
	}


	public void makeMove(Move move) throws InvalidMoveException {
		if (move.getDie().isUsed()) throw new InvalidMoveException("Couldn't move using die because it has already been used!");
		
		// This shouldn't be needed but just in case.
		setStartSlot(move.getStartSlot());
		Piece pieceToMove = null;
		if (move.getStartSlot().equals(game.getBoard().getBar())) {
			// get the first piece of my color
			Iterator<Piece> pieceIterator = game.getBoard().getBar().getPieces().iterator();
			while (pieceIterator.hasNext()) {
				Piece thisPiece = pieceIterator.next();
				if (thisPiece.color == this.getColor()) {
					pieceToMove = game.getBoard().getBar().removePiece(thisPiece);
					break;
				}
			}
		} else {
			pieceToMove = move.getStartSlot().removePiece();
		}

		if (pieceToMove != null) {
			// add the piece to the new location
			if (move.getEndSlot().equals(game.getBoard().getBlackOut())) {
				game.getBoard().getBlackOut().addPiece(pieceToMove);
			} else if (move.getEndSlot().equals(game.getBoard().getWhiteOut())) {
				game.getBoard().getWhiteOut().addPiece(pieceToMove);
			} else {
				// "bump" a piece from the slot if there's one of the opposite
				// color there
				if (move.getEndSlot().getPieces().size() > 0) {
					if (move.getEndSlot().getPieces().first().color != this.getColor()) {
						move.setPieceBumped(true);
						Piece bumpedPiece = move.getEndSlot().removePiece();
						
						game.getBoard().getBar().addPiece(bumpedPiece);
					}
				}
				
			}
			
			// Now add the new piece to the slot
			move.getEndSlot().addPiece(pieceToMove);
			
			// Flag the die associated with this move as used
				
			move.getDie().setUsed();

			new TurnMove(move, this);
			
			clearStartSlot();
			findAllPotentialMoves();
		}
		else {
			Log.e(getClass().getName(), "Couldn't move a piece from slot " + startSlot.getPosition() + " because there are no pieces in that slot.");
			throw(new InvalidMoveException("Couldn't move a piece from slot " + startSlot.getPosition() + " because there are no pieces in that slot."));
		}
	}

	public void undoMove() throws InvalidMoveException {
		// undo moves from this turn if there are any
		if (this.getMoves().size() > 0) {
			// Remove the last move from the list of moves
			Move lastMove = this.getMoves().get(this.getMoves().size() - 1);
			
			if (!lastMove.getDie().isUsed()) throw new InvalidMoveException("Couldn't undo move because it hasn't happened!");

			this.getMoves().remove(lastMove);

			// Undo the last move
			Piece undoPiece = lastMove.getEndSlot().removePiece();
			lastMove.getStartSlot().addPiece(undoPiece);

			// If we bumped a piece in the last move, put it back.
			if (lastMove.isPieceBumped()) {
				Bar bar = game.getBoard().getBar();
				for (Piece piece : bar.getPieces()) {
					if (piece.color != this.getColor()) {
						bar.removePiece(piece);
						lastMove.getEndSlot().addPiece(piece);
						break;
					}
				}
			}

			lastMove.getDie().setUsed(false);
			
			clearStartSlot();
			findAllPotentialMoves();
		}
	}

	public boolean movesLeftForDie(GameDie gameDie) {
		for (Move move : potentialMoves) {
			if (move.getDie().equals(gameDie))
				return true;
		}

		return false;
	}

	@Override
	public int compareTo(Object another) {
		if (another instanceof Turn) {
			/* sort by date created */
			if (this.created.after(((Turn) another).created)) { return 1; }
			else if (this.created.equals(((Turn) another).created)) { return 0; }
			else if (this.created.before(((Turn) another).created)) { return -1; }
		}

		/* otherwise, they are sorted at the same level (should not be possible) */
		return 0;
	}

	public void setStartSlot(int startSlot) {
		if (startSlot >= 0 && game.getBoard().getPlaySlots().size() >= startSlot) {
			Slot slot = game.getBoard().getPlaySlots().get(startSlot);
			setStartSlot(slot);
		}
	}
	
	/**
	 * Determine the slots the active player can move from.  Only used for normal slots, and not for the bar or dugouts.
	 * @return The list of slots that have valid moves.
	 */
	public Moves findAllPotentialMoves() {
		// If we have pieces on the bar, that's our only option at first
		if (game.getBoard().getBar().containsPlayerPieces(this.getColor())) {
			this.setStartSlot(game.getBoard().getBar());
			getAvailableMovesFromBar();
		}
		else {
			potentialMoves.clear();
			
			/* this detects all non-bar moves, there should be no others when we run this */
			List<Integer> uniquePositions = new ArrayList<Integer>();
			Pieces pieces = this.getColor() == Constants.BLACK ? game.getBoard().getBlackPieces() : game.getBoard().getWhitePieces();
			Iterator<Piece> pieceIterator = pieces.iterator();
			while (pieceIterator.hasNext()) {
				Piece piece = pieceIterator.next();
				int position = piece.position;
				if (position >= 0 && position <= 23 && !uniquePositions.contains(position)) { uniquePositions.add(position); }
			}
			
			for (int b=0; b < uniquePositions.size(); b++) {
				Moves slotMoves = findAvailableMovesFromSlot(game.getBoard().getPlaySlots().get(uniquePositions.get(b)));
				potentialMoves.addAll(slotMoves);
			}
		}
		
		return potentialMoves;
	}
			
	/**
	 * Tag the moves that are possible from a given slot.
	 * @param slot  The slot to check.
	 */
	private Moves findAvailableMovesFromSlot(Slot slot) {
		Moves slotMoves = new Moves();
		int slotPosition = slot.getPosition();
		List<Integer> uniqueDieValues = new ArrayList<Integer>();

		
		Iterator<SimpleDie> dieIterator = this.getDice().iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			if (!die.isUsed() && !uniqueDieValues.contains(new Integer(die.getValue()))) {
				/* If we have doubles, we only want to add moves for the first die */
				uniqueDieValues.add(new Integer(die.getValue()));
				int diePosition = slotPosition + (this.getColor() * die.getValue());
				if (diePosition >= 0 && diePosition <= 23) {
						Slot destinationSlot = game.getBoard().getPlaySlots().get(diePosition);
						if (!destinationSlot.isBlocked(this.getColor())) { 
							Move potentialMove = new Move(slot,destinationSlot,die, this.getPlayer());
							slotMoves.add(potentialMove);
						}
				}
				else if (game.playerCanMoveOut()) {
					// If the piece is in the right position, it can always move out.
					// If it's the trailing edge of the player's pieces, it can move out with any roll high enough
					if (this.getColor() == Constants.BLACK) { 
						
						if (diePosition == 24 || (game.getBoard().getBlackPieces().first().position == slotPosition && diePosition > 24)) {
							Move potentialMove = new Move(slot,game.getBoard().getBlackOut(),die, this.getPlayer());
							slotMoves.add(potentialMove);
						}
					}
					else { 
						if (diePosition == -1 || (game.getBoard().getWhitePieces().last().position == slotPosition && diePosition < -1)) {
							Move potentialMove = new Move(slot,game.getBoard().getWhiteOut(),die, this.getPlayer());
							slotMoves.add(potentialMove);
						}
					}
				}
			}
		}
		
		return slotMoves;
	}
	
	/**
	 * Get the list of slots the active player can move to from the bar and add them to their moves.
	 */
	private void getAvailableMovesFromBar() {
		int startSlotPosition = 0;
		List<Integer> uniqueDieValues = new ArrayList<Integer>();

		/* we never allow moves from the bar and anywhere else, so clear the list of moves out */
		Moves potentialMoves = this.getPotentialMoves();
		potentialMoves.clear();
		
		if (game.getBoard().getBar().containsPlayerPieces(this.getColor())) {
			if (this.getColor() == Constants.BLACK) { startSlotPosition = -1; }
			else { startSlotPosition = 24; }

			Iterator<SimpleDie> dieIterator = this.getDice().iterator();
			while (dieIterator.hasNext()) {
				SimpleDie die = dieIterator.next();
				if (!die.isUsed() && !uniqueDieValues.contains(new Integer(die.getValue()))) {
					/* If we have doubles, we only want to add moves for the first die */
					uniqueDieValues.add(new Integer(die.getValue()));
					
					int dieValue = die.getValue();
					int dieSlotPosition = startSlotPosition + (this.getColor() * dieValue);
					Slot destinationSlot = game.getBoard().getPlaySlots().get(dieSlotPosition);
					if (!destinationSlot.isBlocked(this.getColor())) {
						Move potentialMove = new Move(game.getBoard().getBar(), destinationSlot,die, this.getPlayer());
						potentialMoves.add(potentialMove);
					}
				}			
			}
		}
	}

}
