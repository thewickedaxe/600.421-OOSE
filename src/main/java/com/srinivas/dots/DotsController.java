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

        post(API_CONTEXT + "/games", "application/json", (request, response) -> {
            response.status(201);
            try {
                return dotsService.createNewGame(request.body());
            } catch (TooManyGamesException e) {
                logger.error("Too many concurrent games");
                response.status(404);
                return Collections.EMPTY_MAP;
            }
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

    }
}
