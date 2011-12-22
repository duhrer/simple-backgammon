package com.anthonyatkins.simplebackgammon.model;

import java.util.Date;

public class TurnMove extends Move {
	private final Turn turn;

	public TurnMove(Slot startSlot, Slot endSlot, int pips, Turn turn) {
		super(startSlot,endSlot,pips,turn.getPlayer());
		this.turn = turn;
		turn.getMoves().add(this);
	}

	public TurnMove (TurnMove existingMove) {
		super(existingMove);
		this.turn = existingMove.getTurn();
		turn.getMoves().add(this);
	}
	
	public TurnMove(Move move, Turn turn) {
		super(move.getStartSlot(),move.getEndSlot(),move.getPips(),turn.getPlayer());
		this.pieceBumped = move.isPieceBumped();
		this.turn = turn;
		turn.getMoves().add(this);
	}

	public TurnMove(Slot startSlot, Slot endSlot, int pips, Turn turn, Date created) {
		super(startSlot,endSlot,pips,turn.getPlayer(),created);
		this.turn = turn;
		turn.getMoves().add(this);
	}

	public Turn getTurn() {
		return this.turn;
	}
}
