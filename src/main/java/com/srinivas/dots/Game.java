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
    private String whoseTurn;
    private String state;
    private Line horizontalLines[] = new Line[12];
    private Line verticalLines[] = new Line[12];

    public Game(final String pType) {
        this.gameId = Constants.GAMEID;
        System.out.println("GameId: " + this.gameId);
        this.playerId = Constants.PID1;
        this.playerType = pType;
        this.whoseTurn = Constants.RED;
        this.state = Constants.WAITING;
        int k = 0;
        for (int i =0; i < Constants.HROWS; i++) {
            for (int j = 0; j < Constants.HCOLS; j++) {
                Line temp = new Line(i, j, false);
                horizontalLines[k] = temp;
                k++;
            }
        }
        k = 0;
        for (int i =0; i < Constants.VROWS; i++) {
            for (int j = 0; j < Constants.VCOLS; j++) {
                Line temp = new Line(i, j, false);
                verticalLines[k] = temp;
                k++;
            }
        }
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(final String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId2() {
        return playerId2;
    }

    public void setPlayerId2(final String playerId2) {
        this.playerId2 = playerId2;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(final String playerType) {
        this.playerType = playerType;
    }

    public String getPlayerType2() {
        return playerType2;
    }

    public void setPlayerType2(final String playerType2) {
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

    public int getRedScore() {
        return redScore;
    }

    public void setRedScore(final int redScore) {
        this.redScore = redScore;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public void setBlueScore(final int blueScore) {
        this.blueScore = blueScore;
    }

    public String getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(final String whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }
}

class Line {
    private int row;
    private int col;
    private boolean filled;

    public Line(final int row, final int col, final boolean filled) {
        this.row = row;
        this.col = col;
        this.filled = filled;
    }

    public int getRow() {
        return row;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(final int col) {
        this.col = col;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(final boolean filled) {
        this.filled = filled;
    }
}
