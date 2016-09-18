package com.srinivas.dots;

/**
 * Created by srinivas on 9/17/16.
 */

/**
 *
 */
public class Game {
    private String gameId;
    private String playerId;
    private String playerId2;
    private String playerType;
    private String playerType2;
    private int redScore;
    private int blueScore;

    public Game(final String playerType) {
        this.gameId = Constants.gameID;
        System.out.println("GameId: " + this.gameId);
        this.playerId = Constants.pid1;
        this.playerType = playerType;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId2() {
        return playerId2;
    }

    public void setPlayerId2(String playerId2) {
        this.playerId2 = playerId2;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getPlayerType2() {
        return playerType2;
    }

    public void setPlayerType2(String playerType2) {
        this.playerType2 = playerType2;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                ", playerId='" + playerId + '\'' +
                ", playerId2='" + playerId2 + '\'' +
                ", playerType='" + playerType + '\'' +
                ", playerType2='" + playerType2 + '\'' +
                '}';
    }
}
