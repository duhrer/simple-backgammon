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
			Cursor cursor = db.query(Game.TABLE_NAME, Game.COLUMNS,Game.FINISHED + "=false",null,null,null,Game.CREATED + " desc","limit 1");
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int id = cursor.getInt(cursor.getColumnIndex(Game._ID));
				return id;
			}
		}

		return -1;
	}
	
	public static Game getGameById(long gameId, SQLiteDatabase db) {
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
	
	private static Match getMatchById(long matchId, Game game, SQLiteDatabase db) {
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

	private static List<Move> getMovesByTurn(long turnId, Game game, SQLiteDatabase db) {
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
	
	private long saveMatch(Match match, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(Match.BLACK_PLAYER, match.getBlackPlayer().getId());
		values.put(Match.WHITE_PLAYER, match.getWhitePlayer().getId());
		values.put(Match.NUM_GAMES, match.getNumGames());
		values.put(Match.FINISHED, match.isFinished());
		values.put(Match.CREATED, match.getCreated().getTime());
		
		if (match.getId() == -1) {
			long matchId = db.insert(Match.TABLE_NAME, null, values);
			match.setId(matchId);
			return matchId;
		}
		else {
			db.update(Match.TABLE_NAME, values, Match._ID + "=" + match.getId(), null);
			return match.getId();
		}
	}
	
	private int deleteMatch(Match match, SQLiteDatabase db) {
		// Delete all games in this match
		for (Game game : match.getGames()) {
			deleteGame(game.getId(),db);
		}
		
		return db.delete(Match.TABLE_NAME, Match._ID + "=" + match.getId(), null);
	}
	
	private int deleteMatch(long matchId, SQLiteDatabase db) {
		// Delete all games in this match
		for (Game game : getGamesByMatch(matchId,db)) {
			deleteGame(game.getId(),db);
		}
		
		return db.delete(Match.TABLE_NAME, Match._ID + "=" + matchId, null);
	}
	
	private long saveTurn(Turn turn, SQLiteDatabase db) {
		ContentValues values = new ContentValues();

		// FIXME:  Do something about the game relationship (research first)
		//values.put(Turn.GAME, turn.getGame().getId());
		values.put(Turn.PLAYER, turn.getPlayer().getId());
		values.put(Turn.COLOR, turn.getColor());
		values.put(Turn.DIE_ONE, turn.getDice().get(0).getValue());
		values.put(Turn.DIE_TWO, turn.getDice().get(1).getValue());
		values.put(Turn.CREATED, turn.getCreated().getTime());
		
		if (turn.getId() == -1) {
			long turnId = db.insert(Turn.TABLE_NAME, null, values);
			turn.setId(turnId);
			return turnId;
		}
		else {
			db.update(Turn.TABLE_NAME, values, Turn._ID + "=" + turn.getId(), null);
			return turn.getId();
		}
	}
	
	private int deleteTurn(Turn turn, SQLiteDatabase db) {
		return deleteTurn(turn.getId(),db);
	}
	
	private int deleteTurn(long turnId, SQLiteDatabase db) {
		// FIXME:  Delete all moves in this turn
		
		return db.delete(Turn.TABLE_NAME, Turn._ID + "=" + turnId, null);
	}
	
	private long saveMove(Move move, SQLiteDatabase db) {
		ContentValues values = new ContentValues();

		// FIXME:  Do something about the TURN relationship (research first)
		//values.put(Move.TURN, move.getTurn().getId());
		// FIXME:  Do something about the player relationship (research first)
		//values.put(Move.PLAYER, move.getPlayer().getId());
		values.put(Move.DIE, move.getDie().getValue());
		// FIXME:  Something seems wrong about storing color here
		//values.put(Move.COLOR, move.getColor().getValue());
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
	
	private int deleteMove(Move move, SQLiteDatabase db) {
		return deleteMove(move.getId(),db);
	}
	
	private int deleteMove(long moveId, SQLiteDatabase db) {
		return db.delete(Move.TABLE_NAME, Move._ID + "=" + moveId, null);
	}
	
	private long savePlayer(Player player, SQLiteDatabase db) {
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
	
	private int deletePlayer(Player player, SQLiteDatabase db) {
		return deletePlayer(player.getId(), db);
	}
	
	private int deletePlayer(long playerId, SQLiteDatabase db) {
		return db.delete(Player.TABLE_NAME, Player._ID + "=" + playerId, null);
	}
	
	
	private long saveGame(Game game, SQLiteDatabase db) {
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
			return gameId;
		}
		else {
			// This is an existing game
			db.update(Game.TABLE_NAME, values, Game._ID + "=" + game.getId(), null);
			return game.getId();
		}
	}

	private int deleteGame(Game game, SQLiteDatabase db) {
		return deleteGame(game.getId(), db);
	}
	
	private int deleteGame(long gameId, SQLiteDatabase db) {
		// FIXME:  Delete all turns in this game
		
		return db.delete(Game.TABLE_NAME, Game._ID + "=" + gameId,null);
	}
	
	private static List<Game> getGamesByMatch(long matchId, SQLiteDatabase db) {
		List<Game> games = new ArrayList<Game>();
		
		// FIXME: Map the relationship of games to matches sensibly and retrieve the games here.
		
		return games;
	}
	
	private static List<Turn> getTurnsByGame(long gameId, Game game, SQLiteDatabase db) {
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
					Player player = getPlayerById(playerId, color, game, db);
					
					SimpleDice dice = new SimpleDice(color);
					SimpleDie d1 = new SimpleDie(d1Value,color);
					dice.add(d1);
					SimpleDie d2 = new SimpleDie(d2Value,color);
					dice.add(d2);
					Turn turn = new Turn(player, dice);
					
					List<Move> moves = getMovesByTurn(turnId, game, db);
					turn.addMoves(moves);
				}
			}
		}

		
		return turns;
	}

	public static Player getPlayerById(long playerId, int color, Game game, SQLiteDatabase db) {
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


