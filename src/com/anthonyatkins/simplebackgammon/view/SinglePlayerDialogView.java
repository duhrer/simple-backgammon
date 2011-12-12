package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.model.Dialog;

public class SinglePlayerDialogView extends View{
	public Dialog dialog = new Dialog();
	private GameView gameView;
	private Palette palette;
	
	public SinglePlayerDialogView(Context context, GameView gameView, Palette palette) {
		super(context);
		this.dialog = dialog;
		this.gameView = gameView;
		this.palette = palette;
		this.setBackgroundColor(Color.TRANSPARENT);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		if (gameView.getGame().getCurrentTurn().getColor() == Constants.BLACK) {
			canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);
		}

		
		int textSize = (int) (getMeasuredWidth()*2/dialog.getMessage().length());
		int x = getMeasuredWidth()/2;
		int y = getMeasuredHeight()/2;

		palette.dialogTextPaint.setTextSize(textSize);
		

		super.onDraw(canvas);
		if (dialog.getMessage() != null) {
			canvas.drawText(dialog.getMessage(), x, y, palette.dialogTextPaint);
		}

		canvas.restore();
	}


	public void setMessage(String message) {
		dialog.setMessage(message);
	}
}
