package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.activity.SimpleBackgammon;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.GameDie;
import com.anthonyatkins.simplebackgammon.model.SimpleDie;

public class DieView extends View {
	public SimpleDie die;
	private int imageResource;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Palette theme = null;
	
	public DieView(Context context, SimpleDie die, Palette theme) {
		super(context);
		this.die = die;
		this.theme=theme;
		updateImage();
	}


	private void updateImage() {
		if (die.getColor() == Constants.BLACK) {
			switch (die.getValue()) {
				case 1:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bd1);
					break;
				case 2:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bd2);
					break;
				case 3:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bd3);
					break;
				case 4:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bd4);
					break;
				case 5:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bd5);
					break;
				case 6:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bd6);
					break;
				default:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bdu);
			}
		}
		else {
			switch (die.getValue()) {
				case 1:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wd1);
					break;
				case 2:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wd2);
					break;
				case 3:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wd3);
					break;
				case 4:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wd4);
					break;
				case 5:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wd5);
					break;
				case 6:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wd6);
					break;
				default:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wdu);
			}
		}
	}
	
	public void setAlpha(int a) {
		paint.setAlpha(a);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		int gameState = Game.UNINITIALIZED;
		int activePlayerColor = 0;
		if (die instanceof GameDie) {
			if (((GameDie) die).getGame() != null) {
				if (((GameDie) die).getGame().getActivePlayer() != null) {
					activePlayerColor = ((GameDie) die).getGame().getActivePlayer().color;
				}
				gameState = ((GameDie) die).getGame().getState();
			}
			if (gameState != Game.PICK_FIRST && activePlayerColor != die.getColor()) {
				this.setVisibility(INVISIBLE);
			}
			else {
				setVisibility(VISIBLE);
			}
		}
		
		if (die.isUsed()) { this.setAlpha(128);}
		else { setAlpha(255); }

		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),imageResource), null, new Rect(0,0,getMeasuredWidth(),getMeasuredHeight()), paint);

		if (die instanceof GameDie) {
			// Draw a simple "prohibit" across a die that has no moves
			if (!((GameDie) die).isUsed() && !((GameDie) die).hasMoves() && ((GameDie) die).getGame().getState() != Game.PICK_FIRST && ((GameDie) die).getGame().getState() != Game.GAME_OVER) {
				canvas.drawLine(0, 0, getMeasuredWidth(), getMeasuredHeight(), theme.dieBlockedPaint);
			}
		}
		
	}


	public void setDieValue(int value) {
		die.setValue(value);
		updateImage();
		invalidate();
	}
	
	public void roll() {
		die.roll();
		updateImage();
		invalidate();
	}


	public int getImageResource() {
		return imageResource;
	}


	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}
}
