package com.anthonyatkins.simplebackgammon.model;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
	private final GameDice dice;
	private final int color;
	private final Date created;

	private Slot startSlot;

	public Turn(Player player, Game game, int color) {
		this.player = player;
		this.color = color;
		this.dice = new GameDice(color, this);
		this.game = game;
		this.created = new Date();
		game.getGameLog().add(this);
	}
	
	public Turn(Player player, Game game, int color, Date created) {
		this.player = player;
		this.color = color;
		this.dice = new GameDice(color, this);
		this.game = game;
		this.created = created;
		game.getGameLog().add(this);
	}

	public Turn(Turn existingTurn, Game game) {
		this.player = existingTurn.getPlayer();
		this.game = game;
		this.color = existingTurn.getColor();
		game.getGameLog().add(this);
		this.dice = new GameDice(existingTurn.dice, this);
		this.created = new Date();
		for (Move move : existingTurn.moves) {
			new TurnMove(move,this);
		}
	}

	public Turn(Player player, Game game, int color, Date created, SimpleDice dice) {
		this.player = player;
		this.game = game;
		this.color = color;
		this.created = created;
		this.dice = new GameDice(dice,this);
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
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((dice == null) ? 0 : dice.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((moves == null) ? 0 : moves.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Turn))
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
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (moves == null) {
			if (other.moves != null)
				return false;
		} else if (!moves.equals(other.moves))
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

	public GameDice getDice() {
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
			clearStartSlot();
			game.findAllPotentialMoves();
		} catch (InvalidMoveException e) {
			Log.e(getClass().getName(), "Can't pick move:",e);
		}
	}

	public Moves getPotentialMoves() {
		return this.potentialMoves;
	}

	public void makeMove(int startSlotPosition, int endSlotPosition) throws InvalidMoveException {
		for (Move potentialMove : getPotentialMoves()) {
			if (potentialMove.getStartSlot().getPosition() == startSlotPosition && potentialMove.getEndSlot().getPosition() == endSlotPosition) {
				makeMove(potentialMove);
				return;
			}
		}
	}


	public void makeMove(Move move) throws InvalidMoveException {
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
		}
		else {
			Log.e(getClass().getName(), "Couldn't move a piece from slot " + startSlot.getPosition() + " because there are no pieces in that slot.");
			throw(new InvalidMoveException("Couldn't move a piece from slot " + startSlot.getPosition() + " because there are no pieces in that slot."));
		}
	}

	public void undoMove() {
		// undo moves from this turn if there are any
		if (this.getMoves().size() > 0) {
			// Remove the last move from the list of moves
			Move lastMove = this.getMoves().get(this.getMoves().size() - 1);
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
		}

		clearStartSlot();
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
}
