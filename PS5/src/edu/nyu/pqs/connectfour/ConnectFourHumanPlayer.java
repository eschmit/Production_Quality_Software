package edu.nyu.pqs.connectfour;

/**
 * The {@code ConnectFourHumanPlayer} class extends the
 * {@code ConnectFourPlayer} class and provides a concrete
 * implementation.
 * <p>
 * ConnectFourHumanPlayers are of {@code PlayerType} Human
 * and contain one of two ConnectFourColors.
 * <p>
 * Only two ConnectFourHumanPlayers can be instantiated, one
 * black and one red.
 * @author Eric
 * @see ConnectFourPlayer
 * @see ConnectFourColors
 * @see PlayerType
 * 
 */
public class ConnectFourHumanPlayer extends ConnectFourPlayer {
  private static final PlayerType type = PlayerType.HUMAN;
  private final ConnectFourColors color;
  private static ConnectFourPlayer player1;
  private static ConnectFourPlayer player2;
		  
  private ConnectFourHumanPlayer(ConnectFourColors color) {
    super();
    this.color = color;
  }

  /**
   * A static factory method to create and return a 
   * {@code ConnectFourHumanPlayer} object. Only two 
   * ConnectFourHumanPlayers can be instantiated, one
   * black and one red. After that, the same instances will
   * be returned when requested.
   * @param color the color of the player.
   * @return a ConnectFourPlayer object.
   */
  public static ConnectFourPlayer getHumanPlayer(ConnectFourColors color) {
    if (player1 == null) {
      player1 = new ConnectFourHumanPlayer(color);
      return player1;
    }
    if (player1.getColor().equals(color)) {
      return player1;
    }
    if (player2 == null) {
      player2 = new ConnectFourHumanPlayer(color);
      return player2;
    }
    return player2;
  }

  @Override 
  public ConnectFourColors getColor() {
    return color;
  }

  @Override
  public PlayerType getPlayerType() {
    return type;
  }
}
