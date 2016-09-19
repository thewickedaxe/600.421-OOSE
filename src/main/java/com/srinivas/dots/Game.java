package com.srinivas.dots;

/**
 * Created by srinivas on 9/17/16.
 */

/**
 *
 */
public class Game {
    private String gameId;
    private int playerCount;
    private String playerId;
    private String playerId2;
    private String playerType;
    private String playerType2;
    private int redScore;
    private int blueScore;
    private String whoseTurn;
    private String state;
    private Line horizontalLines[] = new Line[Constants.NUM_BOXES];
    private Line verticalLines[] = new Line[Constants.NUM_BOXES];
    private Box boxes[] = new Box[Constants.NUM_BOXES];

    public Game(final String pType) {
        this.gameId = Constants.GAMEID;
        System.out.println("GameId: " + this.gameId);
        this.playerId = Constants.PID1;
        this.playerId2 = Constants.PID2;
        this.playerCount = 1;
        this.playerType = pType.replace("\"", "");
        this.whoseTurn = Constants.RED;
        this.state = Constants.WAITING;
        int k = 0;
        for (int i =0; i < Constants.HROWS; i++) {
            for (int j = 0; j < Constants.HCOLS; j++) {
                horizontalLines[k] = new Line(i, j, false);
                boxes[k] = new Box(i, j, Constants.NONE_OWNER);
                k++;
            }
        }
        k = 0;
        for (int i =0; i < Constants.VROWS; i++) {
            for (int j = 0; j < Constants.VCOLS; j++) {
                verticalLines[k] = new Line(i, j, false);
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

    public String getPlayerColor(final String pid) {
        if (pid.equals(getPlayerId())) {
            return getPlayerType();
        } else {
            return getPlayerType2();
        }
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

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(final int playerCount) {
        this.playerCount = playerCount;
    }

    public Line getVerticalLine(final int row, final int col) {
        for (Line l : verticalLines) {
            if (l.getRow() == row) {
                if (l.getCol() == col) {
                    return l;
                }
            }
        }
        return null;
    }

    public void setVerticalLine(final int row, final int col) {
        for (int i = 0; i < (Constants.HCOLS * Constants.HROWS); i++) {
            if (verticalLines[i].getRow() == row) {
                if (verticalLines[i].getCol() == col) {
                    verticalLines[i].setFilled(true);
                }
            }
        }
    }

    public void setHorizontalLine(final int row, final int col) {
        for (int i = 0; i < (Constants.HCOLS * Constants.HROWS); i++) {
            if (horizontalLines[i].getRow() == row) {
                if (horizontalLines[i].getCol() == col) {
                    horizontalLines[i].setFilled(true);
                }
            }
        }
    }

    public Line getHorizontalLine(final int row, final int col) {
        for (Line l : horizontalLines) {
            if (l.getRow() == row) {
                if (l.getCol() == col) {
                    return l;
                }
            }
        }
        return null;
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

    public int getCol() {
        return col;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(final boolean filled) {
        this.filled = filled;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public void setCol(final int col) {
        this.col = col;
    }
}

class Box {
    private int row;
    private int col;
    private boolean owned;
    private String owner;

    private Line top;
    private Line bot;
    private Line left;
    private Line right;

    public Box(final int row, final int col, final String owner) {
        this.row = row;
        this.col = col;
        this.owner = owner;
        this.owned = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(final boolean owned) {
        this.owned = owned;
    }

    public Line getTop() {
        return top;
    }

    public void setTop(Line top) {
        this.top.setRow(top.getRow());
        this.top.setCol(top.getCol());
        this.top.setFilled(top.isFilled());
    }

    public Line getBot() {
        return bot;
    }

    public void setBot(Line bot) {
        this.bot.setRow(bot.getRow());
        this.bot.setCol(bot.getCol());
        this.bot.setFilled(bot.isFilled());
    }

    public Line getLeft() {
        return left;
    }

    public void setLeft(Line left) {
        this.left.setRow(left.getRow());
        this.left.setCol(left.getCol());
        this.left.setFilled(left.isFilled());
    }

    public Line getRight() {
        return right;
    }

    public void setRight(Line right) {
        this.right.setRow(right.getRow());
        this.right.setCol(right.getCol());
        this.right.setFilled(right.isFilled());
    }
}

class GenericResponse {
    private String playerType;
    private String playerId;
    private String gameId;

    public GenericResponse(final String playerType, final String playerId, final String gameId) {
        this.playerType = playerType;
        this.playerId = playerId;
        this.gameId = gameId;
    }
}