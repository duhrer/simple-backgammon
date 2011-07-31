package com.anthonyatkins.simplebackgammon.model;

import com.anthonyatkins.simplebackgammon.Constants;

public class Game {
	public Board board;
	private Player blackPlayer;
	private Player whitePlayer;
//	public Dialog dialog;
	public GameLog gameLog = new GameLog();
	public Turn currentTurn;

	// "states" the game can be in
	public static final int UNINITIALIZED    = -99;
	public static final int LOAD_SAVE 		 = -1;
	public static final int STARTUP          = 0;
	public static final int PICK_FIRST       = 1;
	public static final int ROLL             = 2;
	public static final int MOVE_PICK_SOURCE = 3;
	public static final int MOVE_PICK_DEST   = 4;
	public static final int MAKE_MOVE        = 5;
	public static final int SWITCH_PLAYER    = 6;
	public static final int GAME_OVER        = 99;
	
	private int state = UNINITIALIZED;
	private Slot sourceSlot = null;
	private Slot destSlot = null;
	private SavedGame savedGame;

	public Game() {
		initialize();
    }

	/* Create a new game based on an existing game.   Used primarily for unit tests. */
	public Game(Game baselineGame) {
		this.setBlackPlayer(new Player(baselineGame.getBlackPlayer(), this));
		this.setWhitePlayer(new Player(baselineGame.getWhitePlayer(), this));
		this.board = new Board(baselineGame.board, this);
		
		if (baselineGame.getActivePlayer() == null ) {
			// do nothing
		}
		else if (baselineGame.getActivePlayer().color == Constants.BLACK) {
			setActivePlayer(getBlackPlayer());
		}
		else {
			setActivePlayer(getWhitePlayer());
		}
		
		this.gameLog = new GameLog(baselineGame.gameLog);
		
		if (baselineGame.currentTurn != null) {
			this.currentTurn = new Turn(baselineGame.currentTurn, this);
		}
	}

	public void initialize() {
//		if (dialog == null) { dialog = new Dialog(); }
//		else { dialog.setMessage(null); }

		/* If we're starting a new game in the same session, we need to clear out the existing data.
		 * Among other things, this should fix the display of the last winning move on the GAME_START screen. */
		currentTurn = null;
		gameLog.clear();
		
		if (getWhitePlayer() == null) { setWhitePlayer(new Player(Constants.WHITE, this)); }
		if (getBlackPlayer() == null) { setBlackPlayer(new Player(Constants.BLACK, this)); }

		// If the board already exists we are reinitializing it.  That method will clear out the player pieces.
		if (board == null) { board = new Board(this); }
		else { 
			try {
				board.initializeSlots();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	public Slot getSourceSlot() {
		return sourceSlot;
	}

	public void setSourceSlot(Slot sourceSlot) {
		this.sourceSlot = sourceSlot;
	}

	public Slot getDestSlot() {
		return destSlot;
	}

	public void setDestSlot(Slot destSlot) {
		this.destSlot = destSlot;
	}

	public SavedGame getSavedGame() {
		return this.savedGame;
	}
	
	public void setSavedGame(SavedGame savedGame) {
		this.savedGame = savedGame;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gameLog == null) ? 0 : gameLog.hashCode());
		result = prime * result
				+ ((getActivePlayer() == null) ? 0 : getActivePlayer().hashCode());
		result = prime * result
				+ ((getBlackPlayer() == null) ? 0 : getBlackPlayer().hashCode());
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result
				+ ((currentTurn == null) ? 0 : currentTurn.hashCode());
		result = prime * result
				+ ((destSlot == null) ? 0 : destSlot.hashCode());
//		result = prime * result + ((dialog == null) ? 0 : dialog.hashCode());
		result = prime * result
				+ ((getInactivePlayer() == null) ? 0 : getInactivePlayer().hashCode());
		result = prime * result
				+ ((sourceSlot == null) ? 0 : sourceSlot.hashCode());
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
		if (gameLog == null) {
			if (other.gameLog != null)
				return false;
		} else if (!gameLog.equals(other.gameLog))
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
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (currentTurn == null) {
			if (other.currentTurn != null)
				return false;
		} else if (!currentTurn.equals(other.currentTurn))
			return false;
		if (destSlot == null) {
			if (other.destSlot != null)
				return false;
		} else if (!destSlot.equals(other.destSlot))
			return false;
//		if (dialog == null) {
//			if (other.dialog != null)
//				return false;
//		} else if (!dialog.equals(other.dialog))
//			return false;
		if (getInactivePlayer() == null) {
			if (other.getInactivePlayer() != null)
				return false;
		} else if (!getInactivePlayer().equals(other.getInactivePlayer()))
			return false;
		if (sourceSlot == null) {
			if (other.sourceSlot != null)
				return false;
		} else if (!sourceSlot.equals(other.sourceSlot))
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

	public void setBlackPlayer(Player blackPlayer) {
		this.blackPlayer = blackPlayer;
	}

	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public void setWhitePlayer(Player whitePlayer) {
		this.whitePlayer = whitePlayer;
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}
}
