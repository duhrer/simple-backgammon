package com.anthonyatkins.simplebackgammon.view;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.activity.SimpleBackgammon;
import com.anthonyatkins.simplebackgammon.model.Bar;
import com.anthonyatkins.simplebackgammon.model.Piece;

public class BarView extends SimpleSlotView{
	private Bar bar;

	Context context;
	
	public BarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.bar = new Bar(null);
		this.theme = new DefaultPalette();
	}

	public BarView(Context context, Bar bar, Palette theme) {
		super(context, bar, theme);
		this.bar = bar;
	}

	public void onDraw(Canvas c) {
		int whitePieces = 0;
		int blackPieces = 0;
		int margin = 5;
		
		int pieceRadius = 0;
		if (getMeasuredWidth() < (getMeasuredHeight()/2)) { pieceRadius = getMeasuredWidth()/2; }
		else { pieceRadius = getMeasuredHeight()/4; }
		
		int textSize = (pieceRadius * 2);
		
		Iterator<Piece> pieceIterator = bar.pieces.iterator();
		while (pieceIterator.hasNext()) {
			Piece piece = pieceIterator.next();
			if (piece.color == Constants.BLACK) { blackPieces++; }
			else { whitePieces++; }
		}
		
		int centerX = (getMeasuredWidth()/2);
		int centerY = (getMeasuredHeight()/4);
		if (whitePieces > 0) {
			// If the player is stuck on the bar, draw the "piece selected" look for the piece(s) on the bar
			if (slot.equals(slot.getGame().getSourceSlot()) && slot.getGame().getActivePlayer().color==Constants.WHITE){
				c.drawCircle(centerX, centerY, pieceRadius, theme.pieceSelectedPaint);
			}
			else {
				c.drawCircle(centerX, centerY, pieceRadius, theme.whitePieceFillPaint);
			}
			
			if (whitePieces > 1) {
				c.drawText(String.valueOf(whitePieces), centerX, centerY + textSize/4, theme.pieceMultiplierPaint);
			}
		}
		if (blackPieces > 0) {
			c.rotate(180,getMeasuredWidth()/2,getMeasuredHeight()/2);
			// If the player is stuck on the bar, draw the "piece selected" look for the piece(s) on the bar
			if (slot.equals(slot.getGame().getSourceSlot()) && slot.getGame().getActivePlayer().color==Constants.BLACK){
				c.drawCircle(centerX, centerY, pieceRadius, theme.pieceSelectedPaint);
			}
			else {
				c.drawCircle(centerX, centerY, pieceRadius, theme.blackPieceFillPaint);
			}
			if (blackPieces > 1) {
				c.drawText(String.valueOf(blackPieces), centerX, centerY  + textSize/4 ,theme.pieceMultiplierPaint);
			}
			c.restore();
		}
	}
}
