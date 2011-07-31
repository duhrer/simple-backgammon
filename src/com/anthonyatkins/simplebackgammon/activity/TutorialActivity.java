package com.anthonyatkins.simplebackgammon.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.R;

public class TutorialActivity extends Activity {
	public final static int MENU_HELP = 1;
	public final static int MENU_GAME = 2;
	public final static int MENU_EXIT = 99;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	requestWindowFeature(Window.FEATURE_NO_TITLE);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean fullScreen = preferences.getBoolean(Constants.FULL_SCREEN_PREF,false);
		if (fullScreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

        setContentView(R.layout.tutorial);
        registerForContextMenu(findViewById(R.id.tutorial));
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_HELP, 1, R.string.menu_help_back);
		menu.add(0, MENU_GAME, 2, R.string.menu_game);
		menu.add(0, MENU_EXIT, 3, R.string.menu_exit);
		return super.onCreateOptionsMenu(menu);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, MENU_HELP, 1, R.string.menu_help_back);
		menu.add(0, MENU_GAME, 2, R.string.menu_game);
		menu.add(0, MENU_EXIT, 3, R.string.menu_exit);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_HELP:
			startActivity(new Intent(this, HelpActivity.class));
			break;
		case MENU_GAME:
			startActivity(new Intent(this, SimpleBackgammon.class));
			break;
		case MENU_EXIT:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
