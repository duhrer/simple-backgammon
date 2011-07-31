package com.anthonyatkins.simplebackgammon.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

public class DefaultPalette extends Palette {
	public DefaultPalette() {
		super();
		
		blackPieceFillPaint.setColor(Color.BLACK);
		blackPieceFillPaint.setStyle(Style.FILL);

		whitePieceFillPaint.setColor(Color.WHITE);
		whitePieceFillPaint.setStyle(Style.FILL);
		
		pieceMultiplierPaint.setColor(Color.RED);
		pieceMultiplierPaint.setTextAlign(Align.CENTER);
		pieceMultiplierPaint.setStyle(Style.STROKE);
		
		evenSlotPaint.setColor(Color.GRAY);
		oddSlotPaint.setColor(Color.GRAY);
		slotHighlightPaint.setColor(Color.RED);
		slotHighlightPaint.setStyle(Style.STROKE);
		slotHighlightPaint.setStrokeWidth(2);
		
		barPaint.setColor(Color.parseColor("#006600"));
		pitPaint.setColor(Color.GREEN);
		
		dialogTextPaint.setColor(Color.WHITE);
		dialogTextPaint.setColor(Color.WHITE);
		dialogTextPaint.setTextAlign(Paint.Align.CENTER);
		dialogTextPaint.setShadowLayer(5, 0, 0, Color.BLACK);
		
		dugoutHighlightPaint.setColor(Color.YELLOW);
		dugoutHighlightPaint.setStyle(Style.STROKE);
		
		dieBlockedPaint.setColor(Color.RED);
		dieBlockedPaint.setStyle(Style.STROKE);
		dieBlockedPaint.setStrokeWidth(4);
		
		pieceSelectedPaint.setColor(Color.RED);
		pieceSelectedPaint.setStyle(Style.STROKE);
		pieceSelectedPaint.setStrokeWidth(2);
		
		potentialMovePaint.setColor(Color.BLACK);
		potentialMovePaint.setAlpha(127);
	}
}
