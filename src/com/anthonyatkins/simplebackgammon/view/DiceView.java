package com.anthonyatkins.simplebackgammon.view;

import java.util.Iterator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.SimpleDice;
import com.anthonyatkins.simplebackgammon.model.SimpleDie;

public class DiceView extends ViewGroup{
	private SimpleDice dice;
	private final Context context; 
	private Palette theme;
	private final Game game;
	
	public DiceView(Context context, SimpleDice dice, Game game, Palette theme) {
		super(context);
		this.context = context;
		this.dice = dice;
		this.theme = theme;
		this.game = game;
		
		// Add child views for all the dice in the set
		Iterator<SimpleDie> dieIterator = dice.iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			addView(new DieView(context, die, game, theme));
		}
	}	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int dieSide = widthMeasureSpec / (getChildCount()+1);
		if (dieSide > heightMeasureSpec) { dieSide = heightMeasureSpec; }
		
		for (int a=0; a<getChildCount(); a++) {
			View child = getChildAt(a);
			child.setMinimumWidth(dieSide);
			child.setMinimumHeight(dieSide);
			child.measure(dieSide, dieSide);
		}
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (getChildCount() > 0) {
			
			int diceWidth = getChildAt(0).getMeasuredWidth();
			int diceHeight = getChildAt(0).getMeasuredWidth();
			int totalWidth = diceWidth * (getChildCount()+1);
			int centerX = getMeasuredWidth()/2;
			int centerY = getMeasuredHeight()/2;
			int margin = diceWidth/(getChildCount()+1);
			int x = centerX - (totalWidth/2) + margin;
			int y = centerY - (diceHeight/2);
			
			for (int a=0; a<getChildCount(); a++) {
				View child = getChildAt(a);
				child.layout(x, y, x+child.getMeasuredWidth(), y+child.getMeasuredHeight());
				x+=child.getMeasuredWidth() + margin;
			}
		}
	}
	
	public void roll() {
		removeAllViews();
		dice.roll();
		Iterator<SimpleDie> dieIterator = dice.iterator();
		while (dieIterator.hasNext()) {
			SimpleDie die = dieIterator.next();
			addView(new DieView(context,die,game, theme));
		}
	}

	
	public void invalidate() {
		for (int a=0; a<getChildCount();a++) {
			getChildAt(a).invalidate();
		}
		super.invalidate();
	}

	public void setVisibility(int visibility) {
		for (int a=0; a<getChildCount();a++) {
			getChildAt(a).setVisibility(visibility);
		}

		super.setVisibility(visibility);
	}
}
