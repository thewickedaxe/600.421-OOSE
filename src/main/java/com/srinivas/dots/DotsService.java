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
    public DotsService(DataSource dataSource) throws DotsServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS item (item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "                                 title TEXT, done BOOLEAN, created_on TIMESTAMP)" ;
            conn.createQuery(sql).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new DotsServiceException("Failed to create schema at startup", ex);
        }
    }

    /**
     * Fetch all todo entries in the list
     *
     * @return List of all Todo entries
     */
    public List<Todo> findAll() throws DotsServiceException {
        String sql = "SELECT * FROM item" ;
        try (Connection conn = db.open()) {
            List<Todo> todos =  conn.createQuery(sql)
                .addColumnMapping("item_id", "id")
                .addColumnMapping("created_on", "createdOn")
                .executeAndFetch(Todo.class);
            return todos;
        } catch(Sql2oException ex) {
            logger.error("DotsService.findAll: Failed to query database", ex);
            throw new DotsServiceException("DotsService.findAll: Failed to query database", ex);
        }
    }


    /**
     * Find a todo entry given an Id.
     *
     * @param id The id for the Todo entry
     * @return The Todo corresponding to the id if one is found, otherwise null
     */
    public Todo find(String id) throws DotsServiceException {
        String sql = "SELECT * FROM item WHERE item_id = :itemId ";

        try (Connection conn = db.open()) {
            return conn.createQuery(sql)
                .addParameter("itemId", Integer.parseInt(id))
                .addColumnMapping("item_id", "id")
                .addColumnMapping("created_on", "createdOn")
                .executeAndFetchFirst(Todo.class);
        } catch(Sql2oException ex) {
            logger.error(String.format("DotsService.find: Failed to query database for id: %s", id), ex);
            throw new DotsServiceException(String.format("DotsService.find: Failed to query database for id: %s", id), ex);
        }
    }

    /**
     * Update the specified Todo entry with new information
     */
    public Todo update(String todoId, String body) throws DotsServiceException {
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
                throw new DotsServiceException(String.format("DotsService.update: Update operation did not update rows. Incorrect id (?): %s", todoId), null);
            }
        } catch(Sql2oException ex) {
            logger.error(String.format("DotsService.update: Failed to update database for id: %s", todoId), ex);
            throw new DotsServiceException(String.format("DotsService.update: Failed to update database for id: %s", todoId), ex);
        }

        return find(todoId);
    }

    /**
     * Delete the entry with the specified id
     */
    public void delete(String todoId) throws DotsServiceException {
        String sql = "DELETE FROM item WHERE item_id = :itemId" ;
        try (Connection conn = db.open()) {
            //Delete the item
            conn.createQuery(sql)
                .addParameter("itemId", Integer.parseInt(todoId))
                .executeUpdate();

            //Verify that we did indeed change something
            if (getChangedRows(conn) != 1) {
                logger.error(String.format("DotsService.delete: Delete operation did not delete rows. Incorrect id(?): %s", todoId));
                throw new DotsServiceException(String.format("DotsService.delete: Delete operation did not delete rows. Incorrect id(?): %s", todoId), null);
            }
        } catch(Sql2oException ex) {
            logger.error(String.format("DotsService.update: Failed to delete id: %s", todoId), ex);
            throw new DotsServiceException(String.format("DotsService.update: Failed to delete id: %s", todoId), ex);
        }
    }


    /**
     * Create a new Todo entry.
     */
    public void createNewTodo(String body) throws DotsServiceException {
        Todo todo = new Gson().fromJson(body, Todo.class);

        String sql = "INSERT INTO item (title, done, created_on) " +
                "             VALUES (:title, :done, :createdOn)" ;

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .bind(todo)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("DotsService.createNewTodo: Failed to create new entry", ex);
            throw new DotsServiceException("DotsService.createNewTodo: Failed to create new entry", ex);
        }
    }


    public Game createNewGame(final String body) {
        JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
        game = new Game(requestBody.get("playerType").toString());
        return game;
    }

    public Game getBoard(final String id) throws DotsServiceException {
        if (!id.equals(game.getGameId())) {
            throw new DotsServiceException("Incorrect game ID queried");
        }
        return game;
    }

    public Game getState(final String id) throws DotsServiceException {
        if (!id.equals(game.getGameId())) {
            logger.info("Requested ID: " + id);
            logger.info("Existing ID: " + game.getGameId());
            throw new DotsServiceException("Incorrect game ID queried");
        }
        return game;
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class DotsServiceException extends Exception {
        public DotsServiceException(final String message, final Throwable cause) {
            super(message, cause);
        }
        public DotsServiceException(final String message) { super((message));}
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
