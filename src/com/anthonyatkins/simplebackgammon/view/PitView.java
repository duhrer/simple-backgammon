package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.model.Pit;

public class PitView extends ViewGroup{
	public SlotBankView topSlotBankView;
	public SlotBankView bottomSlotBankView; 
	DiceView diceView;
	private Pit pit;
	private Palette theme;
	
	public PitView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PitView(Context context, Pit pit, Palette theme) {
		super(context);
		this.pit = pit;
		this.theme = theme;
		
		setClipChildren(false);
		setClipToPadding(false);
		topSlotBankView = new SlotBankView(context,pit.topSlotBank);
		addView(topSlotBankView);
		bottomSlotBankView = new SlotBankView(context,pit.bottomSlotBank);
		addView(bottomSlotBankView);
		diceView = new DiceView(context, pit.dice,theme);
		addView(diceView);
		
		this.setBackgroundColor(theme.pitPaint.getColor());
	}

	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		
		int slotBankWidth = widthMeasureSpec;
		int slotBankHeight = Math.round((float) (heightMeasureSpec * 0.5));
		int diceHeight = Math.round((float) (heightMeasureSpec * 0.2));
		topSlotBankView.setMinimumWidth(slotBankWidth);
		topSlotBankView.setMinimumHeight(slotBankHeight);
		topSlotBankView.measure(slotBankWidth, slotBankHeight);
		diceView.setMinimumWidth(widthMeasureSpec);
		diceView.setMinimumHeight(diceHeight);
		diceView.measure(widthMeasureSpec, diceHeight);
		bottomSlotBankView.setMinimumWidth(slotBankWidth);
		bottomSlotBankView.setMinimumHeight(slotBankHeight);
		bottomSlotBankView.measure(slotBankWidth, slotBankHeight);
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int y = 0;
		topSlotBankView.layout(0, 0, topSlotBankView.getMeasuredWidth(), topSlotBankView.getMeasuredHeight());
		topSlotBankView.bringToFront();
		y += topSlotBankView.getMeasuredHeight();
		bottomSlotBankView.layout(0, y, bottomSlotBankView.getMeasuredWidth(), y + bottomSlotBankView.getMeasuredHeight());
		bottomSlotBankView.bringToFront();
		y = (int) Math.round(getMeasuredHeight() * 0.4);
		diceView.layout(0, y, diceView.getMeasuredWidth(), y + diceView.getMeasuredHeight());
	}
}