package com.anthonyatkins.simplebackgammon.exception;

public class InvalidMoveException extends Exception {
	private static final long serialVersionUID = -1867207625806769583L;
	
	public InvalidMoveException(String detailMessage) {
		super(detailMessage);
	}
}
