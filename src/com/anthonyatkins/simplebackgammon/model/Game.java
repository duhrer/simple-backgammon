package com.anthonyatkins.simplebackgammon.model;

import java.util.Date;

import com.anthonyatkins.simplebackgammon.Constants;

public class Game {
	// Database setup information
	public static final String _ID             = "_id";
	public static final String MATCH           = "match";
	public static final String BLACK_PLAYER	   = "black_player";
	public static final String WHITE_PLAYER	   = "white_player";
	public static final String POINTS          = "points";
	public static final String FINISHED        = "finished";
	public static final String CREATED         = "created";
	
	public static final String TABLE_NAME = "game";
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		MATCH + " integer " +
		BLACK_PLAYER + " integer " +
		WHITE_PLAYER + " integer " +
		POINTS + " integer " +
		FINISHED + " boolean " +
		CREATED + " datetime " +
		");";
	
	public static final String[] COLUMNS = {
			_ID,
			MATCH,
			BLACK_PLAYER,
			WHITE_PLAYER,
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
	public static final int UNINITIALIZED    = -99;
	public static final int EXIT			 = -1;
	public static final int STARTUP          = 0;
	public static final int PICK_FIRST       = 1;
	public static final int ROLL             = 2;
	public static final int MOVE_PICK_SOURCE = 3;
	public static final int MOVE_PICK_DEST   = 4;
	public static final int MAKE_MOVE        = 5;
	public static final int SWITCH_PLAYER    = 6;
	public static final int GAME_OVER        = 99;
	
	private int state = UNINITIALIZED;

	private Slot startSlot;
	private Slot endSlot;
	// The turn that will eventually be added to the GameLog
	private Turn currentTurn;
	
	// The list of potential moves
	private final Moves potentialMoves = new Moves();
	
	// FIXME:  We have no code to manage matches yet
	private final Match match;
	private boolean isFinished = false;

	public Game(Match match) {
		this.match = match;
		match.addGame(this);
		
		this.gameLog = new GameLog();
		
		this.whitePlayer = (new Player(Constants.WHITE)); 
		this.blackPlayer = (new Player(Constants.BLACK)); 

		this.board = (new Board(this));
	}

	/* Create a new game based on an existing game.   Used primarily for unit tests. */
	public Game(Game baselineGame) {
		this.match = baselineGame.getMatch();
		match.addGame(this);
		this.blackPlayer = (new Player(baselineGame.getBlackPlayer()));
		this.whitePlayer  = (new Player(baselineGame.getWhitePlayer()));
		this.board = (new Board(baselineGame.getBoard(), this));
		
		if (baselineGame.getActivePlayer() == null ) {
			// do nothing
		}
		else if (baselineGame.getActivePlayer().getColor() == Constants.BLACK) {
			setActivePlayer(getBlackPlayer());
		}
		else {
			setActivePlayer(getWhitePlayer());
		}
		
		this.gameLog = (new GameLog(baselineGame.getGameLog()));
	}

	public Player getActivePlayer() {
		if (getBlackPlayer().isActive()) return getBlackPlayer();
		else if (getWhitePlayer().isActive()) return getWhitePlayer();
		return null;
	}

	public void setActivePlayer(Player activePlayer) {
		if (getBlackPlayer().equals(activePlayer)) {
			getBlackPlayer().setActive(true);
			getWhitePlayer().setActive(false);
		}
		else {
			getBlackPlayer().setActive(false);
			getWhitePlayer().setActive(true);
		}
		
		this.currentTurn = new Turn(activePlayer,activePlayer.getDice(),this);
	}

	public Player getInactivePlayer() {
		if (getBlackPlayer().isActive()) return getWhitePlayer();
		else if (getWhitePlayer().isActive()) return getBlackPlayer();
		return null;
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
		result = prime * result + ((getGameLog() == null) ? 0 : getGameLog().hashCode());
		result = prime * result
				+ ((getActivePlayer() == null) ? 0 : getActivePlayer().hashCode());
		result = prime * result
				+ ((getBlackPlayer() == null) ? 0 : getBlackPlayer().hashCode());
		result = prime * result + ((getBoard() == null) ? 0 : getBoard().hashCode());
		result = prime * result
				+ ((getInactivePlayer() == null) ? 0 : getInactivePlayer().hashCode());
		result = prime * result + state;
		result = prime * result
				+ ((getWhitePlayer() == null) ? 0 : getWhitePlayer().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Game))
			return false;
		Game other = (Game) obj;
		if (getGameLog() == null) {
			if (other.getGameLog() != null)
				return false;
		} else if (!getGameLog().equals(other.getGameLog()))
			return false;
		if (getActivePlayer() == null) {
			if (other.getActivePlayer() != null)
				return false;
		} else if (!getActivePlayer().equals(other.getActivePlayer()))
			return false;
		if (getBlackPlayer() == null) {
			if (other.getBlackPlayer() != null)
				return false;
		} else if (!getBlackPlayer().equals(other.getBlackPlayer()))
			return false;
		if (getBoard() == null) {
			if (other.getBoard() != null)
				return false;
		} else if (!getBoard().equals(other.getBoard()))
			return false;
		if (getInactivePlayer() == null) {
			if (other.getInactivePlayer() != null)
				return false;
		} else if (!getInactivePlayer().equals(other.getInactivePlayer()))
			return false;
		if (state != other.state)
			return false;
		if (getWhitePlayer() == null) {
			if (other.getWhitePlayer() != null)
				return false;
		} else if (!getWhitePlayer().equals(other.getWhitePlayer()))
			return false;
		return true;
	}

	public void switchPlayers() {
		Player currentInactivePlayer = getInactivePlayer();
		setActivePlayer(currentInactivePlayer);
	}

	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}
	
	public Turn getCurrentTurn() {
		return this.currentTurn;
	}
	
	public void setCurrentTurn(Turn turn) {
		this.currentTurn = turn;
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

	public void makeMove(Move move) {
		Slot startSlot = getBoard().getPlaySlots().get(move.getStartSlot().getPosition());
		Piece piece = startSlot.removePiece();
		
		Slot endSlot = getBoard().getPlaySlots().get(move.getStartSlot().getPosition());
		endSlot.addPiece(piece);
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

	public Slot getStartSlot() {
		return startSlot;
	}

	public void setStartSlot(Slot startSlot) {
		this.startSlot = startSlot;
	}

	public Slot getEndSlot() {
		return endSlot;
	}

	public void setEndSlot(Slot endSlot) {
		this.endSlot = endSlot;
	}

	public void clearSelectedSlots() {
		this.startSlot = null;
		this.endSlot = null;
	}
	
	public Moves getPotentialMoves() {
		return potentialMoves;
	}
}
