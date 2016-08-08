package edu.nyu.pqs.connectfour;

/**
 * The {@code ConnectFourPlayer} is an abstract class that represents
 * a player of the connect four game. The class provides two abstract
 * methods, getColor and getPlayerType.
 * @author Eric
 *
 */
public abstract class ConnectFourPlayer {

  protected ConnectFourPlayer() { }

  /**
   * Gets the piece color of the connect four player.
   * @return the piece color of the connect four player.
   */
  public abstract ConnectFourColor getColor();

  /**
   * Gets the PlayerType of the connect four player.
   * @return the PlayerType of the connect four player.
   */
  public abstract PlayerType getPlayerType();
}