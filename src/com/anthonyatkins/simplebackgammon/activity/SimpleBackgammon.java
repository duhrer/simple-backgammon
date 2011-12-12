package com.anthonyatkins.simplebackgammon.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.R;
import com.anthonyatkins.simplebackgammon.controller.GameController;
import com.anthonyatkins.simplebackgammon.db.DbOpenHelper;
import com.anthonyatkins.simplebackgammon.db.DbUtils;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Match;
import com.anthonyatkins.simplebackgammon.model.Player;
import com.anthonyatkins.simplebackgammon.view.GameView;

public class SimpleBackgammon extends Activity {
	public final static String BOARD_STATE = "BoardState";
	public final static String GAME_STATE = "GameState";
	public final static String ACTIVE_PLAYER_DICE_STATE = "ActivePlayerDiceState";
	public final static String INACTIVE_PLAYER_DICE_STATE = "InactivePlayerDiceState";
	
	public final static int MENU_NEW_GAME = 1;
	public final static int MENU_HELP = 2;
	private static final int MENU_PREFS = 3;
	public final static int MENU_EXIT = 99;
	public static final String NAMESPACE = "android";
	public static final int ACTIVITY_CODE = 111;
	
	public static final int EXIT_RETURN_CODE = -123;
	
	Match match;
	Game game;
	GameView gameView = null;
	GameController gameController = null;
	private DbOpenHelper dbHelper;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean fullScreen = preferences.getBoolean(Constants.FULL_SCREEN_PREF,false);
		if (fullScreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

    	super.onCreate(savedInstanceState);
    	    	
    	// FIXME: Read the first two active players from the database and make them Black and White in that order

    	this.dbHelper = new DbOpenHelper(this);
    	
    	Player blackPlayer = new Player("Black Player");
    	Player whitePlayer = new Player("White Player");
    	
    	Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			long gameId = bundle.getLong(Game._ID);
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			// Load the match for this game 
			this.match = DbUtils.getMatchByGameId(gameId,db);
			db.close();
			
			if (this.match == null) {
				this.match = new Match(blackPlayer,whitePlayer,1);
			}
			// Find the game with the right id
			this.game = match.getGameById(gameId);
			if (this.game == null) {
				
				int startColor = bundle.getInt(StartupActivity.START_COLOR_KEY,Constants.BLACK);
				
				this.game = new Game(match,startColor);
			}
		}
		else { 
			// This should never happen, if you start getting a lot of games with black as the default, that's why... :)

			// FIXME:  throw an error and die if the parent activity didn't tell us which player is going first
			this.match = new Match(blackPlayer,whitePlayer,1);
			this.game = new Game(match,Constants.BLACK);
		}
		
		this.gameView = new GameView(this,this.game);
		gameController = new GameController(gameView,this);
		gameController.setGameState(game.getState()); 
		
		setContentView(gameView);
    }

    
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		saveGameToDb();
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		saveGameToDb();
		
		// Store the game ID in the out bundle, we'll try to restore that if we can.
		outState.putLong(Game._ID, game.getId());
	}

	private void saveGameToDb() {
		// Save the match to the database
		SQLiteDatabase writeableDb = dbHelper.getWritableDatabase();
		DbUtils.saveMatch(match, writeableDb);
		writeableDb.close();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW_GAME, 0, R.string.menu_new_game);
		menu.add(0, MENU_HELP, 1, R.string.menu_help);
		menu.add(0, MENU_PREFS, 2, R.string.menu_preferences);
		menu.add(0, MENU_EXIT, 3, R.string.menu_exit);
		return super.onCreateOptionsMenu(menu);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, MENU_NEW_GAME, 0, R.string.menu_new_game);
		menu.add(0, MENU_HELP, 1, R.string.menu_help);
		menu.add(0, MENU_PREFS, 2, R.string.menu_preferences);
		menu.add(0, MENU_EXIT, 3, R.string.menu_exit);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW_GAME:
			// FIXME:  The old game needs to be forced to conceed and we need to kick the user back out to the start screen.
			gameController.setGameState(Game.STARTUP); 
			break;
		case MENU_HELP:
			Intent helpIntent = new Intent(this,HelpActivity.class);
			startActivity(helpIntent);
			break;
		case MENU_PREFS:
			Intent preferencesIntent = new Intent(this,PreferencesActivity.class);
			startActivityForResult(preferencesIntent,PreferencesActivity.EDIT_PREFERENCES);
			break;
		case MENU_EXIT:
			setResult(EXIT_RETURN_CODE);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// If there is a game in progress, the back button is used to "undo" moves, but only from three game states
		boolean undoWasHandled = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			undoWasHandled = handleUndo();
		}
		
		// This even didn't have anything to do with us, give everyone else a chance to react to the event.
		if (undoWasHandled == true) {
			return undoWasHandled;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private boolean handleUndo() {
		switch (game.getState()) {
			case Game.MOVE_PICK_DEST:
				gameController.clearSelectedSlots();

				gameController.setGameState(Game.MOVE_PICK_SOURCE);
				return true;
	
			case Game.MOVE_PICK_SOURCE:
				gameController.undoMove();
				gameController.setGameState(Game.MOVE_PICK_SOURCE);
				return true;
			
			case Game.NEW_TURN:
				gameController.undoMove();
				gameController.setGameState(Game.MOVE_PICK_SOURCE);
				return true;
		}
		return false;
	}
}