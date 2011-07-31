package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.anthonyatkins.simplebackgammon.R;

public class RotateableTextView extends TextView {
	private boolean rotate = false;
	
	public RotateableTextView(Context context) {
		super(context);
	}

	public RotateableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public RotateableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.RotateableTextView);
		rotate = a.getBoolean(R.styleable.RotateableTextView_rotate, false);
	}
	
	
	public void setRotated(boolean rotated) {
		this.rotate = rotated;
	}

	public boolean isRotated() {
		return rotate;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (rotate) 
			canvas.rotate(180,getMeasuredWidth()/2,getMeasuredHeight()/2);
		
		super.onDraw(canvas);
	}
}
