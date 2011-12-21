package com.anthonyatkins.simplebackgammon.controller;

import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.exception.InvalidMoveException;
import com.anthonyatkins.simplebackgammon.model.Dugout;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.Moves;
import com.anthonyatkins.simplebackgammon.model.Slot;
import com.anthonyatkins.simplebackgammon.view.AnimatedSlotView;
import com.anthonyatkins.simplebackgammon.view.DiceView;
import com.anthonyatkins.simplebackgammon.view.GameView;
import com.anthonyatkins.simplebackgammon.view.SinglePlayerContinueDialog;
import com.anthonyatkins.simplebackgammon.view.TwoPlayerDialog;

public class GameController {
	private final GameView gameView;
	private SinglePlayerContinueDialog singlePlayerDialog;
	private TwoPlayerDialog twoPlayerDialog;
	private final Activity activity;
	
	public GameController(GameView gameView, Activity activity) {
		this.gameView = gameView;
		this.activity = activity;
		singlePlayerDialog = new SinglePlayerContinueDialog(gameView.getContext(),gameView,this,gameView.getTheme());
		twoPlayerDialog = new TwoPlayerDialog(gameView.getContext(),this,gameView.getTheme());
	}

	public Slot getStartSlot() {
		return this.gameView.getGame().getCurrentTurn().getStartSlot();
	}

	public void setStartSlot(Slot startSlot) {
		this.gameView.getGame().getCurrentTurn().setStartSlot(startSlot);
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
		private final Slot selectedSlot;
		private final GameController controller;
		@Override
		public void onClick(View view) {
			this.controller.setStartSlot(this.getSelectedSlot());
			setGameState(Game.MOVE_PICK_DEST);
		}

		public SelectSlotListener(Slot selectedSlot, GameController controller) {
			this.selectedSlot = selectedSlot;
			this.controller = controller;
		}

		public Slot getSelectedSlot() {
			return selectedSlot;
		}		
	}

	private class MovePieceListener implements OnClickListener {
		Move potentialMove;
		
