package com.anthonyatkins.simplebackgammon.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.activity.SimpleBackgammon;
import com.anthonyatkins.simplebackgammon.model.Dugout;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.Piece;
import com.anthonyatkins.simplebackgammon.model.Player;
import com.anthonyatkins.simplebackgammon.model.SavedGame;
import com.anthonyatkins.simplebackgammon.model.SimpleDie;
import com.anthonyatkins.simplebackgammon.model.Slot;
import com.anthonyatkins.simplebackgammon.model.Turn;
import com.anthonyatkins.simplebackgammon.view.AnimatedSlotView;
import com.anthonyatkins.simplebackgammon.view.GameView;
import com.anthonyatkins.simplebackgammon.view.SinglePlayerContinueDialog;
import com.anthonyatkins.simplebackgammon.view.TwoPlayerDialog;

public class GameController {
	private GameView gameView;
	private SinglePlayerContinueDialog singlePlayerDialog;
	private TwoPlayerDialog twoPlayerDialog;
	

	public GameController(GameView gameView) {
		this.gameView = gameView;
		singlePlayerDialog = new SinglePlayerContinueDialog(gameView.context,gameView,this,gameView.getTheme());
		twoPlayerDialog = new TwoPlayerDialog(gameView.context,this,gameView.getTheme());
	}

	private class ChangeStateListener implements OnClickListener {
		int nextStep;
		public ChangeStateListener (int nextStep) {
			this.nextStep = nextStep;
		}
		public void onClick(View v) {
			setGameState(nextStep);
			v.invalidate();
		}
		
	}
 	
	private class SelectSlotListener implements OnClickListener {
		private Slot selectedSlot;
		public void onClick(View view) {
			gameView.game.setSourceSlot(this.selectedSlot);
			setGameState(Game.MOVE_PICK_DEST);
		}

		public SelectSlotListener(Slot selectedSlot) {
			this.selectedSlot = selectedSlot;
		}		
	}

	private class MovePieceListener implements OnClickListener {
		Move potentialMove;
		
		public void onClick(View v) {
			gameView.game.getActivePlayer().selectedMove = potentialMove;
			setGameState(Game.MAKE_MOVE);
		}
		public MovePieceListener(Move potentialMove) {
			this.potentialMove = potentialMove;
		}
	}
	
	/**
	 * Remove the listeners from all game elements, including the dialog, dugouts, bar, dice, and all slots.
	 */
	public void removeAllListeners() {
		removeAllListeners(gameView);
	}

	public void removeAllListeners(Object object) {
		if (object instanceof View) { 
			((View) object).setOnTouchListener(null); 
			((View) object).setClickable(false); 
			((View) object).invalidate(); 
		}
		if (object instanceof ViewGroup) {
			for (int a=0; a<((ViewGroup) object).getChildCount(); a++) {
				removeAllListeners(((ViewGroup) object).getChildAt(a));
			}
		}
	}
	
	public void addListener(View view, OnClickListener listener) {
		view.setOnClickListener(listener);
		view.setClickable(true);
		view.invalidate();
	}
	

	/**
	 * Remove all moves from the active player and all slots
	 */
	public void clearDetectedMoves() {
		if (gameView.game.getActivePlayer() != null) {
			gameView.game.getActivePlayer().moves.clear();
			for (Slot slot: gameView.game.board.playSlots) {
				slot.moves.clear();
			}
		}
	}
	
	/**
	 * A convenience method to call setGameState when no savedInstance data is available
	 * @param state The state to change the game to.  See setGameState(int state, Bundle savedInstanceState)
	 * for more information.
	 */
	public void setGameState(int state) {
		setGameState(state, null);
	}
	
