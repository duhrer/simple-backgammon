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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean fullScreen = preferences.getBoolean(Constants.FULL_SCREEN_PREF,false);
		if (fullScreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		super.onCreate(savedInstanceState);

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
		startActivityIfNeeded(intent,SimpleBackgammon.ACTIVITY_CODE);
		
		// FIXME:  We need to actually exit if someone calls an exit from a SimpleBackgammon activity.
	}

}
