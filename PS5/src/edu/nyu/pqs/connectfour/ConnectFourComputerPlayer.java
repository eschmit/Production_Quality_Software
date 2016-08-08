package edu.nyu.pqs.connectfour;

/**
 * The {@code ConnectFourComputerPlayer} class extends the
 * {@code ConnectFourPlayer} class and provides a concrete
 * implementation.
 * <p>
 * This class is a singleton and only one instance of it
 * can be instantiated.
 * @author Eric
 * @see ConnectFourPlayer
 */
public class ConnectFourComputerPlayer extends ConnectFourPlayer {
  private static final PlayerType type = PlayerType.COMPUTER;
  private static final ConnectFourColor color = ConnectFourColor.BLACK;
  private static final ConnectFourComputerPlayer INSTANCE =
      new ConnectFourComputerPlayer();

  private ConnectFourComputerPlayer() {
    super();
  }

  /**
   * A static factory method that returns the
   * {@code ConnectFourPlayer} object.
   * @return
   */
  public static ConnectFourPlayer getComputerPlayer() {
    return INSTANCE;
  }

  @Override
  public ConnectFourColor getColor() {
    return color;
  }

  @Override
  public PlayerType getPlayerType() {
    return type;
  }
}