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

    private Sql2o db;
    public Game game;

    private final Logger logger = LoggerFactory.getLogger(DotsService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    public DotsService(DataSource dataSource) throws IncorrectGameIDException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS item (item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "                                 title TEXT, done BOOLEAN, created_on TIMESTAMP)";
            conn.createQuery(sql).executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new IncorrectGameIDException("Failed to create schema at startup", ex);
        }
    }

    /**
     * Fetch all todo entries in the list
     *
     * @return List of all Todo entries
     */
    public List<Todo> findAll() throws IncorrectGameIDException {
        String sql = "SELECT * FROM item";
        try (Connection conn = db.open()) {
            List<Todo> todos = conn.createQuery(sql)
                    .addColumnMapping("item_id", "id")
                    .addColumnMapping("created_on", "createdOn")
                    .executeAndFetch(Todo.class);
            return todos;
        } catch (Sql2oException ex) {
            logger.error("DotsService.findAll: Failed to query database", ex);
            throw new IncorrectGameIDException("DotsService.findAll: Failed to query database", ex);
        }
    }


    /**
     * Find a todo entry given an Id.
     *
     * @param id The id for the Todo entry
     * @return The Todo corresponding to the id if one is found, otherwise null
     */
    public Todo find(String id) throws IncorrectGameIDException {
        String sql = "SELECT * FROM item WHERE item_id = :itemId ";

        try (Connection conn = db.open()) {
            return conn.createQuery(sql)
                    .addParameter("itemId", Integer.parseInt(id))
                    .addColumnMapping("item_id", "id")
                    .addColumnMapping("created_on", "createdOn")
                    .executeAndFetchFirst(Todo.class);
        } catch (Sql2oException ex) {
            logger.error(String.format("DotsService.find: Failed to query database for id: %s", id), ex);
            throw new IncorrectGameIDException(String.format("DotsService.find: Failed to query database for id: %s", id), ex);
        }
    }

    /**
     * Update the specified Todo entry with new information
     */
    public Todo update(String todoId, String body) throws IncorrectGameIDException {
        Todo todo = new Gson().fromJson(body, Todo.class);

        String sql = "UPDATE item SET title = :title, done = :done, created_on = :createdOn WHERE item_id = :itemId ";
        try (Connection conn = db.open()) {
            //Update the item
            conn.createQuery(sql)
                    .bind(todo)  // one-liner to map all Todo object fields to query parameters :title etc
                    .addParameter("itemId", Integer.parseInt(todoId))
                    .executeUpdate();

            //Verify that we did indeed update something
            if (getChangedRows(conn) != 1) {
                logger.error(String.format("DotsService.update: Update operation did not update rows. Incorrect id(?): %s", todoId));
                throw new IncorrectGameIDException(String.format("DotsService.update: Update operation did not update rows. Incorrect id (?): %s", todoId), null);
            }
        } catch (Sql2oException ex) {
            logger.error(String.format("DotsService.update: Failed to update database for id: %s", todoId), ex);
            throw new IncorrectGameIDException(String.format("DotsService.update: Failed to update database for id: %s", todoId), ex);
        }

        return find(todoId);
    }

    /**
     * Delete the entry with the specified id
     */
    public void delete(String todoId) throws IncorrectGameIDException {
        String sql = "DELETE FROM item WHERE item_id = :itemId";
        try (Connection conn = db.open()) {
            //Delete the item
            conn.createQuery(sql)
                    .addParameter("itemId", Integer.parseInt(todoId))
                    .executeUpdate();

            //Verify that we did indeed change something
            if (getChangedRows(conn) != 1) {
                logger.error(String.format("DotsService.delete: Delete operation did not delete rows. Incorrect id(?): %s", todoId));
                throw new IncorrectGameIDException(String.format("DotsService.delete: Delete operation did not delete rows. Incorrect id(?): %s", todoId), null);
            }
        } catch (Sql2oException ex) {
            logger.error(String.format("DotsService.update: Failed to delete id: %s", todoId), ex);
            throw new IncorrectGameIDException(String.format("DotsService.update: Failed to delete id: %s", todoId), ex);
        }
    }


    /**
     * Create a new Todo entry.
     */
    public void createNewTodo(String body) throws IncorrectGameIDException {
        Todo todo = new Gson().fromJson(body, Todo.class);

        String sql = "INSERT INTO item (title, done, created_on) " +
                "             VALUES (:title, :done, :createdOn)";

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .bind(todo)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("DotsService.createNewTodo: Failed to create new entry", ex);
            throw new IncorrectGameIDException("DotsService.createNewTodo: Failed to create new entry", ex);
        }
    }

    public void checkGameId(final String id) throws IncorrectGameIDException {
        String tempid = id.replace("\"", "");
        if (!tempid.equals(game.getGameId())) {
            logger.error("Bad Game ID: " + id);
            throw new IncorrectGameIDException("Incorrect game ID queried");
        }
    }

    public void checkPlayerId(final String id) throws PlayerMismatchException {
        String tempid = id.replace("\"", "");
        if (!tempid.equals(game.getPlayerId()) && !tempid.equals(game.getPlayerId2())) {
            logger.error("Bad Player ID: " + id);
            throw new PlayerMismatchException("Incorrect playerID!");
        }
    }

    public Game createNewGame(final String body) {
        JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
        game = new Game(requestBody.get("playerType").toString());
        return game;
    }

    public void checkTurnCorrectness(final String id, final int row, final int col, final String nature)
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
    }

    public Game getBoard(final String id) throws IncorrectGameIDException {
        checkGameId(id);
        return game;
    }

    public Game getState(final String id) throws IncorrectGameIDException {
        checkGameId(id);
        return game;
    }

    public void flipTurns() {
        if (game.getWhoseTurn().equals(Constants.RED)) {
            game.setWhoseTurn(Constants.BLUE);
        } else {
            game.setWhoseTurn(Constants.RED);
        }
    }

    private void recalcScores(final int row, final int col, final String nature) {
        boolean scoreFlag = false;
        if (nature.equals(Constants.VERTICAL)) {
            if (col > 0) {
                Line leftTop = game.getHorizontalLine(row , col - 1);
                Line leftBot = game.getHorizontalLine(row + 1, col - 1);
                Line left = game.getVerticalLine(row, col - 1);
                if ()
        }
    }

    public Game makeVmove(final String id, final String body)
            throws IncorrectGameIDException, PlayerMismatchException, IncorrectTurnException {
        checkGameId(id);
        JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
        checkPlayerId(requestBody.get("playerId").toString());
        checkTurnCorrectness(requestBody.get("playerId").toString().replace("\"", ""),
                Integer.parseInt(requestBody.get("row").toString().replace("\"", "")),
                Integer.parseInt(requestBody.get("col").toString().replace("\"", "")),
                Constants.VERTICAL);
        flipTurns();
        recalcScores(Integer.parseInt(requestBody.get("row").toString().replace("\"", "")),
                     Integer.parseInt(requestBody.get("col").toString().replace("\"", "")),
                     Constants.VERTICAL);
        return game;
    }

    public Game makeHmove(final String id, final String body)
            throws IncorrectGameIDException, PlayerMismatchException, IncorrectTurnException {
        checkGameId(id);
        JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
        checkPlayerId(requestBody.get("playerId").toString());
        checkTurnCorrectness(requestBody.get("playerId").toString().replace("\"", ""),
                Integer.parseInt(requestBody.get("row").toString().replace("\"", "")),
                Integer.parseInt(requestBody.get("col").toString().replace("\"", "")),
                Constants.HORIZONTAL);
        flipTurns();
        return game;
    }

    public GenericResponse joinGame(final String id) throws IncorrectGameIDException, PlayerOverflowException {
        checkGameId(id);
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
        logger.debug("Player Type1: " + game.getPlayerType());
        logger.debug("Player Type2: " + game.getPlayerType2());
        GenericResponse r = new GenericResponse(game.getPlayerType2(), game.getPlayerId2(), game.getGameId());
        return r;
    }


    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class IncorrectGameIDException extends Exception {
        public IncorrectGameIDException(final String message, final Throwable cause) {
            super(message, cause);
        }

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

    /**
     * This Sqlite specific method returns the number of rows changed by the most recent
     * INSERT, UPDATE, DELETE operation. Note that you MUST use the same connection to get
     * this information
     */
    private int getChangedRows(Connection conn) throws Sql2oException {
        return conn.createQuery("SELECT changes()").executeScalar(Integer.class);
    }
}
