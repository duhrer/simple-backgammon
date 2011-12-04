package com.anthonyatkins.simplebackgammon.model;

import java.util.Date;

public class Move implements Comparable {
	// Database setup information
	public static final String _ID            = "_id";
	public static final String TURN 		  = "turn";

	public static final String PLAYER		      = "player";
	public static final String DIE		      = "die";
	public static final String START_SLOT     = "start_slot";
	public static final String END_SLOT	      = "end_slot";
	public static final String CREATED		  = "created";
	
	public static final String TABLE_NAME = "match";
	public static final String TABLE_CREATE = 
		"CREATE TABLE " +
		TABLE_NAME + " (" +
		_ID + " integer primary key, " +
		TURN + " integer, " +
		PLAYER + " integer, " +
		DIE + " integer, " +
		START_SLOT + " integer, " +
		END_SLOT  + " integer, " +
		CREATED + " datetime " +
		");";
	
	public static final String[] COLUMNS = {
			_ID,
			TURN,
			PLAYER,
			DIE,
			START_SLOT,
			END_SLOT,
			CREATED
	};
	
	private long id = -1;
	private Slot startSlot;
	private Slot endSlot;
	// FIXME:  Find some way of handling moves that involve more than one die, not necessarily in the move object, but somewhere
	private final SimpleDie die;
	private final Player player;
	private final Turn turn;
	private boolean pieceBumped = false;
	private Date created = new Date();

	public Move(Slot startSlot, Slot endSlot, SimpleDie die, Turn turn) {
		this.startSlot = startSlot;
		this.endSlot = endSlot;
		this.die = die;
		this.player = turn.getPlayer();
		this.turn = turn;
	}

	public Move (Move existingMove) {
		this.startSlot = existingMove.getStartSlot();
		this.endSlot = existingMove.getEndSlot();
		// clone this so that changes to the original dice won't be reflected here
		this.die = new SimpleDie(existingMove.die);
		this.player = existingMove.getPlayer();
		this.turn = existingMove.getTurn();
	}
	
	public Turn getTurn() {
		return this.turn;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Move)) return false;
		
		Move otherMove = (Move) obj;
		
		if (this.startSlot.equals(otherMove.startSlot) && 
			this.endSlot.equals(otherMove.endSlot) &&
			this.die.equals(otherMove.die)) {
			return true;
		}
		
		return false;
	}

	public int compareTo(Object another) {
		if (another instanceof Move) {
			Move anotherMove = (Move) another;
			/* sort by created first, used to keep logs, etc. sweet */
			if (this.created.after(((Move) another).created)) { return 1; }
			else if (this.created.equals(((Move) another).created)) { return 0; }
			else if (this.created.before(((Move) another).created)) { return -1; }
			
			/* sort by startSlot position if there is a difference */
			if (anotherMove.startSlot.getPosition() > this.startSlot.getPosition()) { return 1; }
			else if (anotherMove.startSlot.getPosition() < this.startSlot.getPosition()) { return -1; }
			/* otherwise, sort by endSlot position if there is a difference */
			else if (anotherMove.endSlot.getPosition() > this.endSlot.getPosition()) { return 1; }
			else if (anotherMove.endSlot.getPosition() < this.endSlot.getPosition()) { return -1; }

			return this.die.getValue() - anotherMove.die.getValue();
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

	public void setCreated(Date created) {
		this.created = created;
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
