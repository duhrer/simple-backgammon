package com.anthonyatkins.simplebackgammon.model;

import java.util.ArrayList;

public class SavedGame {
	int activePlayer;
	int gameState;
	ArrayList<Integer> boardState = null;
	ArrayList<String> activePlayerDiceState = null;
	ArrayList<String> inactivePlayerDiceState = null;
	
	public SavedGame(int gameState, int activePlayer, ArrayList<Integer> boardState,
			ArrayList<String> activePlayerDiceState,
			ArrayList<String> inactivePlayerDiceState) {
		super();
		this.gameState = gameState;
		this.activePlayer = activePlayer;
		this.boardState = boardState;
		this.activePlayerDiceState = activePlayerDiceState;
		this.inactivePlayerDiceState = inactivePlayerDiceState;
	}
	public int getActivePlayer() {
		return activePlayer;
	}
	public ArrayList<Integer> getBoardState() {
		return boardState;
	}
	public ArrayList<String> getActivePlayerDiceState() {
		return activePlayerDiceState;
	}
	public ArrayList<String> getInactivePlayerDiceState() {
		return inactivePlayerDiceState;
	}
	public int getGameState() {
		return gameState;
	}
}
