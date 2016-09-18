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
            return dotsService.createNewGame(request.body());
        }, new JsonTransformer());

        get(API_CONTEXT + "/games/:gameid/board", "application/json", (request, response) -> {
            try {
                return dotsService.getBoard(request.params(":gameId"));
            } catch (DotsService.DotsServiceException ex) {
                logger.error(String.format("Invalid GameID", request.params(":gameId")));
                response.status(404);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/games/:gameid/state", "application/json", (request, response) -> {
            try {
                logger.info("Requested game id: " + request.params(":gameId"));
                return dotsService.getState(request.params(":gameId"));
            } catch (DotsService.DotsServiceException ex) {
                logger.error(String.format("Invalid GameID", request.params(":gameId")));
                response.status(404);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                return dotsService.find(request.params(":id"));
            } catch (DotsService.DotsServiceException ex) {
                logger.error(String.format("Failed to find object with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/todos", "application/json", (request, response)-> {
            try {
                return dotsService.findAll() ;
            } catch (DotsService.DotsServiceException ex) {
                logger.error("Failed to fetch the list of todos");
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        put(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                return dotsService.update(request.params(":id"), request.body());
            } catch (DotsService.DotsServiceException ex) {
                logger.error(String.format("Failed to update todo with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        delete(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                dotsService.delete(request.params(":id"));
                response.status(200);
            } catch (DotsService.DotsServiceException ex) {
                logger.error(String.format("Failed to delete todo with id: %s", request.params(":id")));
                response.status(500);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
    }
}
