//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.srinivas.dots;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import javax.sql.DataSource;
import java.util.List;

public class DotsService {
    public GameFactory gameFactory;

    private final Logger logger = LoggerFactory.getLogger(DotsService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     */
    public DotsService( ) {
        gameFactory = new GameFactory();
    }

    public Game checkPlayerId(final String id, final Game game) throws PlayerMismatchException {
        String tempid = id.replace("\"", "");
        if (!tempid.equals(game.getPlayerId()) && !tempid.equals(game.getPlayerId2())) {
            logger.error("Bad Player ID: " + id);
            throw new PlayerMismatchException("Incorrect playerID!");
        }
        return game;
    }

    public Game createNewGame(final String body) throws TooManyGamesException {
        JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
        Game game = gameFactory.createGame(requestBody.get("playerType").toString());
        logger.info("Comes Here");
        return game;
    }

    public Game checkTurnCorrectness(final String id, final int row, final int col, final String nature, final Game game)
            throws IncorrectTurnException {
        if (!game.getWhoseTurn().equals(game.getPlayerColor(id))) {
            throw new IncorrectTurnException("Not your turn!!");
        }
        if (nature.equals(Constants.VERTICAL)) {
            Line l = game.getVerticalLine(row, col);
            if (l.isFilled()) {
                throw new IncorrectTurnException("Line already filled");
            } else {
                game.setVerticalLine(row, col);
            }

        } else if (nature.equals(Constants.HORIZONTAL)) {
            Line l = game.getHorizontalLine(row, col);
            if (l.isFilled()) {
                throw new IncorrectTurnException("Line already filled");
            } else {
                game.setHorizontalLine(row, col);
            }
        }
        return game;
    }

    public Game getBoard(final String id) throws IncorrectGameIDException {
        Game game = gameFactory.queryGame(id);
        return game;
    }

    public Game getState(final String id) throws IncorrectGameIDException {
        Game game = gameFactory.queryGame(id);
        if (game.validateFinish()) {
            game.setState(Constants.FINISHED);
        }
        gameFactory.setGame(game);
        return game;
    }

    public Game flipTurns(final Game game) {
        if (game.getWhoseTurn().equals(Constants.RED)) {
            game.setWhoseTurn(Constants.BLUE);
        } else {
            game.setWhoseTurn(Constants.RED);
        }
        return game;
    }

    private Game recalcScores(final int row, final int col, final String nature, Game game) {
        boolean scoreFlag = false;
        if (nature.equals(Constants.VERTICAL)) {
            if (col > 0) {
                if (game.getHorizontalLine(row, col - 1).isFilled() &&
                    game.getHorizontalLine(row + 1, col - 1).isFilled() &&
                    game.getVerticalLine(row, col - 1).isFilled()) {
                    scoreFlag = true;
                    if (game.getWhoseTurn().equals(Constants.RED)) {
                        game.setRedScore(game.getRedScore() + 1);
                    } else if (game.getWhoseTurn().equals(Constants.BLUE)) {
                        game.setBlueScore(game.getBlueScore() + 1);
                    }
                    game.setBox(row, col - 1, game.getWhoseTurn());
                }
            }
            if (col < Constants.VCOLS - 1) {
                if (game.getHorizontalLine(row, col).isFilled() &&
                        game.getHorizontalLine(row + 1, col).isFilled() &&
                        game.getVerticalLine(row, col + 1).isFilled()) {
                    scoreFlag = true;
                    if (game.getWhoseTurn().equals(Constants.RED)) {
                        game.setRedScore(game.getRedScore() + 1);
                    } else if (game.getWhoseTurn().equals(Constants.BLUE)) {
                        game.setBlueScore(game.getBlueScore() + 1);
                    }
                    game.setBox(row, col, game.getWhoseTurn());
                }
            }
            if (scoreFlag) {
                game = flipTurns(game);
            }
            return game;
        } else if (nature.equals(Constants.HORIZONTAL)) {
            if (row > 0) {
                if (game.getHorizontalLine(row - 1, col).isFilled() &&
                        game.getVerticalLine(row - 1, col).isFilled() &&
                        game.getVerticalLine(row - 1, col + 1).isFilled()) {
                    scoreFlag = true;
                    if (game.getWhoseTurn().equals(Constants.RED)) {
                        game.setRedScore(game.getRedScore() + 1);
                    } else if (game.getWhoseTurn().equals(Constants.BLUE)) {
                        game.setBlueScore(game.getBlueScore() + 1);
                    }
                    game.setBox(row - 1, col, game.getWhoseTurn());
                }
            }
            if (row < Constants.HROWS - 1) {
                if (game.getHorizontalLine(row + 1, col).isFilled() &&
                        game.getVerticalLine(row, col).isFilled() &&
                        game.getVerticalLine(row, col + 1).isFilled()) {
                    scoreFlag = true;
                    if (game.getWhoseTurn().equals(Constants.RED)) {
                        game.setRedScore(game.getRedScore() + 1);
                    } else if (game.getWhoseTurn().equals(Constants.BLUE)) {
                        game.setBlueScore(game.getBlueScore() + 1);
                    }
                    game.setBox(row, col, game.getWhoseTurn());
                }
            }
            if (scoreFlag) {
                game = flipTurns(game);
            }
            return game;
        }
        return game;
    }

    public Game makeVmove(final String id, final String body)
            throws IncorrectGameIDException, PlayerMismatchException, IncorrectTurnException {
        Game game = gameFactory.queryGame(id);
        JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
        game = checkPlayerId(requestBody.get("playerId").toString(), game);
        game = checkTurnCorrectness(requestBody.get("playerId").toString().replace("\"", ""),
                Integer.parseInt(requestBody.get("row").toString().replace("\"", "")),
                Integer.parseInt(requestBody.get("col").toString().replace("\"", "")),
                Constants.VERTICAL, game);
        game = recalcScores(Integer.parseInt(requestBody.get("row").toString().replace("\"", "")),
                Integer.parseInt(requestBody.get("col").toString().replace("\"", "")),
                Constants.VERTICAL, game);
        game = flipTurns(game);
        gameFactory.setGame(game);
        return game;
    }

    public Game makeHmove(final String id, final String body)
            throws IncorrectGameIDException, PlayerMismatchException, IncorrectTurnException {
        Game game = gameFactory.queryGame(id);
        JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
        game  = checkPlayerId(requestBody.get("playerId").toString(), game);
        game = checkTurnCorrectness(requestBody.get("playerId").toString().replace("\"", ""),
                Integer.parseInt(requestBody.get("row").toString().replace("\"", "")),
                Integer.parseInt(requestBody.get("col").toString().replace("\"", "")),
                Constants.HORIZONTAL, game);
        game = recalcScores(Integer.parseInt(requestBody.get("row").toString().replace("\"", "")),
               Integer.parseInt(requestBody.get("col").toString().replace("\"", "")),
                Constants.HORIZONTAL, game);
        game = flipTurns(game);
        gameFactory.setGame(game);
        return game;
    }

    public GenericResponse joinGame(final String id) throws IncorrectGameIDException, PlayerOverflowException {
        Game game = gameFactory.queryGame(id);
        if (game.getPlayerCount() == Constants.MAX_PLAYER_COUNT) {
            logger.error("2 people are already playing the game");
            throw new PlayerOverflowException("2 people are already playing!!");
        }
        // This is a hack for not maintaining two game objects
        game.setPlayerCount(2);
        game.setState(Constants.IN_PROGRESS);
        if (game.getPlayerType().equals(Constants.RED)) {
            game.setPlayerType2(Constants.BLUE);
        } else if (game.getPlayerType().equals(Constants.BLUE)) {
            game.setPlayerType2(Constants.RED);
        }
        gameFactory.setGame(game);
        logger.debug("Player Type1: " + game.getPlayerType());
        logger.debug("Player Type2: " + game.getPlayerType2());
        GenericResponse r = new GenericResponse(game.getPlayerType2(), game.getPlayerId2(), game.getGameId());
        return r;
    }


    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class IncorrectGameIDException extends Exception {
        public IncorrectGameIDException(final String message) {
            super((message));
        }
    }

    public static class PlayerOverflowException extends Exception {
        public PlayerOverflowException(String s) {
            super(s);
        }
    }

    public static class PlayerMismatchException extends Exception {
        public PlayerMismatchException(String s) {
            super(s);
        }
    }

    public static class IncorrectTurnException extends Exception {
        public IncorrectTurnException(String s) {
            super(s);
        }
    }
}
