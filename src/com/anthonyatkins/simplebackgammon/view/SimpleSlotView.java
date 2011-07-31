package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.activity.SimpleBackgammon;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.SimpleDie;
import com.anthonyatkins.simplebackgammon.model.Slot;

public class SimpleSlotView extends View implements Comparable<SimpleSlotView> {
	public Palette theme;
	public Slot slot;
	private int imageResource;
	
	public SimpleSlotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.slot = new Slot(Slot.DOWN,0);
		this.theme = new DefaultPalette();
	}

	public SimpleSlotView(Context context, Slot slot, Palette theme) {
		super(context);
		this.slot = slot;
		this.theme = theme;
	}
		
	public void onDraw(Canvas c) {
		int margin = 2;
		int pieceHeight = (getMeasuredHeight()/5) - margin;
		int pieceRadius = pieceHeight/2;
		int stackMinHeight = (int) Math.ceil(slot.pieces.size()/5);
		int centerX = getMeasuredWidth()/2;
		int textHeight = (int) Math.round(pieceHeight * 0.6);
		
		Paint pieceMultiplierPaint = new Paint(theme.pieceMultiplierPaint);
		pieceMultiplierPaint.setTextSize(textHeight);
		
		if (slot.getDirection() == Slot.DOWN) {
			c.rotate(180,getMeasuredWidth()/2,getMeasuredHeight()/2);
		}
		Path stalagmite = new Path();
		stalagmite.moveTo(0, getMeasuredHeight());
		stalagmite.lineTo(getMeasuredWidth()/2, 0);
		stalagmite.lineTo(getMeasuredWidth(), getMeasuredHeight());
		stalagmite.close();
		Paint slotPaint = null;
		if (slot.position % 2 > 0) {
			slotPaint = theme.oddSlotPaint;
		}
		else {
			slotPaint = theme.evenSlotPaint;
		}
		c.drawPath(stalagmite, slotPaint);
		if (getParent() instanceof View && ((View) getParent()).isClickable()){  
			switch (slot.getGame().getState()) {
				case Game.MOVE_PICK_SOURCE:
					// This slot is clickable because it's the starting point for a move
					c.drawPath(stalagmite, theme.slotHighlightPaint); 
					break;
				case Game.MOVE_PICK_DEST:
					// This slot is clickable because someone can move a piece here
					// we will use images for this shortly, but draw a red dot for now in the right place
					if (!slot.equals(slot.getGame().getSourceSlot())) {
						int potentialMovePosition = 5;
						if (slot.pieces.size() < 5) {
							potentialMovePosition = slot.pieces.size();
						}
						int centerY = (int) (getMeasuredHeight() - ((pieceHeight + margin) * potentialMovePosition) - pieceRadius);
						
						if (slot.moves != null && slot.moves.size() > 0) {
							Move incomingMove = null;
							for (Move move: slot.moves) {
								if (move.startSlot.equals(slot.getGame().getSourceSlot())) {
									incomingMove = move;
									break;
								}
							}
							
							// TODO:  display an image instead of a graphic
							if (incomingMove != null) {
//								c.drawCircle(centerX, centerY, pieceRadius, pieceMultiplierPaint);
//								c.drawText(String.valueOf(incomingMove.die.getValue()), centerX, centerY + textHeight/4, pieceMultiplierPaint);
								
								updateImage(incomingMove.die);
								if (imageResource != 0) {
									c.drawBitmap(BitmapFactory.decodeResource(getResources(),imageResource), null, new Rect(centerX-pieceRadius,centerY-pieceRadius,centerX+pieceRadius,centerY+pieceRadius), theme.potentialMovePaint);
								}
							}
						}
					}
					break;
			}
		}
		
		// draw my pieces
		if (slot.pieces.size() > 0) {
			Paint piecePaint = null;
			if (slot.pieces.first().color == Constants.BLACK) {
				piecePaint = theme.blackPieceFillPaint;
			}
			else {
				piecePaint = theme.whitePieceFillPaint;
			}
			
			double topStackedSlot = slot.pieces.size() % 5;
			double highestSlotPosition = 5;
			if (slot.pieces.size() < 5) { highestSlotPosition = slot.pieces.size(); }

			// If we're the selected slot, we draw one piece in the "sixth" position
			int pieceCount = slot.pieces.size();
			if (slot.equals(slot.getGame().getSourceSlot())){
				pieceCount --;
			}

			for (int a =0; a < 5; a++) {
				int centerY = (int) (getMeasuredHeight() - ((pieceHeight + margin) * a) - pieceRadius);
				if ((pieceCount-1) >= a) {
					int multiplier = 0;
					c.drawCircle(centerX, centerY, pieceRadius, piecePaint);
					if (stackMinHeight > 0) { multiplier = stackMinHeight; }
					if (topStackedSlot > a) { multiplier++; }
					
					if (multiplier > 1) {
						c.drawText(String.valueOf(multiplier), centerX, centerY + textHeight/4, pieceMultiplierPaint);
					}
				}
				
				if (slot.equals(slot.getGame().getSourceSlot()) && a+1 == highestSlotPosition){
					c.drawCircle(centerX, centerY, pieceRadius, theme.pieceSelectedPaint);
				}
			}
		}
		super.onDraw(c);
	}

	public int compareTo(SimpleSlotView anotherSlotView) {
		return (this.slot.position - ((SimpleSlotView) anotherSlotView).slot.position);
	}
	
	private void updateImage(SimpleDie die) {
		if (this.slot.getGame().getActivePlayer().color == Constants.BLACK) {
			switch (die.getValue()) {
				case 1:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bc1);
					break;
				case 2:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bc2);
					break;
				case 3:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bc3);
					break;
				case 4:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bc4);
					break;
				case 5:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bc5);
					break;
				case 6:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bc6);
					break;
				default:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.bcu);
			}
		}
		else {
			switch (die.getValue()) {
				case 1:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wc1);
					break;
				case 2:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wc2);
					break;
				case 3:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wc3);
					break;
				case 4:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wc4);
					break;
				case 5:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wc5);
					break;
				case 6:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wc6);
					break;
				default:
					setImageResource(com.anthonyatkins.simplebackgammon.R.drawable.wcu);
			}
		}
	}

	public int getImageResource() {
		return imageResource;
	}
		
	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}
}

