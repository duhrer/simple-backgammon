package com.anthonyatkins.simplebackgammon.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CombinedBarView extends ViewGroup {
	private DugoutView whiteOutView;
	private BarView barView;
	private DugoutView blackOutView;
	private Palette theme;
	
	public CombinedBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CombinedBarView(Context context, DugoutView whiteOutView, BarView barView, DugoutView blackOutView, Palette theme) {
		super(context);

		this.setBlackOutView(blackOutView);
		addView(this.getBlackOutView());
		this.setBarView(barView);
		addView(this.getBarView());
		this.setWhiteOutView(whiteOutView);
		addView(this.getWhiteOutView());		
		
		this.theme = theme;

		setBackgroundColor(theme.barPaint.getColor());
	}	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		
		if (getChildCount() > 0) {
			int childHeight = heightMeasureSpec/getChildCount();
			for (int a=0; a<getChildCount(); a++) {
				View child = getChildAt(a);
				child.setMinimumWidth(widthMeasureSpec);
				child.setMinimumHeight(childHeight);
				child.measure(widthMeasureSpec, childHeight);
			}
		}
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int y = 0;
		getBlackOutView().layout(0, y, getBlackOutView().getMeasuredWidth(), y+getBlackOutView().getMeasuredHeight());
		y += getBlackOutView().getMeasuredHeight();
		getBarView().layout(0, y, getBarView().getMeasuredWidth(), y+getBarView().getMeasuredHeight());
		y += getBarView().getMeasuredHeight();
		getWhiteOutView().layout(0, y, getWhiteOutView().getMeasuredWidth(), y+getWhiteOutView().getMeasuredHeight());
		y += getWhiteOutView().getMeasuredHeight();
	}

	public BarView getBarView() {
		return barView;
	}

	public void setBarView(BarView barView) {
		this.barView = barView;
	}

	public DugoutView getBlackOutView() {
		return blackOutView;
	}

	public void setBlackOutView(DugoutView blackOutView) {
		this.blackOutView = blackOutView;
	}

	public DugoutView getWhiteOutView() {
		return whiteOutView;
	}

	public void setWhiteOutView(DugoutView whiteOutView) {
		this.whiteOutView = whiteOutView;
	}

}
