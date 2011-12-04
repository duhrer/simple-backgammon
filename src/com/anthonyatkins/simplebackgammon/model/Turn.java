package com.anthonyatkins.simplebackgammon.model;

import java.util.Date;
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
	
	public static final String TABLE_NAME = "match";
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
	private final Player player;
	private final Game game;
	private final SimpleDice dice;
	private final int color;
	private final Date created = new Date();

	public Turn(Player player, SimpleDice dice2, Game game) {
		this.player = player;
		this.color = player.getColor();
		this.dice = new SimpleDice(dice2);
		this.game = game;
		game.getGameLog().add(this);
	}
	
	public Turn(Turn existingTurn, Game game) {
		this.player = existingTurn.getPlayer();
		this.game = game;
		game.getGameLog().add(this);
		this.color = player.getColor();
		this.dice = new SimpleDice(existingTurn.dice);
		for (Move move: existingTurn.moves) {
			moves.add(new Move(move));
		}
	}
	
	public Turn(Turn existingTurn) {
		this.player = existingTurn.getPlayer();
		this.game = existingTurn.getGame();
		game.getGameLog().add(this);
		this.color = player.getColor();
		this.dice = new SimpleDice(existingTurn.dice);
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

	public SimpleDice getDice() {
		return dice;
	}

	public int getColor() {
		return color;
	}
}
