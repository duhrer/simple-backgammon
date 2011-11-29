package com.anthonyatkins.simplebackgammon.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Match;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.Player;
import com.anthonyatkins.simplebackgammon.model.SimpleDice;
import com.anthonyatkins.simplebackgammon.model.SimpleDie;
import com.anthonyatkins.simplebackgammon.model.Slot;
import com.anthonyatkins.simplebackgammon.model.Turn;

public class DbUtils {

	public static int getLastUnfinishedGame(SQLiteDatabase db) {
		if (db.isOpen()) {
			Cursor cursor = db.query(Game.TABLE_NAME, Game.COLUMNS,Game.FINISHED + "=false",null,null,null,Game.DATE + " desc","limit 1");
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int id = cursor.getInt(cursor.getColumnIndex(Game._ID));
				return id;
			}
		}

		return -1;
	}
	
	public static Game getGameById(int gameId, SQLiteDatabase db) {
		Game game = new Game();

		if (db.isOpen()) {
			Cursor cursor = db.query(Game.TABLE_NAME, Game.COLUMNS,Game._ID + "=" + gameId,null,null,null,null,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int matchId = cursor.getInt(cursor.getColumnIndex(Game.MATCH));
				Match match = getMatchById(matchId,game, db);
				game.setMatch(match);
				
				int black_player = cursor.getInt(cursor.getColumnIndex(Game.BLACK_PLAYER));
				Player blackPlayer = getPlayerById(black_player, Constants.BLACK, game, db);
				game.setBlackPlayer(blackPlayer);

				int white_player = cursor.getInt(cursor.getColumnIndex(Game.WHITE_PLAYER));
				Player whitePlayer = getPlayerById(black_player, Constants.WHITE, game, db);
				game.setWhitePlayer(whitePlayer);

				int points = cursor.getInt(cursor.getColumnIndex(Game.POINTS));
				game.setPoints(points);
				
				boolean finished = cursor.getInt(cursor.getColumnIndex(Game.FINISHED)) == 1 ? true : false;
				game.setFinished(finished);
				
				// get the list of turns 
				List<Turn> turns = getTurnsByGame(gameId,game,db);
				
				// get the list of moves for each turn and replay each move in order
				Player activePlayer = null;
				if (turns != null && turns.size() > 0) {
					for (Turn turn : turns) {
						activePlayer = turn.getPlayer();
						List<Move> moves = getMovesByTurn(turn.getId(), game, db);
						if (moves != null) {
							for (Move move: moves) {
								game.makeMove(move);
							}
						}
						
						game.getGameLog().add(turn);
					}
					game.setState(Game.MOVE_PICK_SOURCE);
				}
				else {
					game.setState(Game.STARTUP);
				}
				
				// set the active player based on the last turn
				game.setActivePlayer(activePlayer);
			}
		}
		
		return game;
	}
	
	private static Match getMatchById(int matchId, Game game, SQLiteDatabase db) {
		Match match = new Match();
		
		match.setBlackPlayer(game.getBlackPlayer());
		match.setWhitePlayer(game.getWhitePlayer());
			
		
		if (db.isOpen()) {
			Cursor cursor = db.query(Match.TABLE_NAME, Match.COLUMNS,null,null,null,null,null,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				
				int numGames = cursor.getInt(cursor.getColumnIndex(Match.FINISHED));
				match.setNumGames(numGames);

				boolean finished = cursor.getInt(cursor.getColumnIndex(Match.FINISHED)) == 1 ? true : false;
				match.setFinished(finished);
			}
		}

		return match;
	}

	private static List<Move> getMovesByTurn(int turnId, Game game, SQLiteDatabase db) {
		List<Move> moves = new ArrayList<Move>();

		if (db.isOpen()) {
			Cursor cursor = db.query(Move.TABLE_NAME, Move.COLUMNS,Move.TURN + "=" + turnId,null,null,null,"order by " + Move.CREATED,null);
			if (cursor.getCount() > 0) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					int startPos = cursor.getInt(cursor.getColumnIndex(Move.START_SLOT));
					Slot startSlot = game.getBoard().getPlaySlots().get(startPos);
					int endPos = cursor.getInt(cursor.getColumnIndex(Move.END_SLOT));
					Slot endSlot = game.getBoard().getPlaySlots().get(endPos);

					int dieValue = cursor.getInt(cursor.getColumnIndex(Move.DIE));
					int dieColor = cursor.getInt(cursor.getColumnIndex(Move.COLOR));
					SimpleDie die = new SimpleDie(dieValue,dieColor);
					
					Move move = new Move(startSlot, endSlot, die);
					int createdTimestamp = cursor.getInt(cursor.getColumnIndex(Move.CREATED));
					Date created = new Date((long) createdTimestamp);
					move.setCreated(created);
					moves.add(move);
				}
			}
		}

		return moves;
	}
	
	private long saveGame(Game game, SQLiteDatabase db) {

		ContentValues values = new ContentValues();
		values.put(Game.MATCH, game.getMatch().getId());
		values.put(Game.BLACK_PLAYER, game.getBlackPlayer().getId());
		values.put(Game.WHITE_PLAYER, game.getWhitePlayer().getId());
		values.put(Game.POINTS, game.getPoints());
		values.put(Game.FINISHED, game.isFinished() ? 1 : 0);
		
		// FIXME:  We have to make the sure the date is set automatically based on the current time.
		
		if (game.getId() == -1) {
			// This is a new game
			return db.insert(Game.TABLE_NAME, null, values );
		}
		else {
			// This is an existing game
			db.update(Game.TABLE_NAME, values, Game._ID + "=" + game.getId(), null);
			return game.getId();
		}
	}

	private static List<Turn> getTurnsByGame(int gameId, Game game, SQLiteDatabase db) {
		List<Turn> turns = new ArrayList<Turn>();

		if (db.isOpen()) {
			Cursor cursor = db.query(Turn.TABLE_NAME, Turn.COLUMNS,Turn.GAME + "=" + gameId,null,null,null,"order by " + Move.CREATED,null);
			if (cursor.getCount() > 0) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					int turnId = cursor.getInt(cursor.getColumnIndex(Turn._ID));
					int playerId = cursor.getInt(cursor.getColumnIndex(Turn.PLAYER));
					int color = cursor.getInt(cursor.getColumnIndex(Turn.COLOR));
					int d1Value = cursor.getInt(cursor.getColumnIndex(Turn.DIE_ONE));
					int d2Value = cursor.getInt(cursor.getColumnIndex(Turn.DIE_TWO));
					int createdTimestamp = cursor.getInt(cursor.getColumnIndex(Turn.CREATED));
					Date created = new Date((long) createdTimestamp); 
					Player player = getPlayerById(playerId, color, game, db);
					
					SimpleDice dice = new SimpleDice(color);
					SimpleDie d1 = new SimpleDie(d1Value,color);
					dice.add(d1);
					SimpleDie d2 = new SimpleDie(d2Value,color);
					dice.add(d2);
					Turn turn = new Turn(player, dice);
					turn.setCreated(created);
					
					List<Move> moves = getMovesByTurn(turnId, game, db);
					turn.addMoves(moves);
				}
			}
		}

		
		return turns;
	}

	public static Player getPlayerById(int playerId, int color, Game game, SQLiteDatabase db) {
		Player player = new Player(color, game);

		if (db.isOpen()) {
			Cursor cursor = db.query(Player.TABLE_NAME, Player.COLUMNS,Player._ID + "=" + playerId,null,null,null,null,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();	
				String playerName = cursor.getString(cursor.getColumnIndex(Player.NAME));
				player.setName(playerName);
			}
		}
		
		return player;
	}
}


