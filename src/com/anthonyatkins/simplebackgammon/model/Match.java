package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Match {
	// Database setup information
	public static final String _ID            = "_id";
	public static final String BLACK_PLAYER	  = "black_player";
	public static final String WHITE_PLAYER	  = "white_player";
	public static final String POINTS_TO_WIN	  = "points_to_win";
	public static final String FINISHED		  = "finished";
	public static final String CREATED			  = "created";
	
	public static final String TABLE_NAME = "match";
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		BLACK_PLAYER + " integer, " +
		WHITE_PLAYER + " integer, " +
		POINTS_TO_WIN + " integer, " +
		FINISHED + " boolean, " +
		CREATED + " datetime " +
		");";
	
	public static final String[] COLUMNS = {
			_ID,
			BLACK_PLAYER,
			WHITE_PLAYER,
			POINTS_TO_WIN,
			FINISHED,
			CREATED
	};

	private long id = -1;
	private final Player blackPlayer;
	private final Player whitePlayer;
	private final int pointsToWin;
	private boolean isFinished;
	private final Date created = new Date();
	private List<Game> games = new ArrayList<Game>();
	
	public Match(Player blackPlayer, Player whitePlayer, int pointsToWin) {
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;
		this.pointsToWin = pointsToWin;
	}
	
	public void addGame(Game game) {
		games.add(game);
	}
	public void deleteGame(Game game) {
		games.remove(game);
	}
	public List<Game> getGames() {
		return games;
	}
	public Date getCreated() {
		return created;
	}
	public Player getBlackPlayer() {
		return blackPlayer;
	}
	public Player getWhitePlayer() {
		return whitePlayer;
	}
	public int getPointsToWin() {
		return pointsToWin;
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
	public void setId(long id) {
		this.id = id;
	}
	public void addGames(List<Game> gamesByMatch) {
		games.addAll(gamesByMatch);
	}

	public Game getGameById(long gameId) {
		for (Game game : games) {
			if (game.getId() == gameId) {
				return game;
			}
		}
		
		return null;
	}
}
