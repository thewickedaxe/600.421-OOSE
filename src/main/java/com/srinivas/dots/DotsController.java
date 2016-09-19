//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.srinivas.dots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class DotsController {

    private static final String API_CONTEXT = "/dots/api";

    private final DotsService dotsService;

    private final Logger logger = LoggerFactory.getLogger(DotsController.class);

    public DotsController(DotsService dotsService) {
        this.dotsService = dotsService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        /*
         POST: /games/:gameId/vmove

         Requests to make a move for a vertical line.

         Params:
              gameId: <String>, the ID of the current game.
         Body:
              playerId: <String>, the ID of the player making the request.
              row: <Number> The y-index of the requested line.
              col: <Number> The x-index of the requested line.

         Success: 200 (Success, OK)

         Failure:
              404 (Invalid game ID)
              404 (Invalid player ID)
              422 (Incorrect turn / not this player's turn)
              422 (Illegal move)
         */
        post(API_CONTEXT + "/games", "application/json", (request, response) -> {
            response.status(201);
            return dotsService.createNewGame(request.body());
        }, new JsonTransformer());

        post(API_CONTEXT + "/games/:gameID/vmove", "application/json", (request, response) -> {
            try {
                response.status(200);
                return dotsService.makeVmove(request.params(":gameId"), request.body());
            } catch(DotsService.IncorrectGameIDException | DotsService.PlayerMismatchException e) {
                response.status(404);
            } catch (DotsService.IncorrectTurnException e) {
                response.status(422);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        post(API_CONTEXT + "/games/:gameID/hmove", "application/json", (request, response) -> {
            try {
                response.status(200);
                return dotsService.makeHmove(request.params(":gameId"), request.body());
            } catch(DotsService.IncorrectGameIDException | DotsService.PlayerMismatchException e) {
                response.status(404);
            } catch (DotsService.IncorrectTurnException e) {
                response.status(422);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        get(API_CONTEXT + "/games/:gameid/board", "application/json", (request, response) -> {
            try {
                return dotsService.getBoard(request.params(":gameId"));
            } catch (DotsService.IncorrectGameIDException ex) {
                logger.error(String.format("Invalid GameID", request.params(":gameId")));
                response.status(404);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/games/:gameid/state", "application/json", (request, response) -> {
            try {
                logger.debug("Requested game id: " + request.params(":gameId"));
                return dotsService.getState(request.params(":gameId"));
            } catch (DotsService.IncorrectGameIDException ex) {
                response.status(404);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        put(API_CONTEXT + "/games/:gameId", "application/json", (request, response) -> {
            try {
                return dotsService.joinGame(request.params(":gameId"));
            } catch (DotsService.IncorrectGameIDException ex) {
                logger.error(String.format("Error joining game", request.params(":gameId")));
                response.status(404);
            }catch (DotsService.PlayerOverflowException ex) {
                logger.error("2 people are already playing the game!!");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        get(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                return dotsService.find(request.params(":id"));
            } catch (DotsService.IncorrectGameIDException ex) {
                logger.error(String.format("Failed to find object with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());


    }
}
