package com.anthonyatkins.simplebackgammon.model;


public class Player {
	private long id = -1;
	private String name = "Unknown Player";
	
	// Database setup information
	public static final String _ID            = "_id";
	public static final String NAME           = "name";
	
	public static final String TABLE_NAME = "player";
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		NAME + " varchar(20) " +
		");";
	
	public String getName() {
		return name;
	}

	public static final String[] COLUMNS = {
			_ID,
			NAME
	};
	
	public Player(String name) {
		this.name = name;
	}
	
	public Player() {
		super();
	}

	public Player(Player existingPlayer) {
		this.id = existingPlayer.getId();
		this.name = existingPlayer.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Player other = (Player) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void setName(String playerName) {
		this.name = playerName;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long playerId) {
		this.id = playerId;		
	}
}
