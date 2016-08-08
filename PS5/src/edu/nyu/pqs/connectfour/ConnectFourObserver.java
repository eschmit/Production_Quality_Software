package edu.nyu.pqs.connectfour;

import java.awt.Color;

/**
 * The {@code ConnectFourObserver} interface provides a set of abstract
 * methods to be implemented by a concrete implementation of a
 * connect four game view.
 * @author Eric
 *
 */
public interface ConnectFourObserver {
  
  /**
   * Updates the view of the connect four board
   * after a player has made a move.
   * @param row the row to be updated.
   * @param col the column to be updated.
   * @param color the color of the piece played.
   */
  void updateBoardDisplay(int row, int col, Color color);

  /**
   * Presents a menu stating that the game is over and which
   * color won.
   * @param winnerColor the color of the winning player.
   */
  void gameOver(ConnectFourColors winnerColor);

  /**
   * Presents a menu stating that the board is full and
   * the game is therefore a tie.
   */
  void boardFull();
}
