package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.model.Game;

public class GameView extends ViewGroup {
	private Game game;

	private BoardView boardView = null;
	private TwoPlayerDialog twoPlayerDialog = null;
	
	private Context context;
	
	private Palette theme = new DefaultPalette();
	
	public GameView(Context context, Game game) {
		super(context);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		this.context = context;
		this.setGame(game);
		this.setBoardView(new BoardView(context, game.getBoard(), theme));
		addView(getBoardView());
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		if (getBoardView() != null) {
			getBoardView().setMinimumWidth(getMeasuredWidth());
			getBoardView().setMinimumHeight(getMeasuredHeight());
			getBoardView().measure(getMeasuredWidth(), getMeasuredHeight());
		}
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (getBoardView() != null) {
			getBoardView().layout(0, 0, getBoardView().getMeasuredWidth(), getBoardView().getMeasuredHeight());
		}
	}

	public Palette getTheme() {
		return theme;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public BoardView getBoardView() {
		return boardView;
	}

	public void setBoardView(BoardView boardView) {
		this.boardView = boardView;
	}
}
