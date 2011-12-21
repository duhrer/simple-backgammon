package com.anthonyatkins.simplebackgammon.model;

import java.util.Date;

public class Move implements Comparable {
	@Override
	public String toString() {
		return "Move [startSlot=" + startSlot + ", endSlot=" + endSlot
				+ ", die=" + die + ", pieceBumped=" + pieceBumped + "]";
	}

	// Database setup information
	public static final String _ID            = "_id";
	public static final String TURN 		  = "turn";

	public static final String PLAYER		      = "player";
	public static final String DIE		      = "die";
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
		DIE + " integer, " +
		START_SLOT + " integer, " +
		END_SLOT  + " integer, " +
		PIECE_BUMPED + " boolean, " +
		CREATED + " datetime " +
		");";
	
	public static final String[] COLUMNS = {
			_ID,
			TURN,
			PLAYER,
			DIE,
			START_SLOT,
			END_SLOT,
			PIECE_BUMPED,
			CREATED
	};
	
	private long id = -1;
	private Slot startSlot;
	private Slot endSlot;
	// FIXME:  Find some way of handling moves that involve more than one die, not necessarily in the move object, but somewhere
	private final SimpleDie die;
	private final Player player;
	protected boolean pieceBumped = false;
	private final Date created;

	public Move(Slot startSlot, Slot endSlot, SimpleDie die, Player player) {
		this.startSlot = startSlot;
		this.endSlot = endSlot;
		this.die = die;
		this.player = player;
		this.created = new Date();
	}
	
	public Move(Slot startSlot, Slot endSlot, SimpleDie die, Player player, Date created) {
		this.startSlot = startSlot;
		this.endSlot = endSlot;
		this.die = die;
		this.player = player;
		this.created = created;
	}

	public Move (Move existingMove) {
		this.startSlot = existingMove.getStartSlot();
		this.endSlot = existingMove.getEndSlot();
		// clone this so that changes to the original dice won't be reflected here
		this.die = new SimpleDie(existingMove.die);
		this.player = existingMove.getPlayer();
		this.created = new Date();
	}
	
	public Player getPlayer() {
		return player;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Move)) return false;
		
		Move otherMove = (Move) obj;
		
		if (((this.startSlot == null && otherMove.startSlot == null) || this.startSlot.equals(otherMove.startSlot)) && 
			((this.endSlot == null && otherMove.endSlot == null) ||  this.endSlot.equals(otherMove.endSlot)) &&
			this.die.equals(otherMove.die)) {
			return true;
		}
		
		return false;
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

	public boolean isPieceBumped() {
		return pieceBumped;
	}

	public void setPieceBumped(boolean pieceBumped) {
		this.pieceBumped = pieceBumped;
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

	public void setEndSlot(Slot endSlot) {
		this.endSlot = endSlot;
	}

	public Slot getEndSlot() {
		return endSlot;
	}

	public SimpleDie getDie() {
		return die;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void clearStartSlot() {
		this.startSlot = null;			
	}

	public void clearEndSlot() {
		this.endSlot = null;
	}
}
