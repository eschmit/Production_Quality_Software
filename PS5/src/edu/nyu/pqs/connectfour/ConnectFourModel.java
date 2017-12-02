package edu.nyu.pqs.connectfour;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The {@code ConnectFourModel} class implements the necessary functionality
 * for the connect four game. The game has two players, one red, one black.
 * The game can be played with two people or with a computer player.
 * <p>
 * The class implements the {@code ConnectFourSubject} interface and
 * its methods and holds a list of observers.
 * 
 * @author Eric
 * @see ConnectFourSubject
 *
 */
public class ConnectFourModel implements ConnectFourSubject {
  private List<ConnectFourObserver> observers;
  private static final int NUM_ROWS = 6;
  private static final int NUM_COLUMNS = 7;
  private ConnectFourColor[][] gameGrid;
  private final ConnectFourPlayer player1;
  private final ConnectFourPlayer player2;
  private ConnectFourPlayer playerTurn;

  /**
   * The {@code Builder} class is a static class that uses the Builder pattern to create a 
   * {@code ConnectFourModel} object.
   * <p>
   * The builder requires at least one player and takes a max of two. 
   * Players cannot be null.
   * 
   */
  public static class Builder {
    //Required parameter
    private final ConnectFourPlayer player1;

    //Optional parameter
    private ConnectFourPlayer player2 = null;

    public Builder(ConnectFourPlayer player) {
      if (player == null) {
        throw new IllegalArgumentException("Player cannot be null");
      }
      this.player1 = player;
    }

    /**
     * Creates an optional second player. The second player can always be a
     * {@code ConnectFourHumanPlayer}, but must be a different color than
     * the first player. The second player can only be a
     * {@code ConnectFourComputerPlayer} if the first player is a
     * {@code ConnectFourHumanPlayer}.
     * @param player the second ConnectFourPlayer.
     * @return the Builder object.
     * IllegalArgumentException if player is null or if two players have the 
     * same color or if both players are computer players.
     */
    public Builder secondPlayer(ConnectFourPlayer player) {
      if (player == null) {
        throw new IllegalArgumentException("Player cannot be null");
      }
      if (player.getColor().equals(this.player1.getColor())) {
        throw new IllegalArgumentException(
            "Player 1 and player 2 cannot be the same color");
      }
      if (this.player1.getPlayerType().equals(PlayerType.COMPUTER) && 
          player.getPlayerType().equals(PlayerType.COMPUTER)) {
        throw new IllegalArgumentException("Only one player can be of type Computer");
      }
      this.player2 = player;
      return this;
    }

    /**
     * Required method when using the Builder class to create a 
     * ConnectFourModel object.
     * This method is called after the Builder method and the optional method.
     * @return the ConnectFourModel object to be created.
     */
    public ConnectFourModel build() {
      return new ConnectFourModel(this);
    }
  }

  private ConnectFourModel(Builder builder) {
    observers = new ArrayList<ConnectFourObserver>();
    gameGrid = new ConnectFourColor[NUM_ROWS][NUM_COLUMNS];
    player1 = builder.player1;
    ConnectFourPlayerFactory playerFactory = new ConnectFourPlayerFactory();
    
    if (player1.getPlayerType().equals(PlayerType.COMPUTER) && 
        builder.player2 == null) {
      player2 = playerFactory.createPlayer(PlayerType.HUMAN, ConnectFourColor.RED); //ConnectFourHumanPlayer.getPlayer(ConnectFourColor.RED);
    } else {
      player2 = ((builder.player2 == null) ? 
    	   playerFactory.createPlayer(PlayerType.COMPUTER, ConnectFourColor.BLACK)
          : builder.player2);
    }

    playerTurn = (player1.getPlayerType().equals(PlayerType.COMPUTER)) ?
        player2 : player1;
    initBoard();
  }

