package com.anthonyatkins.simplebackgammon.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.anthonyatkins.simplebackgammon.R;
import com.anthonyatkins.simplebackgammon.controller.GameController;
import com.anthonyatkins.simplebackgammon.model.Game;

public class TwoPlayerDialog extends Dialog {
	private int nextState = Game.PICK_FIRST;
	private GameController gameController;
	private TwoPlayerDialogView dialogView;
	
	
	public TwoPlayerDialog(Context context, GameController gameController, Palette theme) {
		super(context,R.style.my_dialog_theme);
		this.gameController = gameController;
		this.dialogView = new TwoPlayerDialogView(context, new com.anthonyatkins.simplebackgammon.model.Dialog(), theme);
		ContinueListener continueListener = new ContinueListener();
		dialogView.setOnClickListener(continueListener);
		setOnCancelListener(continueListener);
		this.setCancelable(true);
		this.setContentView(dialogView);
	}
	
	public void setMessage(String text) {
		dialogView.dialog.setMessage(text);
		dialogView.invalidate();
	}
	
	public void setNextState(int nextState) {
		this.nextState = nextState;
	}
	
	private class ContinueListener implements Dialog.OnClickListener, View.OnClickListener, Dialog.OnCancelListener {
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
