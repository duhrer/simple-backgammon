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
	private CombinedBarView combinedBarView;
	private DiceView blackDiceView;
	private DiceView whiteDiceView;
	private List<AnimatedSlotView> playSlotViews = new ArrayList<AnimatedSlotView>();
		
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// initialize the board
	public BoardView(Context context, Board board, Palette theme) {
		super(context);
		this.context = context;
		this.board = board;
		this.theme = theme;
		
		leftPitView = new PitView(context, board.getLeftPit(), theme);
		addView(leftPitView);
		this.setWhiteDiceView(leftPitView.getDiceView());
		
		rightPitView = new PitView(context, board.getRightPit(), theme);
		addView(rightPitView);
		this.setBlackDiceView(rightPitView.getDiceView());

		this.setCombinedBarView(new CombinedBarView(context, new DugoutView(context,board.getWhiteOut(), theme), new BarView(context,board.getBar(),theme), new DugoutView(context,board.getBlackOut(),theme), theme));
		addView(getCombinedBarView());
		
	
		// Knit together the slots and their views and put the slots in the right pit
		for (int a=0; a<6; a++) {
			Slot slot = board.getPlaySlots().get(a);
			AnimatedSlotView slotView = new AnimatedSlotView(context,slot,theme);
			getPlaySlotViews().add(slotView);
			rightPitView.getBottomSlotBankView().addView(slotView);
		}
		for (int a=6; a<12; a++) {
			Slot slot = board.getPlaySlots().get(a);
			AnimatedSlotView slotView = new AnimatedSlotView(context,slot,theme);
			getPlaySlotViews().add(slotView);
			leftPitView.getBottomSlotBankView().addView(slotView);
		}
		for (int a=12; a<18; a++) {
			Slot slot = board.getPlaySlots().get(a);
			AnimatedSlotView slotView = new AnimatedSlotView(context,slot,theme);
			getPlaySlotViews().add(slotView);
			leftPitView.getTopSlotBankView().addView(slotView);
		}
		for (int a=18; a<24; a++) {
			Slot slot = board.getPlaySlots().get(a);
			AnimatedSlotView slotView = new AnimatedSlotView(context,slot,theme);
			getPlaySlotViews().add(slotView);
			rightPitView.getTopSlotBankView().addView(slotView);
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
		getCombinedBarView().setMinimumWidth(combinedBarWidth);
		getCombinedBarView().setMinimumHeight(heightMeasureSpec);
		getCombinedBarView().measure(combinedBarWidth, heightMeasureSpec);
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
		getCombinedBarView().layout(x,y, x+getCombinedBarView().getMeasuredWidth(), y+getCombinedBarView().getMeasuredHeight());
	}

	public CombinedBarView getCombinedBarView() {
		return combinedBarView;
	}

	public void setCombinedBarView(CombinedBarView combinedBarView) {
		this.combinedBarView = combinedBarView;
	}

	public DiceView getWhiteDiceView() {
		return whiteDiceView;
	}

	public void setWhiteDiceView(DiceView whiteDiceView) {
		this.whiteDiceView = whiteDiceView;
	}

	public DiceView getBlackDiceView() {
		return blackDiceView;
	}

	public void setBlackDiceView(DiceView blackDiceView) {
		this.blackDiceView = blackDiceView;
	}

	public List<AnimatedSlotView> getPlaySlotViews() {
		return playSlotViews;
	}

	public void setPlaySlotViews(List<AnimatedSlotView> playSlotViews) {
		this.playSlotViews = playSlotViews;
	}	
}
