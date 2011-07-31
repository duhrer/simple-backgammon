package com.anthonyatkins.simplebackgammon.view;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.activity.SimpleBackgammon;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.Button;

public class RotateableButton extends Button {
	private GameView gameView;
	
	public RotateableButton(Context context, GameView gameView) {
		super(context);
		this.gameView = gameView;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		if (gameView.game.getActivePlayer().color == Constants.BLACK) {
			canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);
		}
		super.onDraw(canvas);
		canvas.restore();
	}
	
	

}
