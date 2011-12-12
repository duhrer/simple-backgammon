package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.model.Pit;
import com.anthonyatkins.simplebackgammon.model.SimpleDice;

public class PitView extends ViewGroup{
	private SlotBankView topSlotBankView;
	private SlotBankView bottomSlotBankView; 
	private DiceView diceView;
	
	public PitView(Context context, Pit pit, int color, Palette theme) {
		super(context);
		
		setClipChildren(false);
		setClipToPadding(false);
		setTopSlotBankView(new SlotBankView(context,pit.topSlotBank));
		addView(getTopSlotBankView());
		setBottomSlotBankView(new SlotBankView(context,pit.bottomSlotBank));
		addView(getBottomSlotBankView());
		setDiceView(new DiceView(context, new SimpleDice(color), pit.getGame(), color, theme));
		addView(getDiceView());
		
		this.setBackgroundColor(theme.pitPaint.getColor());
	}

	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		
		int slotBankWidth = widthMeasureSpec;
		int slotBankHeight = Math.round((float) (heightMeasureSpec * 0.5));
		int diceHeight = Math.round((float) (heightMeasureSpec * 0.2));
		getTopSlotBankView().setMinimumWidth(slotBankWidth);
		getTopSlotBankView().setMinimumHeight(slotBankHeight);
		getTopSlotBankView().measure(slotBankWidth, slotBankHeight);
		getDiceView().setMinimumWidth(widthMeasureSpec);
		getDiceView().setMinimumHeight(diceHeight);
		getDiceView().measure(widthMeasureSpec, diceHeight);
		getBottomSlotBankView().setMinimumWidth(slotBankWidth);
		getBottomSlotBankView().setMinimumHeight(slotBankHeight);
		getBottomSlotBankView().measure(slotBankWidth, slotBankHeight);
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int y = 0;
		getTopSlotBankView().layout(0, 0, getTopSlotBankView().getMeasuredWidth(), getTopSlotBankView().getMeasuredHeight());
		getTopSlotBankView().bringToFront();
		y += getTopSlotBankView().getMeasuredHeight();
		getBottomSlotBankView().layout(0, y, getBottomSlotBankView().getMeasuredWidth(), y + getBottomSlotBankView().getMeasuredHeight());
		getBottomSlotBankView().bringToFront();
		y = (int) Math.round(getMeasuredHeight() * 0.4);
		getDiceView().layout(0, y, getDiceView().getMeasuredWidth(), y + getDiceView().getMeasuredHeight());
	}

	public DiceView getDiceView() {
		return diceView;
	}

	public void setDiceView(DiceView diceView) {
		this.diceView = diceView;
	}

	public SlotBankView getBottomSlotBankView() {
		return bottomSlotBankView;
	}

	public void setBottomSlotBankView(SlotBankView bottomSlotBankView) {
		this.bottomSlotBankView = bottomSlotBankView;
	}

	public SlotBankView getTopSlotBankView() {
		return topSlotBankView;
	}

	public void setTopSlotBankView(SlotBankView topSlotBankView) {
		this.topSlotBankView = topSlotBankView;
	}
}