		@Override
		public void onClick(View v) {
			makeMove(potentialMove);
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
		gameView.getGame().getCurrentTurn().getPotentialMoves().clear();
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
			case Game.EXIT:
				activity.finish();
				break;
			case Game.STARTUP:
				// This is the default state for new games, any sanity checks, animations, etc. that the controller needs to run will be launched from here.
				
				// Move the game on to the first dice roll.
				setGameState(Game.ROLL);
				
				break;
			case Game.ROLL:
				/* don't redo the roll if we're restoring from a saved bundle */
				if (savedInstanceState != null) { setGameState(Game.MOVE_PICK_SOURCE); }
				
				removeAllListeners();

				// Show the right dice and display their value
				DiceView activeDiceView;
				if (gameView.getGame().getCurrentTurn().getColor() == Constants.BLACK) {
					activeDiceView = gameView.getBoardView().getBlackDiceView();
				}
				else {
					activeDiceView = gameView.getBoardView().getWhiteDiceView();
				}
				
				// update the active dice based on the value of this turn's roll
				activeDiceView.addDieViews();
				setGameState(Game.MOVE_PICK_SOURCE);
				break;
			case Game.MOVE_PICK_SOURCE:
				removeAllListeners();
				clearDetectedMoves();
				
				gameView.getGame().getCurrentTurn().setSelectedMove(null);
				clearSelectedSlots();
				Moves potentialMoves = gameView.getGame().getCurrentTurn().getPotentialMoves();
				
				if (potentialMoves.size() > 0) {
					// If we have pieces on the bar, that's our only option at first
					if (gameView.getGame().getBoard().getBar().containsPlayerPieces(gameView.getGame().getCurrentTurn().getColor())) {
						setStartSlot(gameView.getGame().getBoard().getBar());
						setGameState(Game.MOVE_PICK_DEST);
					}
					else {
						Iterator<Move> moveIterator = potentialMoves.iterator();
						while (moveIterator.hasNext()) {
							Move move = moveIterator.next();
							AnimatedSlotView slotView = gameView.getBoardView().getPlaySlotViews().get(move.getStartSlot().getPosition());
							addListener(slotView,new SelectSlotListener(move.getStartSlot(),this));
						}
					}
				}
				else {
					if (gameView.getGame().getBoard().getBar().containsPlayerPieces(gameView.getGame().getCurrentTurn().getColor())) {
						// If we have no moves, throw up a dialog that points to SWITCH_PLAYER
						singlePlayerDialog.setMessage("Stuck on bar, touch to change players and roll dice.");
					}
					else {
						// If we have no moves, throw up a dialog that points to SWITCH_PLAYER
						singlePlayerDialog.setMessage("Can't move, touch to change players and roll dice.");
					}
					singlePlayerDialog.setNextState(Game.NEW_TURN);
					singlePlayerDialog.show();
				}
				break;
			case Game.MOVE_PICK_DEST:
				removeAllListeners();

				// If we started on the bar, we can touch the bar to cancel our move.
				if (getStartSlot() != null && getStartSlot().equals(gameView.getGame().getBoard().getBar())) {
					addListener(gameView.getBoardView().getCombinedBarView().getBarView(),new ChangeStateListener(Game.MOVE_PICK_SOURCE));
				}
				// otherwise, wire up the possible destination slots based on the source slot
				else {
					AnimatedSlotView sourceSlotView = gameView.getBoardView().getPlaySlotViews().get(getStartSlot().getPosition());
					addListener(sourceSlotView,new ChangeStateListener(Game.MOVE_PICK_SOURCE));
				}
				
				// highlight the destination areas for the selected slot
				// and add handlers that point to MAKE_MOVE				
				Iterator<Move> moveIterator = gameView.getGame().getCurrentTurn().getPotentialMoves().getMovesForStartSlot(getStartSlot()).iterator();
				while (moveIterator.hasNext()) {
					Move potentialMove = moveIterator.next();
					View destinationSlotView = null;
					if (potentialMove.getEndSlot() instanceof Dugout) {
						if (gameView.getGame().getCurrentTurn().getColor() == Constants.BLACK) {
							destinationSlotView = gameView.getBoardView().getCombinedBarView().getBlackOutView();
						}
						else {
							destinationSlotView = gameView.getBoardView().getCombinedBarView().getWhiteOutView();
						}
					}
					else {
						destinationSlotView = gameView.getBoardView().getPlaySlotViews().get(potentialMove.getEndSlot().getPosition());
					}

					addListener(destinationSlotView,new MovePieceListener(potentialMove));
				}
				break;
			case Game.NEW_TURN:
				removeAllListeners();
				gameView.getGame().newTurn();
				setGameState(Game.ROLL);
				break;
			case Game.GAME_OVER:
				gameView.getGame().setFinished(true);
				
				// FIXME:  Save the game to the database
				
				if (gameView.getGame().getCurrentTurn().getColor() == Constants.BLACK) {
					twoPlayerDialog.setMessage("Black Won!  Touch the screen to continue.");
				}
				else {
					twoPlayerDialog.setMessage("White Won!  Touch the screen to continue.");
				}

				twoPlayerDialog.setNextState(Game.EXIT);
				twoPlayerDialog.show();
				break;
		}
	}

	public void undoMove() throws InvalidMoveException {
		gameView.getGame().getCurrentTurn().undoMove();

		// Hide any messages that may have been displayed in the previous state
		twoPlayerDialog.setMessage(null);
	}
	
	public void makeMove(Move potentialMove) {
		gameView.getGame().getCurrentTurn().pickMove(potentialMove);
		
		// Check to see if we won and switch the game state to GAME_OVER
		if (gameView.getGame().playerWon()) {
			setGameState(Game.GAME_OVER);
		}
		else if (gameView.getGame().getCurrentTurn().movesLeft()) {
			// Let the player start their next move if they have any left.
			setGameState(Game.MOVE_PICK_SOURCE);
		}
		else {
			// Switch players if there are no moves left
			twoPlayerDialog.setNextState(Game.NEW_TURN);
			twoPlayerDialog.setMessage("No moves left. Touch to change players and roll dice.");
			twoPlayerDialog.show();
		}
	}

	public void clearSelectedSlots() {
		gameView.getGame().getCurrentTurn().clearStartSlot();
	}
}