	/**
	 * Set the state of the game and perform all the appropriate actions required by the new state.
	 * 
	 * @param state  The state to change the game to.
	 * @param savedInstanceState A Bundle containing saved game information.  This allows resuming a game in progress.
	 */
	public void setGameState(int state, Bundle savedInstanceState) {
		gameView.game.setState(state);
		
		switch (state) {
			case Game.LOAD_SAVE:				
				removeAllListeners();
				restoreSaveGame();
				break;
			case Game.STARTUP:
				removeAllListeners();
				gameView.game.initialize();
				
				twoPlayerDialog.setMessage("Touch to pick first player.");
				twoPlayerDialog.setNextState(Game.PICK_FIRST);
				twoPlayerDialog.show();
				break;
			case Game.PICK_FIRST:
				removeAllListeners();

				gameView.boardView.blackDiceView.roll();
				gameView.boardView.whiteDiceView.roll();
				
				if (gameView.game.getBlackPlayer().dice.getTotal() == gameView.game.getWhitePlayer().dice.getTotal()) {
					twoPlayerDialog.setMessage("Tie!  Touch to roll again.");
					twoPlayerDialog.setNextState(Game.PICK_FIRST);
					twoPlayerDialog.show();
				}
				else if (gameView.game.getBlackPlayer().dice.getTotal() > gameView.game.getWhitePlayer().dice.getTotal()) {
					gameView.game.setActivePlayer(gameView.game.getBlackPlayer());
					singlePlayerDialog.setMessage("Black goes first. Touch to roll.");
					singlePlayerDialog.setNextState(Game.ROLL);
					singlePlayerDialog.show();
				}
				else {
					gameView.game.setActivePlayer(gameView.game.getWhitePlayer());
					singlePlayerDialog.setMessage("White goes first.  Touch to roll.");
					singlePlayerDialog.setNextState(Game.ROLL);
					singlePlayerDialog.show();
				}
				
				break;
			case Game.ROLL:
				/* don't redo the roll if we're restoring from a saved bundle */
				if (savedInstanceState != null) { setGameState(Game.MOVE_PICK_SOURCE); }
				
				removeAllListeners();
				if (gameView.game.getActivePlayer().color == Constants.BLACK) {
					gameView.boardView.blackDiceView.roll();
				}
				else {
					gameView.boardView.whiteDiceView.roll();
				}

				/* Add the previous turn to the move history and continue */
				if (gameView.game.currentTurn != null) {
					gameView.game.gameLog.add(new Turn(gameView.game.currentTurn));
				}
				gameView.game.currentTurn = new Turn(gameView.game.getActivePlayer(), gameView.game.getActivePlayer().dice);
				
				setGameState(Game.MOVE_PICK_SOURCE);
				break;
			case Game.MOVE_PICK_SOURCE:
				removeAllListeners();
				clearDetectedMoves();
				
				gameView.game.getActivePlayer().selectedMove = null;
				gameView.game.setSourceSlot(null);

				// If we have pieces on the bar, that's our only option at first
				if (gameView.game.board.bar.containsPlayerPieces(gameView.game.getActivePlayer().color)) {
					gameView.game.setSourceSlot(gameView.game.board.bar);
					getAvailableMovesFromBar();
					if (gameView.game.getActivePlayer().moves.size() > 0) {
						Iterator<Move> moveIterator = gameView.game.getActivePlayer().moves.iterator();
						while (moveIterator.hasNext()) {
							Move move = moveIterator.next();
							AnimatedSlotView slotView = gameView.boardView.playSlotViews.get(move.endSlot.position);
							addListener(slotView,new MovePieceListener(move));
						}
					}
					else {
						// If we have no moves, throw up a dialog that points to SWITCH_PLAYER
						singlePlayerDialog.setMessage("Stuck on bar, touch to change players and roll dice.");
						singlePlayerDialog.setNextState(Game.SWITCH_PLAYER);
						singlePlayerDialog.show();
					}
				}
				else {
					getAllPlayerMoves();
					if (gameView.game.getActivePlayer().moves.size() > 0) {
						
						// add a handler for each slot that has valid moves
						Iterator<Move> moveIterator = gameView.game.getActivePlayer().moves.iterator();
						while (moveIterator.hasNext()) {
							Move move = moveIterator.next();
							AnimatedSlotView slotView = gameView.boardView.playSlotViews.get(move.startSlot.position);
							addListener(slotView,new SelectSlotListener(move.startSlot));
						}
					}
					else {
						// If we have no moves, throw up a dialog that points to SWITCH_PLAYER
						singlePlayerDialog.setMessage("Can't move, touch to change players and roll dice.");
						singlePlayerDialog.setNextState(Game.SWITCH_PLAYER);
						singlePlayerDialog.show();
					}
				}
				

				break;
			case Game.MOVE_PICK_DEST:
				/* Go back to the MOVE_PICK_SOURCE state if we're resuming from a saved bundle.*/
				if (savedInstanceState != null) { setGameState(Game.MOVE_PICK_SOURCE); }
				removeAllListeners();

				// If we started on the bar, we can touch the bar to cancel our move.
				if (gameView.game.getSourceSlot().equals(gameView.game.board.bar)) {
					addListener(gameView.boardView.combinedBarView.barView,new ChangeStateListener(Game.MOVE_PICK_SOURCE));
				}
				// otherwise, wire up the possible destination slots based on the source slot
				else {
					AnimatedSlotView sourceSlotView = gameView.boardView.playSlotViews.get(gameView.game.getSourceSlot().position);
					addListener(sourceSlotView,new ChangeStateListener(Game.MOVE_PICK_SOURCE));
				}
				
				// highlight the destination areas for the selected slot
				// and add handlers that point to MAKE_MOVE				
				Iterator<Move> moveIterator = gameView.game.getActivePlayer().movesForSlot(gameView.game.getSourceSlot()).iterator();
				while (moveIterator.hasNext()) {
					Move potentialMove = moveIterator.next();
					View destinationSlotView = null;
					if (potentialMove.endSlot instanceof Dugout) {
						if (gameView.game.getActivePlayer().color == Constants.BLACK) {
							destinationSlotView = gameView.boardView.combinedBarView.blackOutView;
						}
						else {
							destinationSlotView = gameView.boardView.combinedBarView.whiteOutView;
						}
					}
					else {
						destinationSlotView = gameView.boardView.playSlotViews.get(potentialMove.endSlot.position);
					}
					addListener(destinationSlotView,new MovePieceListener(potentialMove));
				}
				break;
			case Game.MAKE_MOVE:
				/* We assume that the move has already been made when restoring from a saved bundle. */
				if (savedInstanceState != null) { setGameState(Game.MOVE_PICK_SOURCE); }

				// move one piece from the source to the destination slot and invalidate both
				removeAllListeners();
				makeMove();
				
				break;
			case Game.SWITCH_PLAYER:
				/* If we are restoring from a save, don't switch players again. */
				if (savedInstanceState != null) { setGameState(Game.MOVE_PICK_SOURCE); }
				
				removeAllListeners();
				
				gameView.game.switchPlayers();
				// Combine these two steps into a single event for now
//				if (gameView.game.getActivePlayer().color == Constants.BLACK) {
//					singlePlayerDialog.setMessage("Click to roll Black dice.");
//				}
//				else {
//					singlePlayerDialog.setMessage("Click to roll White dice.");
//				}
//				singlePlayerDialog.setNextState(Game.ROLL);
//				singlePlayerDialog.show();
				setGameState(Game.ROLL);
				break;
			case Game.GAME_OVER:
				/* save the last turn to the list of moves */
				gameView.game.gameLog.add(new Turn(gameView.game.currentTurn));
				gameView.game.currentTurn = null;
				
				if (gameView.game.getActivePlayer().color == Constants.BLACK) {
					twoPlayerDialog.setMessage("Black Won!  Touch to Start New Game.");
				}
				else {
					twoPlayerDialog.setMessage("White Won!  Touch to Start New Game.");
				}
				twoPlayerDialog.setNextState(Game.STARTUP);
				twoPlayerDialog.show();
				break;
		}
	}

