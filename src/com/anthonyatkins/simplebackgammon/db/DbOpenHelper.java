package com.anthonyatkins.simplebackgammon.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Match;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.Player;
import com.anthonyatkins.simplebackgammon.model.Turn;

public class DbOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "simplebackgammon";
	private Context context;

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	public DbOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create tables
		db.execSQL(Player.TABLE_CREATE);
		
		// Create two sample players
		Player player1 = new Player(Constants.BLACK);
		player1.setName("Player 1");
		DbUtils.savePlayer(player1, db);
		Player player2 = new Player(Constants.WHITE);
		player2.setName("Player 2");
		DbUtils.savePlayer(player2, db);
		
		db.execSQL(Match.TABLE_CREATE);
		db.execSQL(Game.TABLE_CREATE);
		db.execSQL(Turn.TABLE_CREATE);
		db.execSQL(Move.TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Delete everything and create tables
		Log.w(DbOpenHelper.class.toString(), "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");

		// delete tables
		deleteAllTables(db);
		
		onCreate(db);		
	}

	private void deleteAllTables(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + Player.TABLE_NAME);		
		db.execSQL("DROP TABLE IF EXISTS " + Match.TABLE_NAME);		
		db.execSQL("DROP TABLE IF EXISTS " + Game.TABLE_NAME);		
		db.execSQL("DROP TABLE IF EXISTS " + Turn.TABLE_NAME);		
		db.execSQL("DROP TABLE IF EXISTS " + Move.TABLE_NAME);		
	}
}
