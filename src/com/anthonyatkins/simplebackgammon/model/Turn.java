package com.anthonyatkins.simplebackgammon.model;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class Turn {
	// Database setup information
	public static final String _ID            = "_id";
	public static final String GAME 		  = "game";
	public static final String PLAYER 		  = "player";
	public static final String COLOR		  = "color";
	public static final String DIE_ONE	      = "d1";
	public static final String DIE_TWO	      = "d2";
	public static final String CREATED        = "created";
	
	public static final String TABLE_NAME = "turn";
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		GAME + " integer, " +
		PLAYER + " integer, " +
		COLOR + " integer, " +
		DIE_ONE + " integer, " +
		DIE_TWO + " integer, " +
		CREATED + " datetime " +
		");";
	
	public static final String[] COLUMNS = {
			_ID,
			GAME,
			PLAYER,
			COLOR,
			DIE_ONE,
			DIE_TWO,
			CREATED
	};
	
	private long id = -1;
	private Moves moves = new Moves();
	private Moves potentialMoves = new Moves();
	
	private final Player player;
	private final Game game;
	private final GameDice dice;
	private final int color;
	private final Date created = new Date();
	
	private Slot startSlot;

	public Turn(Player player, Game game, int color) {
		this.player = player;
		this.color = color;
		this.dice = new GameDice(color,this);
		this.game = game;
		game.getGameLog().add(this);
	}
	
	public Turn(Turn existingTurn, Game game) {
		this.player = existingTurn.getPlayer();
		this.game = game;
		this.color = existingTurn.getColor();
		game.getGameLog().add(this);
		this.dice = new GameDice(existingTurn.dice,this);
		for (Move move: existingTurn.moves) {
			moves.add(new Move(move));
		}
	}
	
	public Turn(Turn existingTurn) {
		this.player = existingTurn.getPlayer();
		this.game = existingTurn.getGame();
		this.color = existingTurn.getColor();
		game.getGameLog().add(this);
		this.dice = new GameDice(existingTurn.dice,this);
		for (Move move: existingTurn.moves) {
			moves.add(new Move(move));
		}
	}
	
	public Game getGame() {
		return this.game;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dice == null) ? 0 : dice.hashCode());
		result = prime * result + ((moves == null) ? 0 : moves.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Turn))
			return false;
		Turn other = (Turn) obj;
		if (dice == null) {
			if (other.dice != null)
				return false;
		} else if (!dice.equals(other.dice))
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
		return true;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void addMoves(List<Move> moves2) {
		for (Move move: moves2) moves.add(move);
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
		moves.add(potentialMove);
		makeMove(potentialMove);
		clearStartSlot();
		game.findAllPotentialMoves();
	}

	public Moves getPotentialMoves() {
		return this.potentialMoves;
	}
	
	private void makeMove(Move move) {
		// This shouldn't be needed but just in case.
		setStartSlot(move.getStartSlot());
		Piece pieceToMove = null;
		if (move.getStartSlot().equals(game.getBoard().getBar())) {
			// get the first piece of my color and move it to the destination, then invalidate the bar and the destination
			Iterator<Piece> pieceIterator = game.getBoard().getBar().getPieces().iterator();
			while (pieceIterator.hasNext()) {
				Piece thisPiece = pieceIterator.next();	
				if (thisPiece.color == game.getCurrentTurn().getColor()) {
					pieceToMove = thisPiece;
					break;
				}
			}
		}
		else {
			pieceToMove = move.getStartSlot().getPieces().get(0);
		}
		
		if (pieceToMove != null) {
			// take the piece out of its old location
			move.getStartSlot().removePiece(pieceToMove);

			// Flag the die associated with this move as used
			move.getDie().setUsed();
			new TurnMove(move, game.getCurrentTurn());

			
			// add the piece to the new location
			if (move.getEndSlot().equals(game.getBoard().getBlackOut())) {
				game.getBoard().getBlackOut().addPiece(pieceToMove);
			}
			else if (move.getEndSlot().equals(game.getBoard().getWhiteOut())) {
				game.getBoard().getWhiteOut().addPiece(pieceToMove);
			}
			else {
				// "bump" a piece from the slot if there's one of the opposite color there
				if (move.getEndSlot().getPieces().size() > 0) {
					Piece targetPiece = move.getEndSlot().getPieces().first();
					if (targetPiece.color != game.getCurrentTurn().getColor()) {
						move.setPieceBumped(true);
						move.getEndSlot().removePiece(targetPiece);
						
						// The destination slot is already invalidated later, so we don't need to do it here.
						game.getBoard().getBar().addPiece(targetPiece);
					}
				}
				
				// Now add the new piece to the slot
				move.getEndSlot().addPiece(pieceToMove);
			}
			
			clearStartSlot();
		}
	}
	
	public void undoMove() {
		// undo moves from this turn if there are any
		if (game.getCurrentTurn().getMoves().size() > 0) {
			// Remove the last move from the list of moves
			Move lastMove = game.getCurrentTurn().getMoves().get(game.getCurrentTurn().getMoves().size()-1);
			game.getCurrentTurn().getMoves().remove(lastMove);
			
			// Undo the last move
			Piece undoPiece = lastMove.getEndSlot().getPieces().last();
			lastMove.getStartSlot().addPiece(undoPiece);
			lastMove.getEndSlot().removePiece(undoPiece);
			
			//If we bumped a piece in the last move, put it back.
			if (lastMove.isPieceBumped()) {
				Bar bar = game.getBoard().getBar();
				for (Piece piece: bar.getPieces()) {
					if (piece.color != game.getCurrentTurn().getColor()) {
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
			if (move.getDie().equals(gameDie)) return true;
		}
		
		return false;
	}
}
