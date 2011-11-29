package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.anthonyatkins.simplebackgammon.model.Dialog;

public class TwoPlayerDialogView extends View {
	private Palette palette;
	private TextView reversedView;
	private TextView normalView;
	private Dialog dialog;
	
	public void setMessage(String message) {
		reversedView.setText(message);
		normalView.setText(message);
	}

	public TwoPlayerDialogView(Context context, Dialog dialog, Palette palette) {
		super(context);
		this.setDialog(dialog);
		this.palette = palette;
		this.setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int textSize = (int) (getMeasuredWidth()*2/getDialog().getMessage().length());
		int x = getMeasuredWidth()/2;
		int y = getMeasuredHeight() - textSize;
	
		palette.dialogTextPaint.setTextSize(textSize);
		
		super.onDraw(canvas);
		if (getDialog().getMessage() != null) {
			canvas.drawText(getDialog().getMessage(), x, y, palette.dialogTextPaint);
			
			canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);
			canvas.drawText(getDialog().getMessage(), x, y, palette.dialogTextPaint);
		}
	}

	public Dialog getDialog() {
		return dialog;
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}
}
