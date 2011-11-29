package com.anthonyatkins.simplebackgammon.model;

public class Match {
	// Database setup information
	public static final String _ID            = "_id";
	public static final String BLACK_PLAYER	  = "black_player";
	public static final String WHITE_PLAYER	  = "white_player";
	public static final String NUM_GAMES	  = "num_games";
	public static final String FINISHED		  = "finished";
	public static final String DATE			  = "date";
	
	public static final String TABLE_NAME = "match";
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		BLACK_PLAYER + " integer, " +
		WHITE_PLAYER + " integer, " +
		NUM_GAMES + " integer, " +
		FINISHED + " boolean, " +
		DATE + " datetime " +
		");";
	
	public static final String[] COLUMNS = {
			_ID,
			BLACK_PLAYER,
			WHITE_PLAYER,
			NUM_GAMES,
			FINISHED,
			DATE
	};

	private long id;
	private Player blackPlayer;
	private Player whitePlayer;
	private int numGames;
	private boolean isFinished;
	
	public Player getBlackPlayer() {
		return blackPlayer;
	}
	public void setBlackPlayer(Player blackPlayer) {
		this.blackPlayer = blackPlayer;
	}
	public Player getWhitePlayer() {
		return whitePlayer;
	}
	public void setWhitePlayer(Player whitePlayer) {
		this.whitePlayer = whitePlayer;
	}
	public int getNumGames() {
		return numGames;
	}
	public void setNumGames(int numGames) {
		this.numGames = numGames;
	}
	public boolean isFinished() {
		return isFinished;
	}
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	public long getId() {
		return this.id;
	}
}
