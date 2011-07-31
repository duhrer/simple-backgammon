package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.anthonyatkins.simplebackgammon.model.SlotBank;

public class SlotBankView extends ViewGroup {
	private SlotBank slotBank;
	
	public SlotBankView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlotBankView(Context context, SlotBank slotBank) {
		super(context);
		this.slotBank = slotBank;
		this.setBackgroundColor(Color.TRANSPARENT);
		
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

		int slotWidth  = Math.round((float) (getMeasuredWidth() / 6));
		int slotHeight = getMeasuredHeight();

		for (int a=0; a < getChildCount(); a++) {
			View slot = getChildAt(a);
			slot.setMinimumWidth(slotWidth);
			slot.setMinimumHeight(slotHeight);
			slot.measure(slotWidth, slotHeight);
		}
	}	

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (slotBank.getDirection() == SlotBank.L_R) {
			for (int a=0; a < getChildCount(); a++) {
				View slot = getChildAt(a);
				slot.layout((a * slot.getMeasuredWidth()), 0,((a+1) * slot.getMeasuredWidth()), slot.getMeasuredHeight());
			}
		}
		else {
			for (int a=getChildCount()-1; a >= 0; a--) {
				View slot = getChildAt(a);
				int inversePos = (getChildCount()-1) - a;
				slot.layout((inversePos * slot.getMeasuredWidth()), 0,((inversePos+1) * slot.getMeasuredWidth()), slot.getMeasuredHeight());
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
	}	
	
	
}
