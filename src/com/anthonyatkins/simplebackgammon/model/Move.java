package com.anthonyatkins.simplebackgammon.model;

import java.util.Date;

public class Move implements Comparable {
	// Database setup information
	public static final String _ID            = "_id";

	public static final String TURN 		  = "turn";
	public static final String PLAYER		      = "player";

	public static final String PIPS		      = "die";
	public static final String START_SLOT     = "start_slot";
	public static final String END_SLOT	      = "end_slot";
	public static final String PIECE_BUMPED   = "piece_bumped";
	public static final String CREATED		  = "created";
	public static final String TABLE_NAME = "move";
	
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		TURN + " integer, " +
		PLAYER + " integer, " +
		PIPS + " integer, " +
		START_SLOT + " integer, " +
		END_SLOT  + " integer, " +
		PIECE_BUMPED + " boolean, " +
		CREATED + " datetime " +
		");";
	public static final String[] COLUMNS = {
			_ID,
			TURN,
			PLAYER,
			PIPS,
			START_SLOT,
			END_SLOT,
			PIECE_BUMPED,
			CREATED
	};
	
	private long id = -1;
	
	private Slot startSlot;
	private Slot endSlot;
	// FIXME:  Find some way of handling moves that involve more than one die, not necessarily in the move object, but somewhere
	private final int pips;

	private final Player player;
	
	protected boolean pieceBumped = false;
	private final Date created;
	public Move (Move existingMove) {
		this.startSlot = existingMove.getStartSlot();
		this.endSlot = existingMove.getEndSlot();
		// clone this so that changes to the original dice won't be reflected here
		this.pips= existingMove.getPips();
		this.player = existingMove.getPlayer();
		this.created = new Date();
	}

	public Move(Slot startSlot, Slot endSlot, int pips, Player player) {
		this.startSlot = startSlot;
		this.endSlot = endSlot;
		this.pips = pips;
		this.player = player;
		this.created = new Date();
	}
	
	public Move(Slot startSlot, Slot endSlot, int pips, Player player, Date created) {
		this.startSlot = startSlot;
		this.endSlot = endSlot;
		this.pips= pips;
		this.player = player;
		this.created = created;
	}

	public void clearEndSlot() {
		this.endSlot = null;
	}
	
	public void clearStartSlot() {
		this.startSlot = null;			
	}

	public int compareTo(Object another) {
		if (another instanceof Move) {
			/* sort by date created */
			if (this.created.after(((Move) another).created)) { return 1; }
			else if (this.created.equals(((Move) another).created)) { return 0; }
			else if (this.created.before(((Move) another).created)) { return -1; }
		}

		/* otherwise, they are sorted at the same level (should not be possible) */
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (endSlot == null) {
			if (other.endSlot != null)
				return false;
		} else if (!endSlot.equals(other.endSlot))
			return false;
		if (pieceBumped != other.pieceBumped)
			return false;
		if (pips != other.pips)
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (startSlot == null) {
			if (other.startSlot != null)
				return false;
		} else if (!startSlot.equals(other.startSlot))
			return false;
		return true;
	}

	public Date getCreated() {
		return created;
	}

	public Slot getEndSlot() {
		return endSlot;
	}

	public long getId() {
		return id;
	}

	public int getPips() {
		return this.pips;
	}

	public Player getPlayer() {
		return player;
	}

	public Slot getStartSlot() {
		return startSlot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((endSlot == null) ? 0 : endSlot.hashCode());
		result = prime * result + (pieceBumped ? 1231 : 1237);
		result = prime * result + pips;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result
				+ ((startSlot == null) ? 0 : startSlot.hashCode());
		return result;
	}

	public boolean isPieceBumped() {
		return pieceBumped;
	}

	public void setEndSlot(Slot endSlot) {
		this.endSlot = endSlot;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPieceBumped(boolean pieceBumped) {
		this.pieceBumped = pieceBumped;
	}

	public void setStartSlot(Slot startSlot) {
		this.startSlot = startSlot;
	}

	@Override
	public String toString() {
		return "Move [startSlot=" + startSlot + ", endSlot=" + endSlot
				+ ", pips=" + pips+ ", pieceBumped=" + pieceBumped + "]";
	}
}
