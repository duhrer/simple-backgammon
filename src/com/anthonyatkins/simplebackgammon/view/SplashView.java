package com.anthonyatkins.simplebackgammon.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;

public class SplashView extends ViewGroup {
	private Button newGameButton;
	private Button continueGameButton;
	
	
	public SplashView(Context context, GameView gameView) {
		super(context);
		
		newGameButton = new Button(context);
		newGameButton.setText("New Game");
		
		this.addView(newGameButton);
		
		if (null == null) {
			continueGameButton = new Button(context);
			continueGameButton.setText("Continue Game");
			this.addView(continueGameButton);
		}
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
	}

}
