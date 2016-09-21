package com.srinivas.dots;

import java.util.UUID;

/**
 * Created by srinivas on 9/21/16.
 */
public class GameFactory {
    Game games[] = new Game[Constants.MAX_CONCURRENT_GAMES];

    public GameFactory() {
        for (int i = 0; i < Constants.MAX_CONCURRENT_GAMES; i++) {
            games[i] = new Game(Constants.PLACEHOLDER, Constants.PLACEHOLDER);
        }
    }

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

    public Game queryGame(final String gameId) throws DotsService.IncorrectGameIDException {
        for (int i = 0; i < Constants.MAX_CONCURRENT_GAMES; i++) {
            if(gameId.equals(games[i].getGameId())) {
                return games[i];
            }
        }
        throw new DotsService.IncorrectGameIDException("Game doesn't exist");
    }

    public void setGame(Game g) {
        for (int i = 0; i < Constants.MAX_CONCURRENT_GAMES; i++) {
            if(games[i].getGameId().equals(g.getGameId())) {
                games[i] = g;
            }
        }
    }
}
