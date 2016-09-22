package com.srinivas.dots;

/**
 * Created by srinivas on 9/17/16.
 */

/**
 * A class that represents a unique game object.
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
    private Line horizontalLines[] = new Line[Constants.HROWS * Constants.HCOLS];
    private Line verticalLines[] = new Line[Constants.VROWS * Constants.VCOLS];
    private Box boxes[] = new Box[Constants.NUM_BOXES];

    /**
     * Game's Constructor.
     * @param pType the color of the first player
     * @param tgameid a game id for the game
     */
    public Game(final String pType, final String tgameid) {
        this.gameId = tgameid;
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
                if (k < Constants.NUM_BOXES) {
                    boxes[k] = new Box(i, j, Constants.NONE_OWNER);
                }
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
        System.out.println("Comes here too!");
    }

    /**
     * Gets a game's gameID.
     * @return the game ID of a game
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * sets a game's gameID.
     * @param gameId the gameID to assign to a game
     */
    public void setGameId(final String gameId) {
        this.gameId = gameId;
    }

    /**
     * Get's the first player id of a game.
     * @return the first player's id
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Set's the first player's ID.
     * @param playerId the value to assign to the first player id
     */
    public void setPlayerId(final String playerId) {
        this.playerId = playerId;
    }

    /**
     * Get's the second player's ID.
     * @return the ID of the second player
     */
    public String getPlayerId2() {
        return playerId2;
    }

    /**
     * Assigns a value to the seoncd player's ID
     * @param playerId2 The value to assign to the second player's ID
     */
    public void setPlayerId2(final String playerId2) {
        this.playerId2 = playerId2;
    }

    /**
     * Return's the color of the first player.
     * @return the first player's color
     */
    public String getPlayerType() {
        return playerType;
    }

    /**
     * Checks if a game is over.
     * @return true when all boxes are owned, false otherwise
     */
    public boolean validateFinish() {
        for (Box b : boxes) {
            if (!b.isOwned()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Set's a player's color.
     * @param playerType the value to assign to player 1
     */
    public void setPlayerType(final String playerType) {
        this.playerType = playerType;
    }

    /**
     * Gets the second player's color.
     * @return the color of the second player
     */
    public String getPlayerType2() {
        return playerType2;
    }

    /**
     * Sets the second player's color.
     * @param playerType2 the value to assign to the second player's color
     */
    public void setPlayerType2(final String playerType2) {
        this.playerType2 = playerType2;
    }

    /**
     * Stringifies the class.
     * @return the class as string
     */
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

    /**
     * Gets red's score
     * @return the value of red's score
     */
    public int getRedScore() {
        return redScore;
    }

    /**
     * Assigns ownership to a box
     * @param row the row of the box
     * @param col the column of the box
     * @param owner the ownership to assign to it
     */
    public void setBox(final int row, final int col, final String owner) {
        for (int i = 0; i < Constants.NUM_BOXES; i++) {
            if (boxes[i].getRow() == row) {
                if (boxes[i].getCol() == col) {
                    boxes[i].setOwner(owner);
                    boxes[i].setOwned(true);
                }
            }
        }
    }

    /**
     * Sets red's score.
     * @param redScore the value to assign to red's score
     */
    public void setRedScore(final int redScore) {
        this.redScore = redScore;
    }

    /**
     * Gets blue's score.
     * @return the integral value of blue's score
     */
    public int getBlueScore() {
        return blueScore;
    }

    /**
     * Assign;s a value to blue's score.
     * @param blueScore the value to assign to blue's score
     */
    public void setBlueScore(final int blueScore) {
        this.blueScore = blueScore;
    }

    /**
     * Find out whose turn it is.
     * @return returns red or blue.
     */
    public String getWhoseTurn() {
        return whoseTurn;
    }

    /**
     * Given a player ID tells you their color.
     * @param pid the player ID to check with
     * @return the color of the player- red or blue
     */
    public String getPlayerColor(final String pid) {
        if (pid.equals(getPlayerId())) {
            return getPlayerType();
        } else {
            return getPlayerType2();
        }
    }

    /**
     * Decide to who is going to play next.
     * @param whoseTurn the color to assign to whoseTurn - red or blue
     */
    public void setWhoseTurn(final String whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    /**
     * Returns the state of the game
     * @return the state - waiting, in progress or finished
     */
    public String getState() {
        return state;
    }

    /**
     * Assigns a value to state.
     * @param state in progress, waiting or finished is assigned to game state
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * Returns the number of active players.
     * @return 1 or 2
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Sets the number of active players.
     * @param playerCount the number of people currently playing 1 or 2
     */
    public void setPlayerCount(final int playerCount) {
        this.playerCount = playerCount;
    }

    /**
     * Return a line defined by it's row and column.
     * @param row the row value of the line
     * @param col the col value of the line
     * @return a Line object at that row-col
     */
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

    /**
     * Assigns a line as filled.
     * @param row the row of the line
     * @param col the col of the line
     */
    public void setVerticalLine(final int row, final int col) {
        for (int i = 0; i < (Constants.HCOLS * Constants.HROWS); i++) {
            if (verticalLines[i].getRow() == row) {
                if (verticalLines[i].getCol() == col) {
                    verticalLines[i].setFilled(true);
                }
            }
        }
    }

    /**
     * Sets a horizontal line as filled.
     * @param row the row value of the line
     * @param col the col value of the line
     */
    public void setHorizontalLine(final int row, final int col) {
        for (int i = 0; i < (Constants.HCOLS * Constants.HROWS); i++) {
            if (horizontalLines[i].getRow() == row) {
                if (horizontalLines[i].getCol() == col) {
                    horizontalLines[i].setFilled(true);
                }
            }
        }
    }

    /**
     * Returns a line object at a row-col.
     * @param row the row value of a line
     * @param col the col value of a line
     * @return the Line defined by that row-col
     */
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

/**
 * A class to represent lines.
 */
class Line {
    private int row;
    private int col;
    private boolean filled;

    /**
     * Line's constructor.
     * @param row the row of the line
     * @param col the col of the line
     * @param filled the line's filled status
     */
    public Line(final int row, final int col, final boolean filled) {
        this.row = row;
        this.col = col;
        this.filled = filled;
    }

    /**
     * Returns the row of the line.
     * @return the row number of the line
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the col of a line.
     * @return the col number of the line
     */
    public int getCol() {
        return col;
    }

    /**
     * Checks if a line is filled.
     * @return true if filled, false if not
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * Sets a line as filled.
     * @param filled true always, here for brevity
     */
    public void setFilled(final boolean filled) {
        this.filled = filled;
    }
}

/**
 * A class to represent a box.
 */
class Box {
    private int row;
    private int col;
    private boolean owned;
    private String owner;

    /**
     * Box's constructor.
     * @param row the row of the box
     * @param col the col of the box
     * @param owner the box's owner
     */
    public Box(final int row, final int col, final String owner) {
        this.row = row;
        this.col = col;
        this.owner = owner;
        this.owned = false;
    }

    /**
     * Gets the row of the box.
     * @return the row number of the box
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the col of the box.
     * @return the col number of the box
     */
    public int getCol() {
        return col;
    }

    /**
     * Assigns an owner to the box.
     * @param owner the owner of the box
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * Checks if a box is owned.
     * @return true if owned, false if not
     */
    public boolean isOwned() {
        return owned;
    }

    /**
     * Sets a box's owned flag.
     * @param owned true always, here for brevity
     */
    public void setOwned(final boolean owned) {
        this.owned = owned;
    }

}

class GenericResponse {
    /**
     * A custom response object to help joining a game.
     */
    private String playerType;
    private String playerId;
    private String gameId;

    /**
     * Generic Response's constructor.
     * @param playerType the type of the player
     * @param playerId the player's id
     * @param gameId the game's id
     */
    public GenericResponse(final String playerType, final String playerId, final String gameId) {
        this.playerType = playerType;
        this.playerId = playerId;
        this.gameId = gameId;
    }
}