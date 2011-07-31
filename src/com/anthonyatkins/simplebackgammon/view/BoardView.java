package com.anthonyatkins.simplebackgammon.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.model.Board;
import com.anthonyatkins.simplebackgammon.model.Slot;

public class BoardView extends ViewGroup{
	Context context; 
	private Board board;
	private PitView leftPitView;
	private PitView rightPitView;
	private Palette theme;
	public CombinedBarView combinedBarView;
	public DiceView blackDiceView;
	public DiceView whiteDiceView;
	public List<AnimatedSlotView> playSlotViews = new ArrayList<AnimatedSlotView>();
		
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// initialize the board
	public BoardView(Context context, Board board, Palette theme) {
		super(context);
		this.context = context;
		this.board = board;
		this.theme = theme;
		
		leftPitView = new PitView(context, board.leftPit, theme);
		addView(leftPitView);
		this.whiteDiceView = leftPitView.diceView;
		
		rightPitView = new PitView(context, board.rightPit, theme);
		addView(rightPitView);
		this.blackDiceView = rightPitView.diceView;

		this.combinedBarView = new CombinedBarView(context, new DugoutView(context,board.whiteOut, theme), new BarView(context,board.bar,theme), new DugoutView(context,board.blackOut,theme), theme);
		addView(combinedBarView);
		
	
		// Knit together the slots and their views and put the slots in the right pit
		for (int a=0; a<6; a++) {
			Slot slot = board.playSlots.get(a);
			AnimatedSlotView slotView = new AnimatedSlotView(context,slot,theme);
			playSlotViews.add(slotView);
			rightPitView.bottomSlotBankView.addView(slotView);
		}
		for (int a=6; a<12; a++) {
			Slot slot = board.playSlots.get(a);
			AnimatedSlotView slotView = new AnimatedSlotView(context,slot,theme);
			playSlotViews.add(slotView);
			leftPitView.bottomSlotBankView.addView(slotView);
		}
		for (int a=12; a<18; a++) {
			Slot slot = board.playSlots.get(a);
			AnimatedSlotView slotView = new AnimatedSlotView(context,slot,theme);
			playSlotViews.add(slotView);
			leftPitView.topSlotBankView.addView(slotView);
		}
		for (int a=18; a<24; a++) {
			Slot slot = board.playSlots.get(a);
			AnimatedSlotView slotView = new AnimatedSlotView(context,slot,theme);
			playSlotViews.add(slotView);
			rightPitView.topSlotBankView.addView(slotView);
		}

	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

		int pitWidth  = Math.round((float) (widthMeasureSpec * 0.46));
		int combinedBarWidth = Math.round((float) (widthMeasureSpec * 0.05));
		
		leftPitView.setMinimumWidth(pitWidth);
		leftPitView.setMinimumHeight(heightMeasureSpec);
		leftPitView.measure(pitWidth, heightMeasureSpec);
		rightPitView.setMinimumWidth(pitWidth);
		rightPitView.setMinimumHeight(heightMeasureSpec);
		rightPitView.measure(pitWidth, heightMeasureSpec);
		combinedBarView.setMinimumWidth(combinedBarWidth);
		combinedBarView.setMinimumHeight(heightMeasureSpec);
		combinedBarView.measure(combinedBarWidth, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int marginWidth = (int) Math.round(getMeasuredWidth() * 0.01);
		int x = 0;
		int y = 0;
		leftPitView.layout(x, y, leftPitView.getMeasuredWidth(), leftPitView.getMeasuredHeight());
		x+=leftPitView.getMeasuredWidth() + marginWidth;
		rightPitView.layout(x, y, x + rightPitView.getMeasuredWidth(), rightPitView.getMeasuredHeight());
		x+=rightPitView.getMeasuredWidth() + marginWidth;
		combinedBarView.layout(x,y, x+combinedBarView.getMeasuredWidth(), y+combinedBarView.getMeasuredHeight());
	}	
}
