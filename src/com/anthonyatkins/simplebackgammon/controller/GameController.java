package com.anthonyatkins.simplebackgammon.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.model.Bar;
import com.anthonyatkins.simplebackgammon.model.Dugout;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.Moves;
import com.anthonyatkins.simplebackgammon.model.Piece;
import com.anthonyatkins.simplebackgammon.model.Player;
import com.anthonyatkins.simplebackgammon.model.SimpleDice;
import com.anthonyatkins.simplebackgammon.model.SimpleDie;
import com.anthonyatkins.simplebackgammon.model.Slot;
import com.anthonyatkins.simplebackgammon.model.Turn;
import com.anthonyatkins.simplebackgammon.model.TurnMove;
import com.anthonyatkins.simplebackgammon.view.AnimatedSlotView;
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
		return this.gameView.getGame().getStartSlot();
	}

	public void setStartSlot(Slot startSlot) {
		this.gameView.getGame().setStartSlot(startSlot);
	}

	public Slot getEndSlot() {
		return this.gameView.getGame().getEndSlot();
	}

	public void setEndSlot(Slot endSlot) {
		this.gameView.getGame().setEndSlot(endSlot);
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
			gameView.getGame().getActivePlayer().setSelectedMove(potentialMove);
			gameView.getGame().setEndSlot(potentialMove.getEndSlot());
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
				slot.getMoves().clear();
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
			case Game.EXIT:
				activity.finish();
				break;
			case Game.STARTUP:
				removeAllListeners();
				
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

				new Turn(gameView.getGame().getActivePlayer(), gameView.getGame().getActivePlayer().getDice(), gameView.getGame());
				
				setGameState(Game.MOVE_PICK_SOURCE);
				break;
			case Game.MOVE_PICK_SOURCE:
				removeAllListeners();
				clearDetectedMoves();
				
				gameView.getGame().getActivePlayer().setSelectedMove(null);
				clearSelectedSlots();

				// If we have pieces on the bar, that's our only option at first
				if (gameView.getGame().getBoard().getBar().containsPlayerPieces(gameView.getGame().getActivePlayer().getColor())) {
					setStartSlot(gameView.getGame().getBoard().getBar());
					getAvailableMovesFromBar();
					if (gameView.getGame().getActivePlayer().getMoves().size() > 0) {
						Iterator<Move> moveIterator = gameView.getGame().getActivePlayer().getMoves().iterator();
						while (moveIterator.hasNext()) {
							Move move = moveIterator.next();
							AnimatedSlotView slotView = gameView.getBoardView().getPlaySlotViews().get(move.getEndSlot().getPosition());
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
					findAllPlayerMoves();
					if (gameView.getGame().getActivePlayer().getMoves().size() > 0) {
						
						// add a handler for each slot that has valid moves
						Iterator<Move> moveIterator = gameView.getGame().getActivePlayer().getMoves().iterator();
						while (moveIterator.hasNext()) {
							Move move = moveIterator.next();
							AnimatedSlotView slotView = gameView.getBoardView().getPlaySlotViews().get(move.getStartSlot().getPosition());
							addListener(slotView,new SelectSlotListener(move.getStartSlot(),this));
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
				removeAllListeners();

				// If we started on the bar, we can touch the bar to cancel our move.
				if (getStartSlot().equals(gameView.getGame().getBoard().getBar())) {
					addListener(gameView.getBoardView().getCombinedBarView().getBarView(),new ChangeStateListener(Game.MOVE_PICK_SOURCE));
				}
				// otherwise, wire up the possible destination slots based on the source slot
				else {
					AnimatedSlotView sourceSlotView = gameView.getBoardView().getPlaySlotViews().get(getStartSlot().getPosition());
					addListener(sourceSlotView,new ChangeStateListener(Game.MOVE_PICK_SOURCE));
				}
				
				// highlight the destination areas for the selected slot
				// and add handlers that point to MAKE_MOVE				
				Iterator<Move> moveIterator = gameView.getGame().getActivePlayer().movesForSlot(getStartSlot()).iterator();
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
						destinationSlotView = gameView.getBoardView().getPlaySlotViews().get(potentialMove.getEndSlot().getPosition());
					}
					addListener(destinationSlotView,new MovePieceListener(potentialMove));
				}
				break;
			case Game.MAKE_MOVE:
				// move one piece from the source to the destination slot and invalidate both
				removeAllListeners();
				makeMove();
				
				break;
			case Game.SWITCH_PLAYER:
				removeAllListeners();
				
				gameView.getGame().switchPlayers();
				
				Player activePlayer = gameView.getGame().getActivePlayer();
				new Turn(activePlayer,new SimpleDice(activePlayer.getColor()),gameView.getGame());
				
				setGameState(Game.ROLL);
				break;
			case Game.GAME_OVER:
				gameView.getGame().setFinished(true);
				
				// FIXME:  Save the game to the database
				
				if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) {
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

	public void makeMove() {
		makeMove(gameView.getGame().getStartSlot(),gameView.getGame().getEndSlot());
	}
	
	public void makeMove(int startSlotPosition, int endSlotPosition) {
		makeMove(gameView.getGame().getBoard().getPlaySlots().get(startSlotPosition),gameView.getGame().getBoard().getPlaySlots().get(endSlotPosition));
	}
	
	public void makeMove(Slot startSlot, Slot endSlot) {
		// This shouldn't be needed but just in case.
		gameView.getGame().setStartSlot(startSlot);
		gameView.getGame().setEndSlot(endSlot);
		Piece pieceToMove = null;
		View sourceSlotView = null;
		if (getStartSlot().equals(gameView.getGame().getBoard().getBar())) {
			sourceSlotView = gameView.getBoardView().getCombinedBarView().getBarView();
			// get the first piece of my color and move it to the destination, then invalidate the bar and the destination
			Iterator<Piece> pieceIterator = gameView.getGame().getBoard().getBar().getPieces().iterator();
			while (pieceIterator.hasNext()) {
				Piece thisPiece = pieceIterator.next();	
				if (thisPiece.color == gameView.getGame().getActivePlayer().getColor()) {
					pieceToMove = thisPiece;
					break;
				}
			}
		}
		else {
			sourceSlotView = gameView.getBoardView().getPlaySlotViews().get(getStartSlot().getPosition());
			pieceToMove = getStartSlot().getPieces().get(0);
		}
		
		if (pieceToMove != null) {
			// take the piece out of its old location
			getStartSlot().removePiece(pieceToMove);
			sourceSlotView.invalidate();

			// Flag the die associated with this move as used
			SimpleDie die = gameView.getGame().getActivePlayer().getSelectedMove().getDie();
			die.setUsed();
			TurnMove move = new TurnMove(getStartSlot(),getEndSlot(),die, gameView.getGame().getCurrentTurn());

			
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
				View destinationSlotView = gameView.getBoardView().getPlaySlotViews().get(destinationSlot.getPosition());
				// "bump" a piece from the slot if there's one of the opposite color there
				if (destinationSlot.getPieces().size() > 0) {
					Piece targetPiece = destinationSlot.getPieces().first();
					if (targetPiece.color != gameView.getGame().getActivePlayer().getColor()) {
						// FIXME:  Clean this up and confirm if the Player needs to know about his moves at all.
						gameView.getGame().getActivePlayer().getSelectedMove().setPieceBumped(true);
						move.setPieceBumped(true);
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
			
			clearSelectedSlots();
			
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

	public void clearSelectedSlots() {
		this.gameView.getGame().clearSelectedSlots();
	}

	/**
	 * Determine the slots the active player can move from.  Only used for normal slots, and not for the bar or dugouts.
	 * @return The list of slots that have valid moves.
	 */
	public Moves findAllPlayerMoves() {
		Moves moves = new Moves();
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
			Moves slotMoves = findAvailableMovesFromSlot(gameView.getGame().getBoard().getPlaySlots().get(uniquePositions.get(b)));
			moves.addAll(slotMoves);
		}
		
		return moves;
	}
			
	/**
	 * Tag the moves that are possible from a given slot and add them to the player's list.
	 * @param slot  The slot to check.
	 */
	public Moves findAvailableMovesFromSlot(Slot slot) {
		Moves slotMoves = new Moves();
		int slotPosition = slot.getPosition();
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
							Move potentialMove = new Move(slot,destinationSlot,die, gameView.getGame().getActivePlayer());
							slotMoves.add(potentialMove);
							gameView.getGame().getActivePlayer().getMoves().add(potentialMove);
							destinationSlot.getMoves().add(potentialMove);
						}
				}
				else if (playerCanMoveOut()) {
					// If the piece is in the right position, it can always move out.
					// If it's the trailing edge of the player's pieces, it can move out with any roll high enough
					if (gameView.getGame().getActivePlayer().getColor() == Constants.BLACK) { 
						
						if (diePosition == 24 || (gameView.getGame().getActivePlayer().getPieces().first().position == slotPosition && diePosition > 24)) {
							Move potentialMove = new Move(slot,gameView.getBoardView().getCombinedBarView().getBlackOutView().slot,die, gameView.getGame().getActivePlayer());
							slotMoves.add(potentialMove);
							gameView.getGame().getActivePlayer().getMoves().add(potentialMove);
							gameView.getBoardView().getCombinedBarView().getBlackOutView().slot.getMoves().add(potentialMove);
						}
					}
					else { 
						if (diePosition == -1 || (gameView.getGame().getActivePlayer().getPieces().last().position == slotPosition && diePosition < -1)) {
							Move potentialMove = new Move(slot,gameView.getBoardView().getCombinedBarView().getWhiteOutView().slot,die, gameView.getGame().getActivePlayer());
							slotMoves.add(potentialMove);
							gameView.getGame().getActivePlayer().getMoves().add(potentialMove);
							gameView.getBoardView().getCombinedBarView().getWhiteOutView().slot.getMoves().add(potentialMove);
						}
					}
				}
			}
		}
		
		return slotMoves;
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
						Move potentialMove = new Move(gameView.getGame().getBoard().getBar(), destinationSlot,die, gameView.getGame().getActivePlayer());
						gameView.getGame().getActivePlayer().getMoves().add(potentialMove);
						destinationSlot.getMoves().add(potentialMove);
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

		if (myDugout.getPieces().size() == 15) { return true; }
		
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
				Piece undoPiece = lastMove.getEndSlot().getPieces().last();
				lastMove.getStartSlot().addPiece(undoPiece);
				lastMove.getEndSlot().removePiece(undoPiece);

				//If we bumped a piece in the last move, put it back.
				if (lastMove.isPieceBumped()) {
					Bar bar = gameView.getGame().getBoard().getBar();
					for (Piece piece: bar.getPieces()) {
						if (piece.color != gameView.getGame().getActivePlayer().getColor()) {
							bar.removePiece(piece);
							lastMove.getEndSlot().addPiece(piece);
							break;
						}
					}
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
					gameView.getBoardView().getPlaySlotViews().get(lastMove.getEndSlot().getPosition()).invalidate();
				}
				
				// For the source view, only the bar is a special case
				if (lastMove.getStartSlot().equals(gameView.getGame().getBoard().getBar())) { 
					gameView.getBoardView().getCombinedBarView().getBarView().invalidate();
				}
				else {
					gameView.getBoardView().getPlaySlotViews().get(lastMove.getStartSlot().getPosition()).invalidate();
				}
			}
			
			clearSelectedSlots();
		}
	}
}
