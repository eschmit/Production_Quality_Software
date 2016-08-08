package edu.nyu.pqs.connectfour;

/**
 * The {@code ConnectFourSubject} interface provides a set of abstract
 * methods to be implemented by a concrete implementation of the
 * connect four game.
 * <p>
 * The {@code ConnectFourModel} class implements this interface.
 * @author Eric
 * @see ConnectFourModel
 *
 */
public interface ConnectFourSubject {

  /**
   * Registers an observer to listen for changes to
   * the {@code ConnectFourSubject}.
   * @param o the observer to be added to the list of 
   * observers held by the subject.
   */
  void registerObserver(ConnectFourObserver o);

  /**
   * Removes the specified observer, if it exists, from
   * the list of observers listening for changes to the
   * {@code ConnectFourSubject}.
   * @param o o the observer to be removed from the list of 
   * observers held by the subject.
   */
  void removeObserver(ConnectFourObserver o);

  /**
   * Notify all registered observers that the game is over as
   * a result of a win or tie.
   */
  void notifyObserversGameOver();

  /**
   * Notify all registered observers that the view needs to
   * be updated with the most recent player move.
   * @param row the row to be updated.
   * @param column the column to be updated.
   * @param playerTurn the color of the piece to be added to
   * the board.
   */
  void notifyObserversUpdateBoardDisplay(int row, int column, ConnectFourColor playerTurn);

  /**
   * Notify all registered observers that the board
   * is completely full.
   */
  void notifyObserversBoardFull();

  /**
   * Finds the first empty row in the specified column to place 
   * a piece for the player whose turn it is. 
   * Notifies any registered observers to update their displays.
   * <p>
   * If one of the two players is a computer player, makes a move
   * for the computer after the other player moves.
   * @param columnNum the column to place a piece.
   * @throws IllegalArgumentException if the column is less than 0 or
   * greater than 6.
   */
  void playerMove(int columnNum);
}