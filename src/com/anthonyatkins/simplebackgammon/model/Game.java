package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.anthonyatkins.simplebackgammon.Constants;

public class Game {
	// Database setup information
	public static final String _ID             = "_id";
	public static final String MATCH           = "match";
	public static final String BLACK_PLAYER	   = "black_player";
	public static final String WHITE_PLAYER	   = "white_player";
	public static final String STARTING_COLOR  = "starting_color";
	public static final String GAME_STATE  	   = "game_state";
	public static final String POINTS          = "points";
	public static final String FINISHED        = "finished";
	public static final String CREATED         = "created";
	
	public static final String TABLE_NAME = "game";
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		MATCH + " integer, " +
		BLACK_PLAYER + " integer, " +
		WHITE_PLAYER + " integer, " +
		STARTING_COLOR + " integer, " +
		GAME_STATE + " integer, " +
		POINTS + " integer, " +
		FINISHED + " boolean, " +
		CREATED + " datetime " +
		");";
	
	public static final String[] COLUMNS = {
			_ID,
			MATCH,
			BLACK_PLAYER,
			WHITE_PLAYER,
			STARTING_COLOR,
			GAME_STATE,
			POINTS,
			FINISHED,
			CREATED
	};
	
	// FIXME: We need to uniquely generate this on game creation based on the database.
	private long id = -1;
	private final Board board;
	private final Player blackPlayer;
	private final Player whitePlayer;
	private final GameLog gameLog;
	
	// The current value of the doubling cube
	private int points = 1;

	private final Date created = new Date();
	
	// "states" the game can be in
	public static final int EXIT			 = -1;
	public static final int STARTUP          = 0;
	public static final int ROLL             = 2;
	public static final int MOVE_PICK_SOURCE = 3;
	public static final int MOVE_PICK_DEST   = 4;
	public static final int NEW_TURN    	 = 6;
	public static final int GAME_OVER        = 99;
	
	private int state = STARTUP;

	// FIXME:  We have no code to manage matches yet
	private final Match match;
	private boolean isFinished = false;

	private int startingColor;
	
	public Game(Match match, int startingColor) {
		this.match = match;
		match.addGame(this);
		
		this.blackPlayer = (match.getBlackPlayer()); 
		this.whitePlayer = (match.getWhitePlayer()); 

		this.gameLog = new GameLog();
		new Turn(startingColor == Constants.BLACK ? match.getBlackPlayer() : match.getWhitePlayer(),this,startingColor);
		
		this.board = (new Board(this));
		
		this.startingColor = startingColor;
	}

	/* Create a new game based on an existing game.   Used primarily for unit tests. */
	public Game(Game baselineGame) {
		this.match = baselineGame.getMatch();
		match.addGame(this);
		this.blackPlayer = (match.getBlackPlayer()); 
		this.whitePlayer = (match.getWhitePlayer()); 
		this.board = (new Board(baselineGame.getBoard(), this));
		
		this.gameLog = new GameLog();
		Iterator<Turn> turnIterator = baselineGame.getGameLog().iterator();
		while (turnIterator.hasNext()) {
			new Turn(turnIterator.next(), this);
		}
		
		this.startingColor = baselineGame.getStartingColor();
	}


