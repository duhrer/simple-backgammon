package com.anthonyatkins.simplebackgammon.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.activity.SimpleBackgammon;
import com.anthonyatkins.simplebackgammon.model.Dugout;


public class DugoutView extends SimpleSlotView{
	private static final int PIECES_PER_ROW = 3;
	private static final int MARGIN = 2;
	private Dugout dugout;
	private Palette theme;
	
	public DugoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.dugout = new Dugout();
		this.theme = new DefaultPalette();
	}

	public DugoutView(Context context, Dugout dugout, Palette theme) {
		super(context, dugout, theme);
		this.dugout = dugout;
		this.theme = theme;
	}
	
	public void draw(Canvas c) {
		int margin = 2;
		int pieceWidth = (getMeasuredWidth()/3) - margin;
		int pieceHeight = (getMeasuredHeight()/5) - margin;

		Paint piecePaint = null;
		if (dugout.color == Constants.BLACK) { 
			piecePaint=theme.blackPieceFillPaint;
		}
		else { 
			c.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);
			piecePaint=theme.whitePieceFillPaint;
		}
		
		if (this.isClickable()) {
			c.drawRect(0,0,this.getMeasuredWidth(),this.getMeasuredHeight(), theme.dugoutHighlightPaint);
		}
		
		// draw pieces in rows of five from top to bottom
		for (int a=0; a < dugout.pieces.size(); a++) {
			int row = (int) Math.floor(a/DugoutView.PIECES_PER_ROW);
			int column = a % DugoutView.PIECES_PER_ROW;
			int x = margin + (column * (pieceWidth+margin));
			int y = margin + (row * (pieceHeight+margin));
			c.drawRect(x, y, x+pieceWidth,y+pieceHeight, piecePaint);
		}
	}
}