	public void makeMove() {
		Piece pieceToMove = null;
		View sourceSlotView = null;
		if (gameView.game.getSourceSlot().equals(gameView.game.board.bar)) {
			sourceSlotView = gameView.boardView.combinedBarView.barView;
			// get the first piece of my color and move it to the destination, then invalidate the bar and the destination
			Iterator<Piece> pieceIterator = gameView.game.board.bar.pieces.iterator();
			while (pieceIterator.hasNext()) {
				Piece thisPiece = pieceIterator.next();	
				if (thisPiece.color == gameView.game.getActivePlayer().color) {
					pieceToMove = thisPiece;
					break;
				}
			}
		}
		else {
			sourceSlotView = gameView.boardView.playSlotViews.get(gameView.game.getSourceSlot().position);
			pieceToMove = gameView.game.getSourceSlot().pieces.get(0);
		}
		
		if (pieceToMove != null) {
			// take the piece out of its old location
			gameView.game.getSourceSlot().removePiece(pieceToMove);
			sourceSlotView.invalidate();
								
			// add the piece to the new location
			if (gameView.game.getActivePlayer().selectedMove.endSlot.equals(gameView.game.board.blackOut)) {
				gameView.game.board.blackOut.addPiece(pieceToMove);
				gameView.boardView.combinedBarView.blackOutView.invalidate();
			}
			else if (gameView.game.getActivePlayer().selectedMove.endSlot.equals(gameView.game.board.whiteOut)) {
				gameView.game.board.whiteOut.addPiece(pieceToMove);
				gameView.boardView.combinedBarView.whiteOutView.invalidate();
			}
			else {
				Slot destinationSlot = gameView.game.getActivePlayer().selectedMove.endSlot;
				View destinationSlotView = gameView.boardView.playSlotViews.get(destinationSlot.position);
				// "bump" a piece from the slot if there's one of the opposite color there
				if (destinationSlot.pieces.size() > 0) {
					Piece targetPiece = destinationSlot.pieces.first();
					if (targetPiece.color != gameView.game.getActivePlayer().color) {
						gameView.game.getActivePlayer().selectedMove.pieceBumped = true;
						destinationSlot.removePiece(targetPiece);
						
						// The destination slot is already invalidated later, so we don't need to do it here.
						gameView.game.board.bar.addPiece(targetPiece);
						gameView.boardView.combinedBarView.barView.invalidate();
					}
				}
				
				// Now add the new piece to the slot
				destinationSlot.addPiece(pieceToMove);
				destinationSlotView.invalidate();
			}
			
			// Flag the die associated with this move as used
			gameView.game.getActivePlayer().selectedMove.die.setUsed();
			gameView.game.setSourceSlot(null);

			// Add the selected move to the list of moves for this turn (so we can undo the move on demand)
			gameView.game.currentTurn.moves.add(gameView.game.getActivePlayer().selectedMove);
			
			// Check to see if we won and switch the game state to GAME_OVER
			if (playerWon()) {
				setGameState(Game.GAME_OVER);
			}
			else if (gameView.game.getActivePlayer().hasMovesLeft()) {
				// Let the player start their next move if they have any left.
				setGameState(Game.MOVE_PICK_SOURCE);
			}
			else {
				// Switch players if there are no moves left
				twoPlayerDialog.setNextState(Game.SWITCH_PLAYER);
				twoPlayerDialog.setMessage("No moves left. Touch to change players and roll dice.");
				twoPlayerDialog.show();
			}
		}
		else {
			setGameState(Game.MOVE_PICK_SOURCE);
		}
	}