	public int getStartingColor() {
		return this.startingColor;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blackPlayer == null) ? 0 : blackPlayer.hashCode());
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((gameLog == null) ? 0 : gameLog.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (isFinished ? 1231 : 1237);
		result = prime * result + points;
		result = prime * result + startingColor;
		result = prime * result + state;
		result = prime * result
				+ ((whitePlayer == null) ? 0 : whitePlayer.hashCode());
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
		Game other = (Game) obj;
		if (blackPlayer == null) {
			if (other.blackPlayer != null)
				return false;
		} else if (!blackPlayer.equals(other.blackPlayer))
			return false;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (gameLog == null) {
			if (other.gameLog != null)
				return false;
		} else if (!gameLog.equals(other.gameLog))
			return false;
		if (id != other.id)
			return false;
		if (isFinished != other.isFinished)
			return false;
		if (points != other.points)
			return false;
		if (startingColor != other.startingColor)
			return false;
		if (state != other.state)
			return false;
		if (whitePlayer == null) {
			if (other.whitePlayer != null)
				return false;
		} else if (!whitePlayer.equals(other.whitePlayer))
			return false;
		return true;
	}

	public void newTurn() {
		if (getCurrentTurn().getColor() == Constants.BLACK) {
			newTurn(whitePlayer,Constants.WHITE);
		}
		else {
			newTurn(blackPlayer,Constants.BLACK);
		}
	}

	public void newTurn(Player player, int color) {
		new Turn(player,this,color);
	}
	
	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}
	
	public Turn getCurrentTurn() {
		if (gameLog.size() > 0) {
			return gameLog.get(gameLog.size()-1);
		}

		return null;
	}
	
	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished  = isFinished;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public Match getMatch() {
		return this.match;
	}

	public boolean isFinished() {
		return this.isFinished;
	}

	public GameLog getGameLog() {
		return gameLog;
	}

	public Board getBoard() {
		return board;
	}

	public Date getCreated() {
		return created;
	}
	
	public boolean playerWon() {
		Dugout myDugout = null;

		if (getCurrentTurn().getColor() == Constants.BLACK) {
			myDugout = getBoard().getBlackOut();
		}
		else {
			myDugout = getBoard().getWhiteOut();
		}

		if (myDugout.getPieces().size() == 15) { return true; }
		
		return false;
	}
	
	public boolean playerCanMoveOut() {
		boolean canMoveOut = true;
		if (getBoard().getBar().containsPlayerPieces(getCurrentTurn().getColor())) {  canMoveOut = false;}
		if (getCurrentTurn().getColor() == Constants.BLACK) {
			if (getBoard().getBlackPieces().first().position < 18) { canMoveOut = false; }
		}
		else {
			if (getBoard().getWhitePieces().last().position > 5) { canMoveOut = false; }
		}

		return canMoveOut;
	}
	
	/**
	 * Determine the slots the active player can move from.  Only used for normal slots, and not for the bar or dugouts.
	 * @return The list of slots that have valid moves.
	 */
	public void findAllPotentialMoves() {
		// If we have pieces on the bar, that's our only option at first
		if (getBoard().getBar().containsPlayerPieces(getCurrentTurn().getColor())) {
			getCurrentTurn().setStartSlot(getBoard().getBar());
			getAvailableMovesFromBar();
		}
		else {
			Moves potentialMoves = getCurrentTurn().getPotentialMoves();
			potentialMoves.clear();
			
			/* this detects all non-bar moves, there should be no others when we run this */
			List<Integer> uniquePositions = new ArrayList<Integer>();
			Pieces pieces = getCurrentTurn().getColor() == Constants.BLACK ? getBoard().getBlackPieces() : getBoard().getWhitePieces();
			Iterator<Piece> pieceIterator = pieces.iterator();
			while (pieceIterator.hasNext()) {
				Piece piece = pieceIterator.next();
				int position = piece.position;
				if (position >= 0 && position <= 23 && !uniquePositions.contains(position)) { uniquePositions.add(position); }
			}
			
			for (int b=0; b < uniquePositions.size(); b++) {
				Moves slotMoves = findAvailableMovesFromSlot(getBoard().getPlaySlots().get(uniquePositions.get(b)));
				potentialMoves.addAll(slotMoves);
			}
		}
	}
			
	/**
	 * Tag the moves that are possible from a given slot.
	 * @param slot  The slot to check.
	 */
	public Moves findAvailableMovesFromSlot(Slot slot) {
		Moves slotMoves = new Moves();
		int slotPosition = slot.getPosition();
		List<Integer> uniqueDieValues = new ArrayList<Integer>();

		
		Iterator<SimpleDie> dieIterator = getCurrentTurn().getDice().iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			if (!die.isUsed() && !uniqueDieValues.contains(new Integer(die.getValue()))) {
				/* If we have doubles, we only want to add moves for the first die */
				uniqueDieValues.add(new Integer(die.getValue()));
				int diePosition = slotPosition + (getCurrentTurn().getColor() * die.getValue());
				if (diePosition >= 0 && diePosition <= 23) {
						Slot destinationSlot = getBoard().getPlaySlots().get(diePosition);
						if (!destinationSlot.isBlocked(getCurrentTurn().getColor())) { 
							Move potentialMove = new Move(slot,destinationSlot,die, getCurrentTurn().getPlayer());
							slotMoves.add(potentialMove);
						}
				}
				else if (playerCanMoveOut()) {
					// If the piece is in the right position, it can always move out.
					// If it's the trailing edge of the player's pieces, it can move out with any roll high enough
					if (getCurrentTurn().getColor() == Constants.BLACK) { 
						
						if (diePosition == 24 || (getBoard().getBlackPieces().first().position == slotPosition && diePosition > 24)) {
							Move potentialMove = new Move(slot,getBoard().getBlackOut(),die, getCurrentTurn().getPlayer());
							slotMoves.add(potentialMove);
						}
					}
					else { 
						if (diePosition == -1 || (getBoard().getWhitePieces().last().position == slotPosition && diePosition < -1)) {
							Move potentialMove = new Move(slot,getBoard().getWhiteOut(),die, getCurrentTurn().getPlayer());
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
	public void getAvailableMovesFromBar() {
		int startSlotPosition = 0;
		List<Integer> uniqueDieValues = new ArrayList<Integer>();

		/* we never allow moves from the bar and anywhere else, so clear the list of moves out */
		Moves potentialMoves = getCurrentTurn().getPotentialMoves();
		potentialMoves.clear();
		
		if (getBoard().getBar().containsPlayerPieces(getCurrentTurn().getColor())) {
			if (getCurrentTurn().getColor() == Constants.BLACK) { startSlotPosition = -1; }
			else { startSlotPosition = 24; }

			Iterator<SimpleDie> dieIterator = getCurrentTurn().getDice().iterator();
			while (dieIterator.hasNext()) {
				SimpleDie die = dieIterator.next();
				if (!die.isUsed() && !uniqueDieValues.contains(new Integer(die.getValue()))) {
					/* If we have doubles, we only want to add moves for the first die */
					uniqueDieValues.add(new Integer(die.getValue()));
					
					int dieValue = die.getValue();
					int dieSlotPosition = startSlotPosition + (getCurrentTurn().getColor() * dieValue);
					Slot destinationSlot = getBoard().getPlaySlots().get(dieSlotPosition);
					if (!destinationSlot.isBlocked(getCurrentTurn().getColor())) {
						Move potentialMove = new Move(getBoard().getBar(), destinationSlot,die, getCurrentTurn().getPlayer());
						potentialMoves.add(potentialMove);
					}
				}			
			}
		}
	}

	public void setStartingColor(int startingColor) {
		this.startingColor = startingColor;
	}
}
