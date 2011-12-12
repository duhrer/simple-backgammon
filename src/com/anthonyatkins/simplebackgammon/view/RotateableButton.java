package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.Button;

import com.anthonyatkins.simplebackgammon.Constants;

public class RotateableButton extends Button {
	private GameView gameView;
	
	public RotateableButton(Context context, GameView gameView) {
		super(context);
		this.gameView = gameView;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		if (gameView.getGame().getCurrentTurn().getColor() == Constants.BLACK) {
			canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);
		}
		super.onDraw(canvas);
		canvas.restore();
	}
	
	

}
