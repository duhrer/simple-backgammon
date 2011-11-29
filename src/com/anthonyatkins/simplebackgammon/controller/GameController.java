package com.anthonyatkins.simplebackgammon.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.model.Dugout;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.Piece;
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
		singlePlayerDialog = new SinglePlayerContinueDialog(gameView.getContext(),gameView,this,gameView.getTheme());
		twoPlayerDialog = new TwoPlayerDialog(gameView.getContext(),this,gameView.getTheme());
	}

	private class ChangeStateListener implements OnClickListener {
		int nextStep;
		public ChangeStateListener (int nextStep) {
			this.nextStep = nextStep;
		}
		@Override
		public void onClick(View v) {
			setGameState(nextStep);
			v.invalidate();
		}
		
	}
 	
	private class SelectSlotListener implements OnClickListener {
		private Slot selectedSlot;
		@Override
		public void onClick(View view) {
			gameView.getGame().setSourceSlot(this.selectedSlot);
			setGameState(Game.MOVE_PICK_DEST);
		}

		public SelectSlotListener(Slot selectedSlot) {
			this.selectedSlot = selectedSlot;
		}		
	}

	private class MovePieceListener implements OnClickListener {
		Move potentialMove;
		
		@Override
		public void onClick(View v) {
			gameView.getGame().getActivePlayer().setSelectedMove(potentialMove);
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
		if (gameView.getGame().getActivePlayer() != null) {
			gameView.getGame().getActivePlayer().getMoves().clear();
			for (Slot slot: gameView.getGame().getBoard().getPlaySlots()) {
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
		gameView.getGame().setState(state);
		
		switch (state) {
			case Game.STARTUP:
				removeAllListeners();
				gameView.getGame().initialize();
				
				twoPlayerDialog.setMessage("Touch to pick first player.");
				twoPlayerDialog.setNextState(Game.PICK_FIRST);
				twoPlayerDialog.show();
				break;
			case Game.PICK_FIRST:
				removeAllListeners();

				gameView.getBoardView().getBlackDiceView().roll();
				gameView.getBoardView().getWhiteDiceView().roll();
				
				if (gameView.getGame().getBlackPlayer().getDice().getTotal() == gameView.getGame().getWhitePlayer().getDice().getTotal()) {
					twoPlayerDialog.setMessage("Tie!  Touch to roll again.");
					twoPlayerDialog.setNextState(Game.PICK_FIRST);
					twoPlayerDialog.show();
				}
				else if (gameView.getGame().getBlackPlayer().getDice().getTotal() > gameView.getGame().getWhitePlayer().getDice().getTotal()) {
					gameView.getGame().setActivePlayer(gameView.getGame().getBlackPlayer());
					singlePlayerDialog.setMessage("Black goes first. Touch to roll.");
					singlePlayerDialog.setNextState(Game.ROLL);
					singlePlayerDialog.show();
				}
				else {
					gameView.getGame().setActivePlayer(gameView.getGame().getWhitePlayer());
					singlePlayerDialog.setMessage("White goes first.  Touch to roll.");
					singlePlayerDialog.setNextState(Game.ROLL);
					singlePlayerDialog.show();
				}
				
				break;
			case Game.ROLL:
				/* don't redo the roll if we're restoring from a saved bundle */
				if (savedInstanceState != null) { setGameState(Game.MOVE_PICK_SOURCE); }
				
				removeAllListeners();
				if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) {
					gameView.getBoardView().getBlackDiceView().roll();
				}
				else {
					gameView.getBoardView().getWhiteDiceView().roll();
				}

				/* Add the previous turn to the move history and continue */
				if (gameView.getGame().getCurrentTurn() != null) {
					gameView.getGame().getGameLog().add(new Turn(gameView.getGame().getCurrentTurn()));
				}
				gameView.getGame().setCurrentTurn(new Turn(gameView.getGame().getActivePlayer(), gameView.getGame().getActivePlayer().getDice()));
				
				setGameState(Game.MOVE_PICK_SOURCE);
				break;
			case Game.MOVE_PICK_SOURCE:
				removeAllListeners();
				clearDetectedMoves();
				
				gameView.getGame().getActivePlayer().setSelectedMove(null);
				gameView.getGame().setSourceSlot(null);

				// If we have pieces on the bar, that's our only option at first
				if (gameView.getGame().getBoard().getBar().containsPlayerPieces(gameView.getGame().getActivePlayer().getColor())) {
					gameView.getGame().setSourceSlot(gameView.getGame().getBoard().getBar());
					getAvailableMovesFromBar();
					if (gameView.getGame().getActivePlayer().getMoves().size() > 0) {
						Iterator<Move> moveIterator = gameView.getGame().getActivePlayer().getMoves().iterator();
						while (moveIterator.hasNext()) {
							Move move = moveIterator.next();
							AnimatedSlotView slotView = gameView.getBoardView().getPlaySlotViews().get(move.getEndSlot().position);
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
					if (gameView.getGame().getActivePlayer().getMoves().size() > 0) {
						
						// add a handler for each slot that has valid moves
						Iterator<Move> moveIterator = gameView.getGame().getActivePlayer().getMoves().iterator();
						while (moveIterator.hasNext()) {
							Move move = moveIterator.next();
							AnimatedSlotView slotView = gameView.getBoardView().getPlaySlotViews().get(move.getStartSlot().position);
							addListener(slotView,new SelectSlotListener(move.getStartSlot()));
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
				if (gameView.getGame().getSourceSlot().equals(gameView.getGame().getBoard().getBar())) {
					addListener(gameView.getBoardView().getCombinedBarView().getBarView(),new ChangeStateListener(Game.MOVE_PICK_SOURCE));
				}
				// otherwise, wire up the possible destination slots based on the source slot
				else {
					AnimatedSlotView sourceSlotView = gameView.getBoardView().getPlaySlotViews().get(gameView.getGame().getSourceSlot().position);
					addListener(sourceSlotView,new ChangeStateListener(Game.MOVE_PICK_SOURCE));
				}
				
				// highlight the destination areas for the selected slot
				// and add handlers that point to MAKE_MOVE				
				Iterator<Move> moveIterator = gameView.getGame().getActivePlayer().movesForSlot(gameView.getGame().getSourceSlot()).iterator();
				while (moveIterator.hasNext()) {
					Move potentialMove = moveIterator.next();
					View destinationSlotView = null;
					if (potentialMove.getEndSlot() instanceof Dugout) {
						if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) {
							destinationSlotView = gameView.getBoardView().getCombinedBarView().getBlackOutView();
						}
						else {
							destinationSlotView = gameView.getBoardView().getCombinedBarView().getWhiteOutView();
						}
					}
					else {
						destinationSlotView = gameView.getBoardView().getPlaySlotViews().get(potentialMove.getEndSlot().position);
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
				
				gameView.getGame().switchPlayers();
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
				gameView.getGame().getGameLog().add(new Turn(gameView.getGame().getCurrentTurn()));
				gameView.getGame().setCurrentTurn(null);
				
				if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) {
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
		if (gameView.getGame().getSourceSlot().equals(gameView.getGame().getBoard().getBar())) {
			sourceSlotView = gameView.getBoardView().getCombinedBarView().getBarView();
			// get the first piece of my color and move it to the destination, then invalidate the bar and the destination
			Iterator<Piece> pieceIterator = gameView.getGame().getBoard().getBar().pieces.iterator();
			while (pieceIterator.hasNext()) {
				Piece thisPiece = pieceIterator.next();	
				if (thisPiece.color == gameView.getGame().getActivePlayer().getColor()) {
					pieceToMove = thisPiece;
					break;
				}
			}
		}
		else {
			sourceSlotView = gameView.getBoardView().getPlaySlotViews().get(gameView.getGame().getSourceSlot().position);
			pieceToMove = gameView.getGame().getSourceSlot().pieces.get(0);
		}
		
		if (pieceToMove != null) {
			// take the piece out of its old location
			gameView.getGame().getSourceSlot().removePiece(pieceToMove);
			sourceSlotView.invalidate();
								
			// add the piece to the new location
			if (gameView.getGame().getActivePlayer().getSelectedMove().getEndSlot().equals(gameView.getGame().getBoard().getBlackOut())) {
				gameView.getGame().getBoard().getBlackOut().addPiece(pieceToMove);
				gameView.getBoardView().getCombinedBarView().getBlackOutView().invalidate();
			}
			else if (gameView.getGame().getActivePlayer().getSelectedMove().getEndSlot().equals(gameView.getGame().getBoard().getWhiteOut())) {
				gameView.getGame().getBoard().getWhiteOut().addPiece(pieceToMove);
				gameView.getBoardView().getCombinedBarView().getWhiteOutView().invalidate();
			}
			else {
				Slot destinationSlot = gameView.getGame().getActivePlayer().getSelectedMove().getEndSlot();
				View destinationSlotView = gameView.getBoardView().getPlaySlotViews().get(destinationSlot.position);
				// "bump" a piece from the slot if there's one of the opposite color there
				if (destinationSlot.pieces.size() > 0) {
					Piece targetPiece = destinationSlot.pieces.first();
					if (targetPiece.color != gameView.getGame().getActivePlayer().getColor()) {
						gameView.getGame().getActivePlayer().getSelectedMove().setPieceBumped(true);
						destinationSlot.removePiece(targetPiece);
						
						// The destination slot is already invalidated later, so we don't need to do it here.
						gameView.getGame().getBoard().getBar().addPiece(targetPiece);
						gameView.getBoardView().getCombinedBarView().getBarView().invalidate();
					}
				}
				
				// Now add the new piece to the slot
				destinationSlot.addPiece(pieceToMove);
				destinationSlotView.invalidate();
			}
			
			// Flag the die associated with this move as used
			gameView.getGame().getActivePlayer().getSelectedMove().getDie().setUsed();
			gameView.getGame().setSourceSlot(null);

			// Add the selected move to the list of moves for this turn (so we can undo the move on demand)
			gameView.getGame().getCurrentTurn().getMoves().add(gameView.getGame().getActivePlayer().getSelectedMove());
			
			// Check to see if we won and switch the game state to GAME_OVER
			if (playerWon()) {
				setGameState(Game.GAME_OVER);
			}
			else if (gameView.getGame().getActivePlayer().hasMovesLeft()) {
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
		gameView.getGame().getActivePlayer().getMoves().clear();
		List<Integer> uniquePositions = new ArrayList<Integer>();
		Iterator<Piece> pieceIterator = gameView.getGame().getActivePlayer().getPieces().iterator();
		while (pieceIterator.hasNext()) {
			Piece piece = pieceIterator.next();
			int position = piece.position;
			if (position >= 0 && position <= 23 && !uniquePositions.contains(position)) { uniquePositions.add(position); }
		}

		for (int b=0; b < uniquePositions.size(); b++) {
			getAvailableMovesFromSlot(gameView.getGame().getBoard().getPlaySlots().get(uniquePositions.get(b)));
		}
	}
			
	/**
	 * Tag the moves that are possible from a given slot and add them to the player's list.
	 * @param slot  The slot to check.
	 */
	public void getAvailableMovesFromSlot(Slot slot) {
		int slotPosition = slot.position;
		List<Integer> uniqueDieValues = new ArrayList<Integer>();
		
		Iterator<SimpleDie> dieIterator = gameView.getGame().getActivePlayer().getDice().iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			if (!die.isUsed() && !uniqueDieValues.contains(new Integer(die.getValue()))) {
				/* If we have doubles, we only want to add moves for the first die */
				uniqueDieValues.add(new Integer(die.getValue()));
				int diePosition = slotPosition + (gameView.getGame().getActivePlayer().getColor() * die.getValue());
				if (diePosition >= 0 && diePosition <= 23) {
						Slot destinationSlot = gameView.getGame().getBoard().getPlaySlots().get(diePosition);
						if (!destinationSlot.isBlocked(gameView.getGame().getActivePlayer().getColor())) { 
							Move potentialMove = new Move(slot,destinationSlot,die);
							gameView.getGame().getActivePlayer().getMoves().add(potentialMove);
							destinationSlot.moves.add(potentialMove);
						}
				}
				else if (playerCanMoveOut()) {
					// If the piece is in the right position, it can always move out.
					// If it's the trailing edge of the player's pieces, it can move out with any roll high enough
					if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) { 
						
						if (diePosition == 24 || (gameView.getGame().getActivePlayer().getPieces().first().position == slotPosition && diePosition > 24)) {
							Move potentialMove = new Move(slot,gameView.getBoardView().getCombinedBarView().getBlackOutView().slot,die);
							gameView.getGame().getActivePlayer().getMoves().add(potentialMove);
							gameView.getBoardView().getCombinedBarView().getBlackOutView().slot.moves.add(potentialMove);
						}
					}
					else { 
						if (diePosition == -1 || (gameView.getGame().getActivePlayer().getPieces().last().position == slotPosition && diePosition < -1)) {
							Move potentialMove = new Move(slot,gameView.getBoardView().getCombinedBarView().getWhiteOutView().slot,die);
							gameView.getGame().getActivePlayer().getMoves().add(potentialMove);
							gameView.getBoardView().getCombinedBarView().getWhiteOutView().slot.moves.add(potentialMove);
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
		gameView.getGame().getActivePlayer().getMoves().clear();
		
		if (gameView.getGame().getBoard().getBar().containsPlayerPieces(gameView.getGame().getActivePlayer().getColor())) {
			if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) { startSlotPosition = -1; }
			else { startSlotPosition = 24; }

			Iterator<SimpleDie> dieIterator = gameView.getGame().getActivePlayer().getDice().iterator();
			while (dieIterator.hasNext()) {
				SimpleDie die = dieIterator.next();
				if (!die.isUsed() && !uniqueDieValues.contains(new Integer(die.getValue()))) {
					/* If we have doubles, we only want to add moves for the first die */
					uniqueDieValues.add(new Integer(die.getValue()));
					
					int dieValue = die.getValue();
					int dieSlotPosition = startSlotPosition + (gameView.getGame().getActivePlayer().getColor() * dieValue);
					Slot destinationSlot = gameView.getGame().getBoard().getPlaySlots().get(dieSlotPosition);
					if (!destinationSlot.isBlocked(gameView.getGame().getActivePlayer().getColor())) {
						Move potentialMove = new Move(gameView.getGame().getBoard().getBar(), destinationSlot,die);
						gameView.getGame().getActivePlayer().getMoves().add(potentialMove);
						destinationSlot.moves.add(potentialMove);
					}
				}			
			}
		}
	}
	
	public boolean playerWon() {
		Dugout myDugout = null;

		if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) {
			myDugout = gameView.getGame().getBoard().getBlackOut();
		}
		else {
			myDugout = gameView.getGame().getBoard().getWhiteOut();
		}

		if (myDugout.pieces.size() == 15) { return true; }
		
		return false;
	}
	
	public boolean playerCanMoveOut() {
		boolean canMoveOut = true;
		if (gameView.getGame().getBoard().getBar().containsPlayerPieces(gameView.getGame().getActivePlayer().getColor())) {  canMoveOut = false;}
		if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) {
			if (gameView.getGame().getActivePlayer().getPieces().first().position < 18) { canMoveOut = false; }
		}
		else {
			if (gameView.getGame().getActivePlayer().getPieces().last().position > 5) { canMoveOut = false; }
		}

		return canMoveOut;
	}
	
	public void undoMove() {
		// undo moves from this turn if there are any
		if (gameView.getGame().getCurrentTurn() != null) {
			if (gameView.getGame().getCurrentTurn().getMoves().size() > 0) {
				// If this was the end of the turn, make sure the active player will be correct after the "undo"
				if (gameView.getGame().getActivePlayer().getColor() != gameView.getGame().getCurrentTurn().getPlayer().getColor()) {
					gameView.getGame().switchPlayers();
				}

				// Remove the last move from the list of moves
				Move lastMove = gameView.getGame().getCurrentTurn().getMoves().get(gameView.getGame().getCurrentTurn().getMoves().size()-1);
				gameView.getGame().getCurrentTurn().getMoves().remove(lastMove);

				// Undo the last move
				Piece undoPiece = null;
				for (Piece piece: lastMove.getEndSlot().pieces) {
					if (piece.color == gameView.getGame().getCurrentTurn().getPlayer().getColor()) {
						undoPiece = piece;
						break;
					}
				}
				if (undoPiece != null) {
					lastMove.getStartSlot().addPiece(undoPiece);
					lastMove.getEndSlot().removePiece(undoPiece);
				}
			
				// If we bumped someone, put them back in their rightful place... :)
				if (lastMove.isPieceBumped() == true) {
					for (Piece piece : gameView.getGame().getBoard().getBar().pieces) {
						if (piece.color != gameView.getGame().getCurrentTurn().getPlayer().getColor()) {
							lastMove.getEndSlot().addPiece(piece);
							gameView.getGame().getBoard().getBar().removePiece(piece);
							break;
						}
					}
					
					gameView.getBoardView().getCombinedBarView().getBarView().invalidate();
				}
				
				// Spin through the dice and flag the first one with the right value as unused
				for (int a = 0; a < gameView.getGame().getCurrentTurn().getPlayer().getDice().size(); a++) {
					SimpleDie die = gameView.getGame().getCurrentTurn().getPlayer().getDice().get(a);
					if (die.getValue() == lastMove.getDie().getValue()) {
						if (die.isUsed() == true) {
							die.setUsed(false);
							break;
						}
					}
				}

				// Hide any messages that may have been displayed in the previous state
				twoPlayerDialog.setMessage(null);
				
				// Make sure the right dice are displayed
				if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) {
					gameView.getBoardView().getBlackDiceView().setVisibility(View.VISIBLE);
				}
				else {
					gameView.getBoardView().getWhiteDiceView().setVisibility(View.VISIBLE);
				}
				gameView.getBoardView().getBlackDiceView().invalidate();
				gameView.getBoardView().getWhiteDiceView().invalidate();
				
				// set the views as dirty
				// For the destination view, the dugouts are a special case
				if (lastMove.getEndSlot().equals(gameView.getGame().getBoard().getBlackOut())) { 
					gameView.getBoardView().getCombinedBarView().getBlackOutView().invalidate();
				}
				else if (lastMove.getEndSlot().equals(gameView.getGame().getBoard().getWhiteOut())) { 
					gameView.getBoardView().getCombinedBarView().getWhiteOutView().invalidate();
				}
				else {
					gameView.getBoardView().getPlaySlotViews().get(lastMove.getEndSlot().position).invalidate();
				}
				
				// For the source view, only the bar is a special case
				if (lastMove.getStartSlot().equals(gameView.getGame().getBoard().getBar())) { 
					gameView.getBoardView().getCombinedBarView().getBarView().invalidate();
				}
				else {
					gameView.getBoardView().getPlaySlotViews().get(lastMove.getStartSlot().position).invalidate();
				}
			}
			
			// clear out the previously selected source and destination slot
			gameView.getGame().setSourceSlot(null);
			gameView.getGame().setDestSlot(null);
		}
	}

	/*
	 * Convenience method to find the correct selectedMove, sourceSlot, and destinationSlot before calling makeMove()
	 */
	public void makeMove(int startPosition, int endPosition) {
		Iterator<Move> moveIterator = gameView.getGame().getActivePlayer().getMoves().iterator();
		while (moveIterator.hasNext()) {
			Move move = moveIterator.next();
			if (move.getStartSlot().position == startPosition && move.getEndSlot().position == endPosition) {
				gameView.getGame().getActivePlayer().setSelectedMove(move);
				gameView.getGame().setSourceSlot(move.getStartSlot());
				gameView.getGame().setDestSlot(move.getEndSlot());
				
				break;
			}
		}
		
		if (gameView.getGame().getSourceSlot() != null && gameView.getGame().getSourceSlot().position == startPosition &&
				gameView.getGame().getDestSlot() != null && gameView.getGame().getDestSlot().position == endPosition) {
			makeMove();
		}

	}

}
