package com.srinivas.dots;

import java.util.UUID;

/**
 * Created by srinivas on 9/21/16.
 */

/**
 * A Game factory.
 */
public class GameFactory {
    Game games[] = new Game[Constants.MAX_CONCURRENT_GAMES];

    /**
     * Game facotry's empty constructor that does some initial setup.
     */
    public GameFactory() {
        for (int i = 0; i < Constants.MAX_CONCURRENT_GAMES; i++) {
            games[i] = new Game(Constants.PLACEHOLDER, Constants.PLACEHOLDER);
        }
    }

    /**
     * Creates a game if possible and returns it.
     * @param playerType the first player's color
     * @return a game object with init params initialized
     * @throws TooManyGamesException when games count more than max concurrent games
     */
    public Game createGame(final String playerType) throws TooManyGamesException {
        int i;
        for (i = 0; i <  Constants.MAX_CONCURRENT_GAMES; i++) {
            if (games[i].getState().equals(Constants.WAITING)) {
                System.out.println("Hii");
                break;
            } else if (games[i].getState().equals((Constants.FINISHED))) {
                break;
            }
        }
        System.out.println("i:" + i);
        if (i == Constants.MAX_CONCURRENT_GAMES) {
            throw new TooManyGamesException("Too many concurrent games!!");
        }
        UUID idOne = UUID.randomUUID();
        games[i] = new Game(playerType, idOne.toString());
        return games[i];
    }

    /**
     * Get s a game b y id
     * @param gameId the id to search for
     * @return a game object with matching ID
     * @throws DotsService.IncorrectGameIDException when no such game exists
     */
    public Game queryGame(final String gameId) throws DotsService.IncorrectGameIDException {
        for (int i = 0; i < Constants.MAX_CONCURRENT_GAMES; i++) {
            if(gameId.equals(games[i].getGameId())) {
                return games[i];
            }
        }
        throw new DotsService.IncorrectGameIDException("Game doesn't exist");
    }

    /**
     * Assigns a game to a game.
     * @param g the value to assign to running games
     */
    public void setGame(Game g) {
        for (int i = 0; i < Constants.MAX_CONCURRENT_GAMES; i++) {
            if(games[i].getGameId().equals(g.getGameId())) {
                games[i] = g;
            }
        }
    }
}
