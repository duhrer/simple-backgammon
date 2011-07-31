package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.model.Game;

public class GameView extends ViewGroup {
	public Game game;

	public BoardView boardView = null;
	public TwoPlayerDialog twoPlayerDialog = null;
	
	public Context context;
	
	private Palette theme = new DefaultPalette();
	
	public GameView(Context context, Game game) {
		super(context);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		this.context = context;
		this.game = game;
		this.boardView = new BoardView(context, game.board, theme);
		addView(boardView);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		if (boardView != null) {
			boardView.setMinimumWidth(getMeasuredWidth());
			boardView.setMinimumHeight(getMeasuredHeight());
			boardView.measure(getMeasuredWidth(), getMeasuredHeight());
		}
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (boardView != null) {
			boardView.layout(0, 0, boardView.getMeasuredWidth(), boardView.getMeasuredHeight());
		}
	}

	public Palette getTheme() {
		return theme;
	}
}
