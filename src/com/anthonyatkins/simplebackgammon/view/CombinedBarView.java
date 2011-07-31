package com.anthonyatkins.simplebackgammon.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CombinedBarView extends ViewGroup {
	public DugoutView whiteOutView;
	public BarView barView;
	public DugoutView blackOutView;
	private Palette theme;
	
	public CombinedBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CombinedBarView(Context context, DugoutView whiteOutView, BarView barView, DugoutView blackOutView, Palette theme) {
		super(context);

		this.blackOutView=blackOutView;
		addView(this.blackOutView);
		this.barView=barView;
		addView(this.barView);
		this.whiteOutView=whiteOutView;
		addView(this.whiteOutView);		
		
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
		blackOutView.layout(0, y, blackOutView.getMeasuredWidth(), y+blackOutView.getMeasuredHeight());
		y += blackOutView.getMeasuredHeight();
		barView.layout(0, y, barView.getMeasuredWidth(), y+barView.getMeasuredHeight());
		y += barView.getMeasuredHeight();
		whiteOutView.layout(0, y, whiteOutView.getMeasuredWidth(), y+whiteOutView.getMeasuredHeight());
		y += whiteOutView.getMeasuredHeight();
	}

}
