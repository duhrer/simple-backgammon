package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.model.Slot;

public class AnimatedSlotView extends ViewGroup {
	private Slot slot;
	private SimpleSlotView slotView;
	private PointerView pointerView;
	private Palette theme;
	
	public AnimatedSlotView(Context context, Slot slot, Palette theme) {
		super(context);

		this.slot = slot;
		this.theme = theme;
		this.slotView = new SimpleSlotView(context, slot, theme);
		this.setClipChildren(false);
		addView(slotView);
		
		this.pointerView = new PointerView(context, slot);
		// Disable the pointer for the time being.
//        addView(pointerView);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int arrowViewHeight = widthMeasureSpec;
		int slotViewHeight = (int) Math.round(heightMeasureSpec * 0.8);
		slotView.setMinimumWidth(widthMeasureSpec);
		slotView.setMinimumHeight(slotViewHeight);
		slotView.measure(widthMeasureSpec, slotViewHeight);
		pointerView.setMinimumWidth(widthMeasureSpec);
		pointerView.setMinimumHeight(arrowViewHeight);
		pointerView.measure(widthMeasureSpec, arrowViewHeight);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int slotY = 0;
		int arrowY = 0;
		int arrowPadding = getMeasuredHeight() - slotView.getMeasuredHeight();
		
		if (slot.getDirection() == Slot.UP) { slotY += arrowPadding; }
		else { arrowY += slotView.getMeasuredHeight(); }
		slotView.layout(0, slotY, slotView.getMeasuredWidth(), slotY + slotView.getMeasuredHeight());
		pointerView.layout(0, arrowY, pointerView.getMeasuredWidth(), arrowY+pointerView.getMeasuredHeight());
	}
}
