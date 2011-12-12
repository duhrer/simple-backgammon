package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.Moves;
import com.anthonyatkins.simplebackgammon.model.SimpleDie;
import com.anthonyatkins.simplebackgammon.model.Slot;

public class SimpleSlotView extends View implements Comparable<SimpleSlotView> {
	public Palette theme;
	public Slot slot;
	private int imageResource;
	
	public SimpleSlotView(Context context, Slot slot, Palette theme) {
		super(context);
		this.slot = slot;
		this.theme = theme;
	}
		
	public void onDraw(Canvas c) {
		int margin = 2;
		int pieceHeight = (getMeasuredHeight()/5) - margin;
		int pieceRadius = pieceHeight/2;
		int stackMinHeight = (int) Math.ceil(slot.getPieces().size()/5);
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
		if (slot.getPosition() % 2 > 0) {
			slotPaint = theme.oddSlotPaint;
		}
		else {
			slotPaint = theme.evenSlotPaint;
		}
		c.drawPath(stalagmite, slotPaint);

		Moves potentialMoves = slot.getGame().getCurrentTurn().getPotentialMoves();
		if (potentialMoves != null && potentialMoves.size() > 0) {
			switch (slot.getGame().getState()) {
				case Game.MOVE_PICK_SOURCE:
					// If the slot is the start point for potential moves, highlight it
					if (potentialMoves.getMovesForStartSlot(slot).size() > 0) c.drawPath(stalagmite, theme.slotHighlightPaint); 
					break;
				case Game.MOVE_PICK_DEST:
					// If we're not the start point, check to see if we're the end point.
					if (!isSelectedSlot()) {
						Moves movesForEndSlot = potentialMoves.getMovesForEndSlot(slot);
						if (movesForEndSlot.size() > 0 ) {
							int potentialMovePosition = 5;
							if (slot.getPieces().size() < 5) {
								potentialMovePosition = slot.getPieces().size();
							}
							int centerY = (int) (getMeasuredHeight() - ((pieceHeight + margin) * potentialMovePosition) - pieceRadius);
							
							Move incomingMove = null;
							for (Move move: movesForEndSlot) {
								if (isSelectedSlot(move.getStartSlot())) {
									incomingMove = move;
									break;
								}
							}
							
							if (incomingMove != null) {
								updateImage(incomingMove.getDie());
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
		if (slot.getPieces().size() > 0) {
			Paint piecePaint = null;
			if (slot.getPieces().first().color == Constants.BLACK) {
				piecePaint = theme.blackPieceFillPaint;
			}
			else {
				piecePaint = theme.whitePieceFillPaint;
			}
			
			double topStackedSlot = slot.getPieces().size() % 5;
			double highestSlotPosition = 5;
			if (slot.getPieces().size() < 5) { highestSlotPosition = slot.getPieces().size(); }

			// If we're the selected slot, we draw one piece in the "sixth" position
			int pieceCount = slot.getPieces().size();
			if (isSelectedSlot()){
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
				
				if (isSelectedSlot() && a+1 == highestSlotPosition){
					c.drawCircle(centerX, centerY, pieceRadius, theme.pieceSelectedPaint);
				}
			}
		}
		super.onDraw(c);
	}

	protected boolean isSelectedSlot() {
		return isSelectedSlot(this.slot);
	}
	
	protected boolean isSelectedSlot(Slot slot) {
		return slot.getGame().getCurrentTurn().getStartSlot() != null && slot.equals(slot.getGame().getCurrentTurn().getStartSlot());
	}

	public int compareTo(SimpleSlotView anotherSlotView) {
		return (this.slot.getPosition() - ((SimpleSlotView) anotherSlotView).slot.getPosition());
	}
	
	private void updateImage(SimpleDie die) {
		if (this.slot.getGame().getCurrentTurn().getColor() == Constants.BLACK) {
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