	/**
	 * Determine the slots the active player can move from.  Only used for normal slots, and not for the bar or dugouts.
	 * @return The list of slots that have valid moves.
	 */
	public void getAllPlayerMoves() {
		/* this detects all non-bar moves, there should be no others when we run this */
		gameView.game.getActivePlayer().moves.clear();
		List<Integer> uniquePositions = new ArrayList<Integer>();
		Iterator<Piece> pieceIterator = gameView.game.getActivePlayer().pieces.iterator();
		while (pieceIterator.hasNext()) {
			Piece piece = pieceIterator.next();
			int position = piece.position;
			if (position >= 0 && position <= 23 && !uniquePositions.contains(position)) { uniquePositions.add(position); }
		}

		for (int b=0; b < uniquePositions.size(); b++) {
			getAvailableMovesFromSlot(gameView.game.board.playSlots.get(uniquePositions.get(b)));
		}
	}
			
	/**
	 * Tag the moves that are possible from a given slot and add them to the player's list.
	 * @param slot  The slot to check.
	 */
	public void getAvailableMovesFromSlot(Slot slot) {
		int slotPosition = slot.position;
		List<Integer> uniqueDieValues = new ArrayList<Integer>();
		
		Iterator<SimpleDie> dieIterator = gameView.game.getActivePlayer().dice.iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			if (!die.isUsed() && !uniqueDieValues.contains(new Integer(die.getValue()))) {
				/* If we have doubles, we only want to add moves for the first die */
				uniqueDieValues.add(new Integer(die.getValue()));
				int diePosition = slotPosition + (gameView.game.getActivePlayer().color * die.getValue());
				if (diePosition >= 0 && diePosition <= 23) {
						Slot destinationSlot = gameView.game.board.playSlots.get(diePosition);
						if (!destinationSlot.isBlocked(gameView.game.getActivePlayer().color)) { 
							Move potentialMove = new Move(slot,destinationSlot,die);
							gameView.game.getActivePlayer().moves.add(potentialMove);
							destinationSlot.moves.add(potentialMove);
						}
				}
				else if (playerCanMoveOut()) {
					// If the piece is in the right position, it can always move out.
					// If it's the trailing edge of the player's pieces, it can move out with any roll high enough
					if (gameView.game.getActivePlayer().color == Constants.BLACK) { 
						
						if (diePosition == 24 || (gameView.game.getActivePlayer().pieces.first().position == slotPosition && diePosition > 24)) {
							Move potentialMove = new Move(slot,gameView.boardView.combinedBarView.blackOutView.slot,die);
							gameView.game.getActivePlayer().moves.add(potentialMove);
							gameView.boardView.combinedBarView.blackOutView.slot.moves.add(potentialMove);
						}
					}
					else { 
						if (diePosition == -1 || (gameView.game.getActivePlayer().pieces.last().position == slotPosition && diePosition < -1)) {
							Move potentialMove = new Move(slot,gameView.boardView.combinedBarView.whiteOutView.slot,die);
							gameView.game.getActivePlayer().moves.add(potentialMove);
							gameView.boardView.combinedBarView.whiteOutView.slot.moves.add(potentialMove);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Get the list of slots the active player can move to from the bar and add them to their moves.
	 */
	public void getAvailableMovesFromBar() {
		int startSlotPosition = 0;
		List<Integer> uniqueDieValues = new ArrayList<Integer>();

		/* we never allow moves from the bar and anywhere else, so clear the list of moves out */
		gameView.game.getActivePlayer().moves.clear();
		
		if (gameView.game.board.bar.containsPlayerPieces(gameView.game.getActivePlayer().color)) {
			if (gameView.game.getActivePlayer().color == Constants.BLACK) { startSlotPosition = -1; }
			else { startSlotPosition = 24; }

			Iterator<SimpleDie> dieIterator = gameView.game.getActivePlayer().dice.iterator();
			while (dieIterator.hasNext()) {
				SimpleDie die = dieIterator.next();
				if (!die.isUsed() && !uniqueDieValues.contains(new Integer(die.getValue()))) {
					/* If we have doubles, we only want to add moves for the first die */
					uniqueDieValues.add(new Integer(die.getValue()));
					
					int dieValue = die.getValue();
					int dieSlotPosition = startSlotPosition + (gameView.game.getActivePlayer().color * dieValue);
					Slot destinationSlot = gameView.game.board.playSlots.get(dieSlotPosition);
					if (!destinationSlot.isBlocked(gameView.game.getActivePlayer().color)) {
						Move potentialMove = new Move(gameView.game.board.bar, destinationSlot,die);
						gameView.game.getActivePlayer().moves.add(potentialMove);
						destinationSlot.moves.add(potentialMove);
					}
				}			
			}
		}
	}
	
	public boolean playerWon() {
		Dugout myDugout = null;

		if (gameView.game.getActivePlayer().color == Constants.BLACK) {
			myDugout = gameView.game.board.blackOut;
		}
		else {
			myDugout = gameView.game.board.whiteOut;
		}

		if (myDugout.pieces.size() == 15) { return true; }
		
		return false;
	}
	
	public boolean playerCanMoveOut() {
		boolean canMoveOut = true;
		if (gameView.game.board.bar.containsPlayerPieces(gameView.game.getActivePlayer().color)) {  canMoveOut = false;}
		if (gameView.game.getActivePlayer().color == Constants.BLACK) {
			if (gameView.game.getActivePlayer().pieces.first().position < 18) { canMoveOut = false; }
		}
		else {
			if (gameView.game.getActivePlayer().pieces.last().position > 5) { canMoveOut = false; }
		}

		return canMoveOut;
	}
	
	public void restoreSaveGame() {
		SavedGame savedGame = gameView.game.getSavedGame();
	
		if (savedGame != null) {
			if (savedGame.getActivePlayer() == Constants.BLACK) {
				gameView.game.setActivePlayer(gameView.game.getBlackPlayer());
			}
			else {
				gameView.game.setActivePlayer(gameView.game.getWhitePlayer());
			}

			gameView.game.getActivePlayer().setDiceState(savedGame.getActivePlayerDiceState());
			gameView.game.getInactivePlayer().setDiceState(savedGame.getInactivePlayerDiceState());
	
			gameView.boardView.blackDiceView.invalidate();
			gameView.boardView.whiteDiceView.invalidate();
			
			setGameState(savedGame.getGameState());
		}
		else {
			setGameState(Game.PICK_FIRST);
		}
	}
	
	public void undoMove() {
		// undo moves from this turn if there are any
		if (gameView.game.currentTurn != null) {
			if (gameView.game.currentTurn.moves.size() > 0) {
				// If this was the end of the turn, make sure the active player will be correct after the "undo"
				if (gameView.game.getActivePlayer().color != gameView.game.currentTurn.player.color) {
					Player currentActivePlayer = gameView.game.getActivePlayer();
					
					gameView.game.switchPlayers();
				}

				// Remove the last move from the list of moves
				Move lastMove = gameView.game.currentTurn.moves.get(gameView.game.currentTurn.moves.size()-1);
				gameView.game.currentTurn.moves.remove(lastMove);

				// Undo the last move
				Piece undoPiece = null;
				for (Piece piece: lastMove.endSlot.pieces) {
					if (piece.color == gameView.game.currentTurn.player.color) {
						undoPiece = piece;
						break;
					}
				}
				if (undoPiece != null) {
					lastMove.startSlot.addPiece(undoPiece);
					lastMove.endSlot.removePiece(undoPiece);
				}
			
				// If we bumped someone, put them back in their rightful place... :)
				if (lastMove.pieceBumped == true) {
					for (Piece piece : gameView.game.board.bar.pieces) {
						if (piece.color != gameView.game.currentTurn.player.color) {
							lastMove.endSlot.addPiece(piece);
							gameView.game.board.bar.removePiece(piece);
							break;
						}
					}
					
					gameView.boardView.combinedBarView.barView.invalidate();
				}
				
				// Spin through the dice and flag the first one with the right value as unused
				for (int a = 0; a < gameView.game.currentTurn.player.dice.size(); a++) {
					SimpleDie die = gameView.game.currentTurn.player.dice.get(a);
					if (die.getValue() == lastMove.die.getValue()) {
						if (die.isUsed() == true) {
							die.setUsed(false);
							break;
						}
					}
				}

				// Hide any messages that may have been displayed in the previous state
				twoPlayerDialog.setMessage(null);
				
				// Make sure the right dice are displayed
				if (gameView.game.getActivePlayer().color == Constants.BLACK) {
					gameView.boardView.blackDiceView.setVisibility(View.VISIBLE);
				}
				else {
					gameView.boardView.whiteDiceView.setVisibility(View.VISIBLE);
				}
				gameView.boardView.blackDiceView.invalidate();
				gameView.boardView.whiteDiceView.invalidate();
				
				// set the views as dirty
				// For the destination view, the dugouts are a special case
				if (lastMove.endSlot.equals(gameView.game.board.blackOut)) { 
					gameView.boardView.combinedBarView.blackOutView.invalidate();
				}
				else if (lastMove.endSlot.equals(gameView.game.board.whiteOut)) { 
					gameView.boardView.combinedBarView.whiteOutView.invalidate();
				}
				else {
					gameView.boardView.playSlotViews.get(lastMove.endSlot.position).invalidate();
				}
				
				// For the source view, only the bar is a special case
				if (lastMove.startSlot.equals(gameView.game.board.bar)) { 
					gameView.boardView.combinedBarView.barView.invalidate();
				}
				else {
					gameView.boardView.playSlotViews.get(lastMove.startSlot.position).invalidate();
				}
			}
			
			// clear out the previously selected source and destination slot
			gameView.game.setSourceSlot(null);
			gameView.game.setDestSlot(null);
		}
	}

	/*
	 * Convenience method to find the correct selectedMove, sourceSlot, and destinationSlot before calling makeMove()
	 */
	public void makeMove(int startPosition, int endPosition) {
		Iterator<Move> moveIterator = gameView.game.getActivePlayer().moves.iterator();
		while (moveIterator.hasNext()) {
			Move move = moveIterator.next();
			if (move.startSlot.position == startPosition && move.endSlot.position == endPosition) {
				gameView.game.getActivePlayer().selectedMove = move;
				gameView.game.setSourceSlot(move.startSlot);
				gameView.game.setDestSlot(move.endSlot);
				
				break;
			}
		}
		
		if (gameView.game.getSourceSlot() != null && gameView.game.getSourceSlot().position == startPosition &&
				gameView.game.getDestSlot() != null && gameView.game.getDestSlot().position == endPosition) {
			makeMove();
		}

	}

}
