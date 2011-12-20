package com.anthonyatkins.simplebackgammon.db;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.exception.InvalidMoveException;
import com.anthonyatkins.simplebackgammon.model.Game;
import com.anthonyatkins.simplebackgammon.model.Match;
import com.anthonyatkins.simplebackgammon.model.Move;
import com.anthonyatkins.simplebackgammon.model.Player;
import com.anthonyatkins.simplebackgammon.model.SimpleDice;
import com.anthonyatkins.simplebackgammon.model.SimpleDie;
import com.anthonyatkins.simplebackgammon.model.Slot;
import com.anthonyatkins.simplebackgammon.model.Turn;
import com.anthonyatkins.simplebackgammon.model.TurnMove;

public class DbUtils {
	public static long getLastUnfinishedGameId(SQLiteDatabase db) {
		if (db.isOpen()) {
			Cursor cursor = db.query(Game.TABLE_NAME, Game.COLUMNS,Game.FINISHED + "=0",null,null,null,Game.CREATED + " desc","1");
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				long id = cursor.getLong(cursor.getColumnIndex(Game._ID));
				return id;
			}
		}

		return -1;
	}
	
	public static Game getGameById(Match match, long gameId, SQLiteDatabase db) {
		Game game = new Game(match,0);

		if (db.isOpen()) {
			Cursor cursor = db.query(Game.TABLE_NAME, Game.COLUMNS,Game._ID + "=" + gameId,null,null,null,null,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				
				loadGameFromCursor(db, game, cursor);
			}
		}
		
		return game;
	}

	public static void loadGameFromCursor(SQLiteDatabase db, Game game, Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(Game._ID));
		game.setId(id);
		
		int points = cursor.getInt(cursor.getColumnIndex(Game.POINTS));
		game.setPoints(points);
		
		boolean finished = cursor.getInt(cursor.getColumnIndex(Game.FINISHED)) == 1 ? true : false;
		game.setFinished(finished);
		
		int startingColor = cursor.getInt(cursor.getColumnIndex(Game.STARTING_COLOR));
		game.setStartingColor(startingColor);

		// get the list of turns 
		loadTurnsByGame(game,db);
		
		// if there were no turns saved, this is a corrupt game, and we'll have to throw an exception
		if (game.getGameLog().size() == 0) {
			Log.e("DbUtils.loadGameFromCursor()", "There were no turn saved for this game, adding a new turn to the game log to avoid a crash");
			new Turn(startingColor == Constants.BLACK ? game.getBlackPlayer() : game.getWhitePlayer(),game,startingColor);
		}
		
		int gameState = cursor.getInt(cursor.getColumnIndex(Game.GAME_STATE));
		game.setState(gameState);
	}
	
	public static Match getMatchById(long matchId, SQLiteDatabase db) {
		if (db.isOpen()) {
			Cursor cursor = db.query(Match.TABLE_NAME, Match.COLUMNS,Match._ID + "=" + matchId,null,null,null,null,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int blackPlayerId = cursor.getInt(cursor.getColumnIndex(Match.BLACK_PLAYER));
				Player blackPlayer = getPlayerById(blackPlayerId, db);
				int whitePlayerId = cursor.getInt(cursor.getColumnIndex(Match.WHITE_PLAYER));
				Player whitePlayer = getPlayerById(whitePlayerId, db);

				int pointsToFinish = cursor.getInt(cursor.getColumnIndex(Match.POINTS_TO_WIN));

				Match match = new Match(blackPlayer,whitePlayer,pointsToFinish);
				match.setId(matchId);
				
				boolean finished = cursor.getInt(cursor.getColumnIndex(Match.FINISHED)) == 1 ? true : false;
				match.setFinished(finished);
				
				loadMatchGames(match, db);
				return match;
			}
		}

		return null;
	}

	public static void loadMovesByTurn(Turn turn, Game game, SQLiteDatabase db) {
		if (db.isOpen()) {
			// get the list of moves for this turn and replay each move in order
			Cursor cursor = db.query(Move.TABLE_NAME, Move.COLUMNS,Move.TURN + "=" + turn.getId(),null,null,null,Move.CREATED,null);
			if (cursor.getCount() > 0) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					int startPos = cursor.getInt(cursor.getColumnIndex(Move.START_SLOT));
					Slot startSlot = game.getBoard().getPlaySlots().get(startPos);
					
					int endPos = cursor.getInt(cursor.getColumnIndex(Move.END_SLOT));
					Slot endSlot = game.getBoard().getPlaySlots().get(endPos);

					boolean pieceBumped = cursor.getInt(cursor.getColumnIndex(Move.PIECE_BUMPED)) == 1 ? true : false;

					int dieValue = cursor.getInt(cursor.getColumnIndex(Move.DIE));
					
					int createdTimestamp = cursor.getInt(cursor.getColumnIndex(Move.CREATED));
					Date created = new Date((long) createdTimestamp);
					
					// FIXME:  Get the first matching die from this turn and use it for the move
					for (SimpleDie die : turn.getDice()) {
						if (die.getValue() == dieValue && !die.isUsed()) {
							try {
								Move move = new Move(startSlot, endSlot, die, turn.getPlayer(), created);
								move.setPieceBumped(pieceBumped);
								turn.makeMove(move);
								break;
							} catch (InvalidMoveException e) {
								Log.e("loadGameFromCursor()", "Error replaying moves from stored game...", e);
							}
						}
					}
					
				}
			}
		}
	}
	
	public static long saveMatch(Match match, SQLiteDatabase db) {
		savePlayer(match.getBlackPlayer(),db);
		savePlayer(match.getWhitePlayer(),db);
		
		ContentValues values = new ContentValues();
		values.put(Match.BLACK_PLAYER, match.getBlackPlayer().getId());
		values.put(Match.WHITE_PLAYER, match.getWhitePlayer().getId());
		values.put(Match.POINTS_TO_WIN, match.getPointsToWin());
		values.put(Match.FINISHED, match.isFinished());
		values.put(Match.CREATED, match.getCreated().getTime());
		
		if (match.getId() == -1) {
			long matchId = db.insert(Match.TABLE_NAME, null, values);
			match.setId(matchId);
		}
		else {
			db.update(Match.TABLE_NAME, values, Match._ID + "=" + match.getId(), null);
		}
		
		for (Game game: match.getGames()) {
			saveGame(game,db);
		}
		
		return match.getId();
	}
	
	public static int deleteMatch(Match match, SQLiteDatabase db) {
		// Delete all games in this match
		for (Game game : match.getGames()) {
			deleteGame(game,db);
		}
		
		return db.delete(Match.TABLE_NAME, Match._ID + "=" + match.getId(), null);
	}
	
	public static long saveTurn(Turn turn, SQLiteDatabase db) {
		ContentValues values = new ContentValues();

		values.put(Turn.GAME, turn.getGame().getId());
		values.put(Turn.PLAYER, turn.getPlayer().getId());
		values.put(Turn.COLOR, turn.getColor());
		values.put(Turn.DIE_ONE, turn.getDice().get(0).getValue());
		values.put(Turn.DIE_TWO, turn.getDice().get(1).getValue());
		values.put(Turn.CREATED, turn.getCreated().getTime());
		values.put(Turn.START_SLOT, turn.getStartSlot() == null ? Slot.INVALID_POSITION : turn.getStartSlot().getPosition());
		
		if (turn.getId() == -1) {
			long turnId = db.insert(Turn.TABLE_NAME, null, values);
			turn.setId(turnId);
		}
		else {
			Cursor cursor = db.query(Turn.TABLE_NAME, Turn.COLUMNS, Turn._ID + "=" + turn.getId(), null, null, null, null);
			if (cursor.getCount() > 0) {
				values.put(Turn._ID, turn.getId());
				db.update(Turn.TABLE_NAME, values, Turn._ID + "=" + turn.getId(), null);
			}
			else {
				long turnId = db.insert(Turn.TABLE_NAME, null, values);
				turn.setId(turnId);
			}
		}
		
		for (Move move : turn.getMoves()) {
			if (move instanceof TurnMove) {
				saveMove((TurnMove) move, db);
			}
			else {
				saveMove(move, turn, db);
			}
		}
		
		return turn.getId();
	}

	public static int deleteTurn(Turn turn, SQLiteDatabase db) {
		db.delete(Move.TABLE_NAME, Move.TURN+"="+turn.getId(), null);
		return db.delete(Turn.TABLE_NAME, Turn._ID + "=" + turn.getId(), null);
	}
	
	private static long saveMove(Move move, Turn turn, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		
		values.put(Move.TURN, turn.getId());
		values.put(Move.PLAYER, move.getPlayer().getId());
		values.put(Move.DIE, move.getDie().getValue());
		values.put(Move.START_SLOT, move.getStartSlot().getPosition());
		values.put(Move.END_SLOT, move.getEndSlot().getPosition());
		values.put(Move.CREATED, move.getCreated().getTime());
		
		if (move.getId() != -1) {
			values.put(Move._ID, move.getId());
		}

		long moveId = db.insert(Move.TABLE_NAME, null, values);
		move.setId(moveId);
		
		return moveId;
	}
	
	public static long saveMove(TurnMove move, SQLiteDatabase db) {
		return saveMove(move, move.getTurn(),db);
	}
	
	public static int deleteMove(Move move, SQLiteDatabase db) {
		return deleteMove(move.getId(),db);
	}
	
	public static int deleteMove(long moveId, SQLiteDatabase db) {
		return db.delete(Move.TABLE_NAME, Move._ID + "=" + moveId, null);
	}
	
	public static long savePlayer(Player player, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(Player.NAME, player.getName());
		
		if (player.getId() == -1) {
			long playerId = db.insert(Player.TABLE_NAME, null, values);
			player.setId(playerId);
			return playerId;
		}
		else {
			db.update(Player.TABLE_NAME, values, Player._ID + "=" + player.getId(), null);
			return player.getId();
		}
	}
	
	public static int deletePlayer(Player player, SQLiteDatabase db) {
		return deletePlayer(player.getId(), db);
	}
	
	public static int deletePlayer(long playerId, SQLiteDatabase db) {
		return db.delete(Player.TABLE_NAME, Player._ID + "=" + playerId, null);
	}
	
	
	public static long saveGame(Game game, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(Game.MATCH, game.getMatch().getId());
		values.put(Game.BLACK_PLAYER, game.getBlackPlayer().getId());
		values.put(Game.WHITE_PLAYER, game.getWhitePlayer().getId());
		values.put(Game.STARTING_COLOR, game.getStartingColor());
		values.put(Game.GAME_STATE, game.getState());
		values.put(Game.POINTS, game.getPoints());
		values.put(Game.FINISHED, game.isFinished() ? 1 : 0);
		values.put(Game.CREATED, game.getCreated().getTime());
		
		if (game.getId() == -1) {
			// This is a new game
			long gameId = db.insert(Game.TABLE_NAME, null, values );
			game.setId(gameId);
		}
		else {
			// This is an existing game, see if it already exists in the database
			Cursor cursor = db.query(Game.TABLE_NAME, Game.COLUMNS, Game._ID + "=" + game.getId(), null, null, null, null);
			if (cursor.getCount() > 0) {
				values.put(Game._ID, game.getId());
				db.update(Game.TABLE_NAME, values, Game._ID + "=" + game.getId(), null);
				
				// We have to get rid of our existing move history since this is an update
				deleteTurnsByGame(game,db);
			}
			else {
				long gameId = db.insert(Game.TABLE_NAME, null, values );
				game.setId(gameId);
			}
		}
		
		for (Turn turn : game.getGameLog()) {
			saveTurn(turn, db);
		}
		
		return game.getId();
	}

	private static void deleteTurnsByGame(Game game, SQLiteDatabase db) {
		for (Turn turn : game.getGameLog()) {
			deleteTurn(turn,db);
		}
	}

	public static int deleteGame(Game game, SQLiteDatabase db) {
		for (Turn turn : game.getGameLog()) {
			deleteTurn(turn, db);
		}
		
		return db.delete(Game.TABLE_NAME, Game._ID + "=" + game.getId(),null);
	}
	
	
	public static void loadMatchGames(Match match, SQLiteDatabase db) {
		if (db.isOpen()) {
			Cursor cursor = db.query(Game.TABLE_NAME, Game.COLUMNS,Game.MATCH+ "=" + match.getId(),null,null,null,Game.CREATED + " desc",null);
			if (cursor.getCount() > 0) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					loadGameFromCursor(db, new Game(match,0), cursor);
				}
			}
		}
	}
	
	public static void loadTurnsByGame(Game game, SQLiteDatabase db) {
		game.getGameLog().clear();
		if (db.isOpen()) {
			Cursor cursor = db.query(Turn.TABLE_NAME, Turn.COLUMNS,Turn.GAME + "=" + game.getId(),null,null,null,Turn.CREATED,null);
			if (cursor.getCount() > 0) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					int turnId = cursor.getInt(cursor.getColumnIndex(Turn._ID));
					int playerId = cursor.getInt(cursor.getColumnIndex(Turn.PLAYER));
					int color = cursor.getInt(cursor.getColumnIndex(Turn.COLOR));
					int d1Value = cursor.getInt(cursor.getColumnIndex(Turn.DIE_ONE));
					int d2Value = cursor.getInt(cursor.getColumnIndex(Turn.DIE_TWO));
					int startSlot = cursor.getInt(cursor.getColumnIndex(Turn.START_SLOT));
					Date created = new Date(cursor.getInt(cursor.getColumnIndex(Turn.CREATED)));
					Player player = getPlayerById(playerId, db);
					
					SimpleDice dice = new SimpleDice(d1Value,d2Value,color);
					Turn newTurn = new Turn(player,game,color,created,dice);
					newTurn.setId(turnId);
					if (startSlot != Slot.INVALID_POSITION) newTurn.setStartSlot(startSlot);
					
					game.findAllPotentialMoves();

					loadMovesByTurn(newTurn, game, db);
				}
			}
		}
	}

	public static Player getPlayerById(long playerId, SQLiteDatabase db) {
		Player player = new Player();

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

	public static Match getMatchByGameId(long gameId, SQLiteDatabase db) {
		
		if (db.isOpen()) {
			Cursor cursor = db.query(Game.TABLE_NAME, Game.COLUMNS,Game._ID + "=" + gameId,null,null,null,null,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();	
				int matchId = cursor.getInt(cursor.getColumnIndex(Game.MATCH));
				return getMatchById(matchId, db);
			}
		}		

		return null;
	}
}


