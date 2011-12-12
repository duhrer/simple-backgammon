package com.anthonyatkins.simplebackgammon.model;

public class TurnMove extends Move {
	private final Turn turn;

	public TurnMove(Slot startSlot, Slot endSlot, SimpleDie die, Turn turn) {
		super(startSlot,endSlot,die,turn.getPlayer());
		this.turn = turn;
		turn.getMoves().add(this);
	}

	public TurnMove (TurnMove existingMove) {
		super(existingMove);
		this.turn = existingMove.getTurn();
		turn.getMoves().add(this);
	}
	
	public TurnMove(Move move, Turn turn) {
		super(move.getStartSlot(),move.getEndSlot(),move.getDie(),turn.getPlayer());
		this.turn = turn;
	}

	public Turn getTurn() {
		return this.turn;
	}
}