  /**
   * Initializes all board spaces to the color white.
   */
  private void initBoard() {
    for (int i = 0; i < NUM_ROWS; i++) {
      for (int j = 0; j < NUM_COLUMNS; j++) {
        gameGrid[i][j] = ConnectFourColor.WHITE;
      }
    }
  }

  @Override
  public void playerMove(int columnNum) {
    if (columnNum < 0 || columnNum > 6) {
      throw new IllegalArgumentException(
          "Column number must be between 0 and 6 (inclusive)");
    }
    int rowAvailable = findEmptyRow(columnNum);
    if (rowAvailable != -1) {
      if (notifyObserversBoardUpdate(rowAvailable, columnNum)) {
        return;
      }
    }
    playerTurn = (playerTurn.equals(player1)) ? player2 : player1;
    if (playerTurn.getPlayerType().equals(PlayerType.COMPUTER)) {
      ConnectFourPosition position = computerPlayerMove();
      if (notifyObserversBoardUpdate(position.row, position.column)) {
        return;
      }
      playerTurn = (playerTurn.equals(player1)) ? player2 : player1;
    }
  }

  /**
   * Finds the first empty row in the specified column to place
   * a piece for the player whose turn it is.
   * @param column the column to place a piece.
   * @return the first available row or -1 if their is no empty row
   * in the column.
   * @throws IllegalArgumentException if the column is less than 0
   * or greater than 6.
   */
  int findEmptyRow(int column) {
    if (column < 0 || column > 6) {
      throw new IllegalArgumentException(
          "Column index must be between 0 and 6 (inclusive)");
    }
    int rowAvailable = -1;
    for (int row = NUM_ROWS - 1; row >=0; row--) {
      if (gameGrid[row][column].equals(ConnectFourColor.WHITE)) {
        gameGrid[row][column] = playerTurn.getColor();
        rowAvailable = row;
        break;
      }
    }  
    return rowAvailable;
  }

  /**
   * Checks in each direction around the specified row and column
   * to see if their are three additional pieces of the same color.
   * @param row the row from which to check.
   * @param column the column from which to check.
   * @return true if there are four pieces of the same color in a row.
   * False otherwise.
   * @throws IllegalArgumentException if the column is less than 0
   * or greater than 6, or if the row is less than 0 or greater than 5.
   */
  boolean checkWin(int row, int column) {
    if (row < 0 || row > 5) {
      throw new IllegalArgumentException(
          "row index must be between 0 and 5 (inclusive)");
    }
    if (column < 0 || column > 6) {
      throw new IllegalArgumentException(
          "Column index must be between 0 and 6 (inclusive)");
    }
    ConnectFourColor pieceColor = playerTurn.getColor();
    if ((column - 1 > -1) && (column - 2 > -1) && (column - 3 > -1)) {
      if (gameGrid[row][column - 1].equals(pieceColor) &&
          gameGrid[row][column - 2].equals(pieceColor) &&
          gameGrid[row][column - 3].equals(pieceColor)) {
          return true;
      }
    }
    //check to bottom of [row][column]
    if ((row + 1 < 6) && (row + 2 < 6) && (row + 3 < 6)) {
      if (gameGrid[row + 1][column].equals(pieceColor) &&
          gameGrid[row + 2][column].equals(pieceColor) &&
          gameGrid[row + 3][column].equals(pieceColor)) {
          return true;
      }
    }
    //check to right of [row][column]
    if ((column + 1 < 7) && (column + 2 < 7) && (column + 3 < 7)) {
      if (gameGrid[row][column + 1].equals(pieceColor) &&
          gameGrid[row][column + 2].equals(pieceColor) &&
          gameGrid[row][column + 3].equals(pieceColor)) {
          return true;
      }
    }
    //check top of [row][column]
    if ((row - 1 > -1) && (row - 2 > -1) && (row - 3 > -1)) {
      if (gameGrid[row - 1][column].equals(pieceColor) &&
          gameGrid[row - 2][column].equals(pieceColor) &&
          gameGrid[row - 3][column].equals(pieceColor)) {
          return true;
      }
    }
    //diagonal up left.
    if ((column - 1 > -1) && (column - 2 > -1) && (column - 3 > -1) &&
        (row - 1 > -1) && (row - 2 > -1) && (row - 3 > -1)) {
      if (gameGrid[row - 1][column - 1].equals(pieceColor) &&
          gameGrid[row - 2][column - 2].equals(pieceColor) &&
          gameGrid[row - 3][column - 3].equals(pieceColor)) {
          return true;
      }
    }
    //diagonal down left
    if ((column - 1 > -1) && (column - 2 > -1) && (column - 3 > -1) &&
        (row + 1 < 6) && (row + 2 < 6) && (row + 3 < 6)) {
      if (gameGrid[row + 1][column - 1].equals(pieceColor) &&
          gameGrid[row + 2][column - 2].equals(pieceColor) &&
          gameGrid[row + 3][column - 3].equals(pieceColor)) {
          return true;
      }
    }
    //diagonal up right
    if ((column + 1 < 7) && (column + 2 < 7) && (column + 3 < 7) &&
        (row - 1 > -1) && (row - 2 > -1) && (row - 3 > -1)) {
      if (gameGrid[row - 1][column + 1].equals(pieceColor) &&
          gameGrid[row - 2][column + 2].equals(pieceColor) &&
          gameGrid[row - 3][column + 3].equals(pieceColor)) {
          return true;
      }
    }
    //diagonal down right
    if ((column + 1 < 7) && (column + 2 < 7) && (column + 3 < 7) &&
        (row + 1 < 6) && (row + 2 < 6) && (row + 3 < 6)) {
      if (gameGrid[row + 1][column + 1].equals(pieceColor) &&
          gameGrid[row + 2][column + 2].equals(pieceColor) &&
          gameGrid[row + 3][column + 3].equals(pieceColor)) {
          return true;
      }
    }
    return false;
  }

