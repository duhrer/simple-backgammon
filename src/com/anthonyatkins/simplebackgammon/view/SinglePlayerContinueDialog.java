package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.anthonyatkins.simplebackgammon.R;
import com.anthonyatkins.simplebackgammon.controller.GameController;
import com.anthonyatkins.simplebackgammon.model.Game;

public class SinglePlayerContinueDialog extends android.app.Dialog {
	private int nextState = Game.PICK_FIRST;
	private GameController gameController;
	private SinglePlayerDialogView dialogView;
	private GameView gameView;
	
	public SinglePlayerContinueDialog(Context context, GameView gameView, GameController gameController, Palette theme) {
		super(context,R.style.my_dialog_theme);
		this.gameController = gameController;
		this.gameView = gameView;
		this.dialogView = new SinglePlayerDialogView(context, gameView, theme);
		ContinueListener continueListener = new ContinueListener();
		dialogView.setOnClickListener(continueListener);
		setOnCancelListener(continueListener);
		this.setCancelable(true);
		this.setContentView(dialogView);
	}
	
	
	public void setMessage(String text) {
		dialogView.setMessage(text);
		dialogView.invalidate();
	}
	
	public void setNextState(int nextState) {
		this.nextState = nextState;
	}
	
	private class ContinueListener implements android.app.Dialog.OnClickListener, View.OnClickListener, android.app.Dialog.OnCancelListener {
		private void keepGoing() {
			dismiss();
			gameController.setGameState(nextState);
		}
		
		@Override
		public void onClick(View v) {
			keepGoing();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			keepGoing();
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			keepGoing();
		}
	}
}
