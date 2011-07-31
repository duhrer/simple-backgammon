package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.anthonyatkins.simplebackgammon.R;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Slot;

public class PointerView extends View {
	private Slot slot;
	private TranslateAnimation downArrowAnimation;
	private TranslateAnimation upArrowAnimation;
	private int imageResource;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public PointerView(Context context, Slot slot) {
		super(context);
		this.slot = slot;
		setVisibility(VISIBLE);
		setImageResource(R.drawable.blue_arrow);

        int downArrowStartY = 0;
        int downArrowEndY = 25;
        
        int upArrowStartY = 25;
        int upArrowEndY = 0;

        downArrowAnimation = new TranslateAnimation(0, 0, downArrowStartY, downArrowEndY);
        downArrowAnimation.setDuration(1000);
        downArrowAnimation.setRepeatCount(Animation.INFINITE);
        
        upArrowAnimation = new TranslateAnimation(0, 0, upArrowStartY, upArrowEndY);
        upArrowAnimation.setDuration(1000);
        upArrowAnimation.setRepeatCount(Animation.INFINITE);
        
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (slot.getDirection() == Slot.DOWN) {
			canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);
		}
		
		
		setVisibility(VISIBLE);
		if (slot.equals(slot.getGame().getSourceSlot())) {
			setImageResource(R.drawable.red_arrow);
//			setAnimation(null);
		}
		else if (slot.equals(slot.getGame().getDestSlot())) {
			setImageResource(R.drawable.blue_arrow);
//			setAnimation(null);
		}
		else if (getParent() instanceof View && ((View) getParent()).isClickable()) {
			if (slot.getGame().getState() == Game.MOVE_PICK_SOURCE) {
				setImageResource(R.drawable.red_arrow);
				setAnimation(upArrowAnimation);
				upArrowAnimation.startNow();
			}
			if (slot.getGame().getState() == Game.MOVE_PICK_DEST) {
				setImageResource(R.drawable.blue_arrow);
				setAnimation(downArrowAnimation);
				downArrowAnimation.startNow();
			}
		}
		else {
//			setAnimation(null);
//			setVisibility(INVISIBLE);
		}

		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),imageResource), null, new Rect(0,0,getMeasuredWidth(),getMeasuredHeight()), paint);
	}

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}
}