  /**
   * Notifies any registered observers when the view of the board needs
   * to be updated or when the game has ended in a win or tie.
   * @param row the row to be updated.
   * @param column the column to be updated.
   * @return true if the game is over, false otherwise.
   * @throws IllegalArgumentException if the column is less than 0
   * or greater than 6, or if the row is less than 0 or greater than 5.
   */
  boolean notifyObserversBoardUpdate(int row, int column) {
    if (row < 0 || row > 5) {
      throw new IllegalArgumentException(
          "row index must be between 0 and 5 (inclusive)");
    }
    if (column < 0 || column > 6) {
      throw new IllegalArgumentException(
          "Column index must be between 0 and 6 (inclusive)");
    }
    if (checkWin(row, column)) {
      notifyObserversUpdateBoardDisplay(row, column, playerTurn.getColor());
      notifyObserversGameOver();
      return true;
    }
    notifyObserversUpdateBoardDisplay(row, column, playerTurn.getColor());
    if (checkFullBoard()) {
      notifyObserversBoardFull();
      return true;
    }
    return false;
  }

  /**
   * When the computer player has three in a row, this method checks to see
   * if there is an available move to get four in a row.
   * @param columnNum the column to find the first available row, if
   * it exists.
   * @param compColor the computer player color.
   * @return the row and column for the computer player to place a piece.
   * Both are -1 if there is no move to make.
   * @throws IllegalArgumentException if the column is less than 0
   * or greater than 6.
   */
  private ConnectFourPosition checkForWinningComputerMove(
      int columnNum, ConnectFourColor compColor) {
    if (columnNum < 0 || columnNum > 6) {
      throw new IllegalArgumentException(
          "Column index must be between 0 and 6 (inclusive)");
    }
    int row = findEmptyRow(columnNum);
    if (row != -1) {
      gameGrid[row][columnNum] = compColor;
      if (checkWin(row, columnNum)) {
        notifyObserversGameOver();
        return new ConnectFourPosition(row, columnNum);
      }
    }
    return new ConnectFourPosition();
  }

