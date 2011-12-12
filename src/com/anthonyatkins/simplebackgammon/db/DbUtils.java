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
import com.anthonyatkins.simplebackgammon.model.TurnMove;

public class DbUtils {
	public static long getLastUnfinishedGame(SQLiteDatabase db) {
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
		int points = cursor.getInt(cursor.getColumnIndex(Game.POINTS));
		game.setPoints(points);
		
		boolean finished = cursor.getInt(cursor.getColumnIndex(Game.FINISHED)) == 1 ? true : false;
		game.setFinished(finished);
		
		int startingColor = cursor.getInt(cursor.getColumnIndex(Game.STARTING_COLOR));
		game.setStartingColor(startingColor);

		// get the list of turns 
		List<Turn> turns = getTurnsByGame(game,db);
		
		// get the list of moves for each turn and replay each move in order
		if (turns != null && turns.size() > 0) {
			for (Turn turn : turns) {
				List<Move> moves = getMovesByTurn(turn, game, db);
				if (moves != null) {
					for (Move move: moves) {
						game.makeMove(move);
					}
				}
			}
		}
		else {
			game.newTurn(startingColor == Constants.BLACK ? game.getBlackPlayer() : game.getWhitePlayer());
		}
		
		
		if (finished) { 
			game.setState(Game.GAME_OVER);
		}
		else {
			game.setState(Game.MOVE_PICK_SOURCE);
		}
	}
	
	public static Match getMatchById(long matchId, SQLiteDatabase db) {
		if (db.isOpen()) {
			Cursor cursor = db.query(Match.TABLE_NAME, Match.COLUMNS,null,null,null,null,null,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int blackPlayerId = cursor.getInt(cursor.getColumnIndex(Match.BLACK_PLAYER));
				Player blackPlayer = getPlayerById(blackPlayerId, Constants.BLACK, db);
				int whitePlayerId = cursor.getInt(cursor.getColumnIndex(Match.WHITE_PLAYER));
				Player whitePlayer = getPlayerById(whitePlayerId, Constants.WHITE, db);

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

	public static List<Move> getMovesByTurn(Turn turn, Game game, SQLiteDatabase db) {
		List<Move> moves = new ArrayList<Move>();

		if (db.isOpen()) {
			Cursor cursor = db.query(Move.TABLE_NAME, Move.COLUMNS,Move.TURN + "=" + turn.getId(),null,null,null,"order by " + Move.CREATED,null);
			if (cursor.getCount() > 0) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					int startPos = cursor.getInt(cursor.getColumnIndex(Move.START_SLOT));
					Slot startSlot = game.getBoard().getPlaySlots().get(startPos);
					int endPos = cursor.getInt(cursor.getColumnIndex(Move.END_SLOT));
					Slot endSlot = game.getBoard().getPlaySlots().get(endPos);

					int dieValue = cursor.getInt(cursor.getColumnIndex(Move.DIE));
					SimpleDie die = new SimpleDie(dieValue,turn.getColor());
					
					TurnMove move = new TurnMove(startSlot, endSlot, die, turn);
					int createdTimestamp = cursor.getInt(cursor.getColumnIndex(Move.CREATED));
					Date created = new Date((long) createdTimestamp);
					move.setCreated(created);
				}
			}
		}

		return moves;
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
		
		if (turn.getId() == -1) {
			long turnId = db.insert(Turn.TABLE_NAME, null, values);
			turn.setId(turnId);
		}
		else {
			db.update(Turn.TABLE_NAME, values, Turn._ID + "=" + turn.getId(), null);
		}
		
		for (Move move : turn.getMoves()) {
			if (move instanceof TurnMove) {
				saveMove((TurnMove) move, db);
			}
		}
		
		return turn.getId();
	}
	
	public static int deleteTurn(Turn turn, SQLiteDatabase db) {
		for (Move move : turn.getMoves()) {
			deleteMove(move, db);
		}
		return db.delete(Turn.TABLE_NAME, Turn._ID + "=" + turn.getId(), null);
	}
	
	public static long saveMove(TurnMove move, SQLiteDatabase db) {
		ContentValues values = new ContentValues();

		values.put(Move.TURN, move.getTurn().getId());
		values.put(Move.PLAYER, move.getPlayer().getId());
		values.put(Move.DIE, move.getDie().getValue());
		values.put(Move.START_SLOT, move.getStartSlot().getPosition());
		values.put(Move.END_SLOT, move.getEndSlot().getPosition());
		values.put(Move.CREATED, move.getCreated().getTime());
		
		if (move.getId() == -1) {
			long moveId = db.insert(Move.TABLE_NAME, null, values);
			move.setId(moveId);
			return moveId;
		}
		else {
			db.update(Move.TABLE_NAME, values, Move._ID + "=" + move.getId(), null);
			return move.getId();
		}
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
		values.put(Game.POINTS, game.getPoints());
		values.put(Game.FINISHED, game.isFinished() ? 1 : 0);
		values.put(Game.CREATED, game.getCreated().getTime());
		
		if (game.getId() == -1) {
			// This is a new game
			long gameId = db.insert(Game.TABLE_NAME, null, values );
			game.setId(gameId);
		}
		else {
			// This is an existing game
			db.update(Game.TABLE_NAME, values, Game._ID + "=" + game.getId(), null);
		}
		
		for (Turn turn : game.getGameLog()) {
			saveTurn(turn, db);
		}
		
		return game.getId();
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
	
	public static List<Turn> getTurnsByGame(Game game, SQLiteDatabase db) {
		List<Turn> turns = new ArrayList<Turn>();

		if (db.isOpen()) {
			Cursor cursor = db.query(Turn.TABLE_NAME, Turn.COLUMNS,Turn.GAME + "=" + game.getId(),null,null,null,Move.CREATED + " desc",null);
			if (cursor.getCount() > 0) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					int playerId = cursor.getInt(cursor.getColumnIndex(Turn.PLAYER));
					int color = cursor.getInt(cursor.getColumnIndex(Turn.COLOR));
					int d1Value = cursor.getInt(cursor.getColumnIndex(Turn.DIE_ONE));
					int d2Value = cursor.getInt(cursor.getColumnIndex(Turn.DIE_TWO));
					Player player = getPlayerById(playerId, color, db);
					
					SimpleDice dice = new SimpleDice(color);
					dice.add(new SimpleDie(d1Value,color));
					dice.add(new SimpleDie(d2Value,color));
					new Turn(player, game, color);
				}
			}
		}

		
		return turns;
	}

	public static Player getPlayerById(long playerId, int color, SQLiteDatabase db) {
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


