package com.anthonyatkins.simplebackgammon.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.db.DbOpenHelper;
import com.anthonyatkins.simplebackgammon.db.DbUtils;
import com.anthonyatkins.simplebackgammon.model.Game;

public class StartupActivity extends Activity {
	private boolean isGameRunning = false;
	private static final String GAME_RUNNING = "gameRunning";	
	
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
		int gameId = DbUtils.getLastUnfinishedGame(db);
		db.close();

		// FIXME:  If there's no existing game in progress, prompt to create/pick players, create a match, etc.
		
		// Start the main activity (with an existing game ID if there is one
		Intent intent = new Intent(this, SimpleBackgammon.class);
		if (gameId != -1) { 
			intent.putExtra(Game._ID, gameId); 
		}
		
		if (!isGameRunning) { 
			this.isGameRunning = true;
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
			if (resultCode == SimpleBackgammon.EXIT_RETURN_CODE) { 
				finish(); 
			}
			
			// FIXME:  Here's where we should check to see if the match is finished and prompt to start a new match, etc.
		}
	}
	
	

}