  /**
   * This method is used by the computer player to check if there are some
   * number of pieces in a row that the computer should add to or block.
   * @param columnNum the column to find the first available row, if
   * it exists. 
   * @param compColor the computer player color.
   * @return the row and column for the computer player to place a piece.
   * Both are -1 if there is no move to make.
   * IllegalArgumentException if the column is less than 0
   * or greater than 6.
   */
  private ConnectFourPosition checkXInARow(
      int columnNum, ConnectFourColor compColor) {
    if (columnNum < 0 || columnNum > 6) {
      throw new IllegalArgumentException(
          "Column index must be between 0 and 6 (inclusive)");
    }
    int row = findEmptyRow(columnNum);
    if (row != -1) {
      gameGrid[row][columnNum] = compColor;
      return new ConnectFourPosition(row, columnNum);
    }
    return new ConnectFourPosition();
  }

  /**
   * Finds the appropriate move for the computer player to make.
   * Will make a winning move if it is available. Will block a winning
   * move if the opponent has one.
   * Will get three in a row if it is available. Will block the opponent
   * from getting three in a row.
   * Otherwise will make a random move.
   * @return the row and column for the computer player to place a piece.
   */
  ConnectFourPosition computerPlayerMove() {
    int columnNum = -1;
    ConnectFourColor compColor = playerTurn.getColor();
    ConnectFourColor humanColor = (playerTurn.equals(player1)) ? 
        player2.getColor() : player1.getColor();
    columnNum = checkXInARowVertical(3, compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkForWinningComputerMove(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowHorizontal(3, compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkForWinningComputerMove(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    //check 3 horizontal with empty middle
    columnNum = checkThreeHorizontalWithMiddleEmpty(compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkForWinningComputerMove(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowPositiveDiagonal(3, compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkForWinningComputerMove(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkThreePosDiagonalWithMiddleEmpty(compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkForWinningComputerMove(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowNegativeDiagonal(3, compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkForWinningComputerMove(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkThreeNegDiagonalWithMiddleEmpty(compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkForWinningComputerMove(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowVertical(3, humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowHorizontal(3, humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkThreeHorizontalWithMiddleEmpty(humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowPositiveDiagonal(3, humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkThreePosDiagonalWithMiddleEmpty(humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowNegativeDiagonal(3, humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkThreeNegDiagonalWithMiddleEmpty(humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowVertical(2, compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowVertical(2, humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowHorizontal(2, compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowHorizontal(2, humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowPositiveDiagonal(2, compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowPositiveDiagonal(2, humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowNegativeDiagonal(2, compColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    columnNum = checkXInARowNegativeDiagonal(2, humanColor);
    if (columnNum != -1) {
      ConnectFourPosition position = checkXInARow(columnNum, compColor);
      if (position.column != -1) {
        return position;
      }
    }
    //get random column number
    Random rand = new Random();
    columnNum = rand.nextInt(7);
    int row = findEmptyRow(columnNum);
    return new ConnectFourPosition(row, columnNum);
  }

  /**
   * Check if the board is completely full.
   * @return true if the board is full. False otherwise.
   */
  boolean checkFullBoard() {
    int rowZero = 0;
    boolean full = true;
    for (int col = 0; col < NUM_COLUMNS; col++) {
      if (gameGrid[rowZero][col].equals(ConnectFourColor.WHITE)){
        full = false;
        break;
      }
    }
    return full;
  }

  /**
   * Completely clear the board to an empty one.
   */
  void clearBoard() {
    initBoard();
  }

  /**
   * Check if there are X pieces of the same color in a row
   * vertically and if the next piece is white.
   * @param count the number of pieces of the same color to find.
   * @param color the color of the pieces to be searched.
   * @return the column index where there are X pieces in a row. 
   * -1 if not found.
   * @throws IllegalArgumentException if count is less than 1.
   */
  int checkXInARowVertical(int count, ConnectFourColor color) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    int middleRow = 3;
    for (int col = 0; col < NUM_COLUMNS; col++) {
      for (int row = NUM_ROWS - 1; row > middleRow - 1; row--) {
        int xInARow = 0;
        for (int c = 0; c < count; c++) {
          if (gameGrid[row - c][col].equals(color)) {
            xInARow += 1;
          }
        }
        if (xInARow == count && gameGrid[row - count][col].equals(
            ConnectFourColor.WHITE)) {
          return col;
        }
      }
    }
    return -1;
  }

  /**
   * Check if there are X pieces of the same color in a row
   * horizontally and if the piece before or after is white
   * and the spaces below, if any, are taken.
   * @param count the number of pieces of the same color to find.
   * @param color the color of the pieces to be searched.
   * @return the column before or after the X pieces if it is
   * available to add 1 to X. -1 otherwise.
   * @throws IllegalArgumentException if count is less than 1.
   */
  int checkXInARowHorizontal(int count, ConnectFourColor color) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    int middleCol = 4;
    for (int row = NUM_ROWS - 1; row > 0; row--) {
      for (int col = 0; col < middleCol; col++) {
        int xInARow = 0;
        for (int c = 0; c < count; c++) {
          if (gameGrid[row][col + c].equals(color) ) {
            xInARow += 1;
          }
        }
        if (xInARow == count) { 
          if (gameGrid[row][col + count].equals(ConnectFourColor.WHITE)) {
            if (row + 1 < NUM_ROWS) {
              if (!gameGrid[row + 1][col + count].equals(
                  ConnectFourColor.WHITE)) {
                return col + count;
              }
            } else {
              return col + count;
            }
          }
          if (col > 0 && gameGrid[row][col - 1].equals(
              ConnectFourColor.WHITE)) {
            if (row + 1 < NUM_ROWS) {
              if (!gameGrid[row + 1][col - 1].equals(
                  ConnectFourColor.WHITE)) {
                return col - 1;
              }
            } else {
              return col - 1;
            }
          }
        }
      }
    }
    return -1;
  }

  /**
   * Check if there are four pieces in a row horizontally 
   * where one is white and the other three are of the same color,
   * and any spaces below the white
   * one are taken.
   * @param color the color of the pieces to be searched.
   * @return the column of the white space. -1 if it does not exist.
   */
  int checkThreeHorizontalWithMiddleEmpty(ConnectFourColor color) {
    int middleCol = 4;
    int piecesNeeded = 4;
    for (int row = NUM_ROWS - 1; row > 0; row--) {
      for (int col = 0; col < middleCol; col++) {
        int xInARow = 0;
        int emptySpot = 0;
        int emptySpotCol = -1;
        for (int x = 0; x < piecesNeeded; x++) {
          if (gameGrid[row][col + x].equals(color)) {
            xInARow += 1;
          } else if(gameGrid[row][col + x].equals(ConnectFourColor.WHITE)) {
            if (row + 1 < NUM_ROWS) {
              if(!gameGrid[row + 1][col + x].equals(ConnectFourColor.WHITE)) {
                emptySpot += 1;
                emptySpotCol = col + x;
                if (emptySpot > 1) {
                  break;
                }
              }
            } else {
              emptySpot += 1;
              emptySpotCol = col + x;
              if (emptySpot > 1) {
                break;
              }
            }
          }
        }
        if (xInARow == 3 && emptySpot == 1 && emptySpotCol != -1) {
          return emptySpotCol;
        }  
      }
    }
    return -1;
  }

  /**
   * Check if there are X pieces of the same color in a row
   * diagonally in a positive slope direction, and if the 
   * piece before or after is white and the spaces below, if any, 
   * are taken.
   * @param count the number of pieces of the same color to find.
   * @param color the color of the pieces to be searched.
   * @return the column before or after the X pieces if it is
   * available to add 1 to X. -1 otherwise.
   * @throws IllegalArgumentException if count is less than 1.
   */
  int checkXInARowPositiveDiagonal(int count, ConnectFourColor color) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    for (int col = 0; col < NUM_COLUMNS; col++) {
      for (int row = NUM_ROWS - 1; row >= 0; row--) {
        int xInARow = 0;
        for (int c = 0; c < count; c++) {
          if (row - c < 0 || col + c > NUM_COLUMNS - 1) {
            break;
          }
          if (gameGrid[row - c][col + c].equals(color)) {
            xInARow += 1;
          }
        }
        if (xInARow == count) {
          if (row - count > -1 && col + count < NUM_COLUMNS) {
            if (!(gameGrid[row - count + 1][col + count].
                equals(ConnectFourColor.WHITE)) && 
                gameGrid[row - count][col + count].
                equals(ConnectFourColor.WHITE)) {
              return col + count;
            }
          }
          if (col > 0 && row < NUM_ROWS - 1 && gameGrid[row + 1][col - 1].
              equals(ConnectFourColor.WHITE)) {
            if (row + 2 < NUM_ROWS) {
              if (!gameGrid[row + 2][col - 1].
                  equals(ConnectFourColor.WHITE)) {
                return col - 1;
              }
            } else {
              return col - 1;
            }
          }
        }
      }
    }
    return -1;
  }

  /**
   * Check if there are four pieces in a row diagonally, in a positive
   * slope direction, where one is white and the
   * other three are of the same color, and any spaces below the white
   * one are taken.
   * @param color the color of the pieces to be searched.
   * @return the column of the white space. -1 if it does not exist.
   */
  int checkThreePosDiagonalWithMiddleEmpty(ConnectFourColor color) {
    int piecesNeeded = 4;
    for (int col = 0; col < NUM_COLUMNS; col++) {
      for (int row = NUM_ROWS - 1; row >= 0; row--) {
        int xInARow = 0;
        int emptySpot = 0;
        int emptySpotCol = -1;
        for (int x = 0; x < piecesNeeded; x++) {
          if (row - x < 0 || col + x > NUM_COLUMNS - 1) {
            break;
          }
          if (gameGrid[row - x][col + x].equals(color)) {
            xInARow += 1;
          } else if (gameGrid[row - x][col + x].equals(ConnectFourColor.WHITE)) {
            if (row - x + 1 < NUM_ROWS) {
              if (!gameGrid[row - x + 1][col + x].equals(ConnectFourColor.WHITE)) {
                emptySpot += 1;
                emptySpotCol = col + x;
                if (emptySpot > 1) {
                  break;
                } 
              }
            } else {
              emptySpot += 1;
              emptySpotCol = col + x;
              if (emptySpot > 1) {
                break;
              }
            }
          }
        }
        if (xInARow == 3 && emptySpot == 1 && emptySpotCol != -1) {
          return emptySpotCol;
        }
      }
    }
    return -1;
  }

  /**
   * Check if there are X pieces of the same color in a row
   * diagonally in a negative slope direction, and if the 
   * piece before or after is white and the spaces below, if any, 
   * are taken.
   * @param count the number of pieces of the same color to find.
   * @param color the color of the pieces to be searched.
   * @return the column before or after the X pieces if it is
   * available to add 1 to X. -1 otherwise.
   * @throws IllegalArgumentException if count is less than 1.
   */
  int checkXInARowNegativeDiagonal(int count, ConnectFourColor color) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    for (int col = 0; col < NUM_COLUMNS; col++) {
      for (int row = 0; row < NUM_ROWS; row++) {
        int xInARow = 0;
        for (int c = 0; c < count; c++) {
          if (row + c > NUM_ROWS - 1 || col + c > NUM_COLUMNS - 1) {
            break;
          }
          if (gameGrid[row + c][col + c].equals(color)) {
            xInARow += 1;
          }
        }
        if (xInARow == count) {
          if (row + count < NUM_ROWS && col + count < NUM_COLUMNS) {
            if (row + count + 1 < NUM_ROWS) {
              if (!gameGrid[row + count + 1][col + count].
                  equals(ConnectFourColor.WHITE) && 
                  gameGrid[row + count][col + count].
                  equals(ConnectFourColor.WHITE)) {
                return col + count; 
              }
            } else {
              if (gameGrid[row + count][col + count].
                  equals(ConnectFourColor.WHITE)) {
                return col + count; 
              }
            }
          } 
          if (row > 0 && col > 0 && !gameGrid[row][col - 1].
              equals(ConnectFourColor.WHITE) && 
              gameGrid[row - 1][col - 1].
              equals(ConnectFourColor.WHITE)) {
            return col - 1;
          }
        }
      }
    }
    return -1;
  }

  /**
   * Check if there are four pieces in a row diagonally, in a negative
   * slope direction, where one is white and the
   * other three are of the same color, and any spaces below the white
   * one are taken.
   * @param color the color of the pieces to be searched.
   * @return the column of the white space. -1 if it does not exist.
   */
  int checkThreeNegDiagonalWithMiddleEmpty(ConnectFourColor color) {
    int piecesNeeded = 4;
    for (int col = 0; col < NUM_COLUMNS; col++) {
      for (int row = 0; row < NUM_ROWS; row++) {
        int xInARow = 0;
        int emptySpot = 0;
        int emptySpotCol = -1;
        for (int x = 0; x < piecesNeeded; x++) {
          if (row + x > NUM_ROWS - 1 || col + x > NUM_COLUMNS - 1) {
            break;
          }
          if (gameGrid[row + x][col + x].equals(color)) {
            xInARow += 1;
          } else if (gameGrid[row + x][col + x].equals(ConnectFourColor.WHITE)) {
            if (row + x + 1 < NUM_ROWS) { 
              if (!gameGrid[row + x + 1][col + x].equals(ConnectFourColor.WHITE)) {
                emptySpot += 1;
                emptySpotCol = col + x;
                if (emptySpot > 1) {
                  break;
                }   
              }
            } else {
              emptySpot += 1;
              emptySpotCol = col + x;
              if (emptySpot > 1) {
                break;
              } 
            }
          }
        }
        if (xInARow == 3 && emptySpot == 1 && emptySpotCol != -1) {
          return emptySpotCol;
        }
      }
    }
    return -1;
  }

  @Override
  public void registerObserver(ConnectFourObserver o) {
    observers.add(o);
  }

  @Override
  public void removeObserver(ConnectFourObserver o) {
    observers.remove(o);
  }

  @Override
  public void notifyObserversGameOver() {
    ConnectFourPlayer winner;
    if (playerTurn.equals(player1)) {
      winner = player1;
    } else {
      winner = player2;
    }
    for (ConnectFourObserver observer : observers) {
      observer.gameOver(winner.getColor());
    }
  }

  @Override
  public void notifyObserversUpdateBoardDisplay(int row, int column, 
      ConnectFourColor playerTurnColor) {
    Color color;
    if (playerTurnColor.equals(ConnectFourColor.RED)) {
      color = Color.RED; 
    } else {
      color = Color.BLACK;
    }
    for (ConnectFourObserver observer : observers) {
      observer.updateBoardDisplay(row, column, color);
    }
  }

  @Override
  public void notifyObserversBoardFull() {
    for (ConnectFourObserver observer : observers) {
      observer.boardFull();
    }
  }

  /**
   * The {@code ConnectFourPosition} class represents a position
   * by row and column on the connect four board.
   * @author Eric
   *
   */
  class ConnectFourPosition {
    int row = -1;
    int column = -1;

    private ConnectFourPosition() { }

    private ConnectFourPosition(int row, int column) {
      this.row = row;
      this.column = column;
    }
  }
}