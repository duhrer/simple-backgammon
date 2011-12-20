package com.anthonyatkins.simplebackgammon.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.db.DbOpenHelper;
import com.anthonyatkins.simplebackgammon.db.DbUtils;

public class BackgammonStartupActivity extends Activity {
	private boolean isGameRunning = false;
	private static final String GAME_RUNNING = "gameRunning";
	public static final String START_COLOR_KEY = "startColor";
	public static final String GAME_ID_KEY = "gameId";
	public static final String BLACK_PLAYER_KEY = "blackPlayerId";	
	public static final String WHITE_PLAYER_KEY = "whitePlayerId";
	public static final String POINTS_TO_WIN_KEY = "pointsToWin";	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			isGameRunning = savedInstanceState.getBoolean(GAME_RUNNING,false);
		}
		
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean fullScreen = preferences.getBoolean(Constants.FULL_SCREEN_PREF,false);
		if (fullScreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		// Create the database if it doesn't exist and look for an existing game
		DbOpenHelper dbHelper = new DbOpenHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		long gameId = DbUtils.getLastUnfinishedGameId(db);
		db.close();

		
		// Start the main activity (with an existing game ID if there is one
		Intent intent = new Intent(this, SimpleBackgammon.class);
		Bundle bundle = new Bundle();
		
		// FIXME:  If there's no existing game in progress, prompt to create/pick players, create a match, etc.

		// FIXME:  Replace with data set on this screen
		bundle.putInt(BLACK_PLAYER_KEY, 1);
		bundle.putInt(WHITE_PLAYER_KEY, 2);
		bundle.putInt(POINTS_TO_WIN_KEY, 1);
		
		// FIXME:  Replace this with the starting player determined by rolling the dice
		bundle.putInt(START_COLOR_KEY, Color.WHITE);
		
		
		
		if (gameId != -1) { 
			bundle.putLong(GAME_ID_KEY, gameId);
		}

		intent.putExtras(bundle);
		
		if (!isGameRunning) { 
			this.isGameRunning = true;
			
			// FIXME:  pick the starting player and pass that along to the starting activity
			startActivityForResult(intent,SimpleBackgammon.ACTIVITY_CODE);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(GAME_RUNNING, isGameRunning);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SimpleBackgammon.ACTIVITY_CODE) {
			this.isGameRunning = false;
			
			if (resultCode == SimpleBackgammon.EXIT_RETURN_CODE) { 
				finish(); 
			}
			else if (resultCode == SimpleBackgammon.GAME_OVER_RETURN_CODE){
				// FIXME:  The game was conceded, tally it up as a victory for the right player
				// FIXME:  Here's where we should check to see if the match is finished and prompt to start a new match, etc.
			}
			
		}
	}
	
	

}
