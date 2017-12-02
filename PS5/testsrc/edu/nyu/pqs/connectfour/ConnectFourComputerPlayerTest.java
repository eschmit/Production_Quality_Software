package edu.nyu.pqs.connectfour;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ConnectFourComputerPlayerTest {

  @Test
  public void testComputerPlayerSingleton() {
    ConnectFourPlayerFactory playerFactory = new ConnectFourPlayerFactory();
    ConnectFourPlayer compPlayer1 = playerFactory
            .createPlayer(PlayerType.COMPUTER, ConnectFourColor.BLACK);
    ConnectFourPlayer compPlayer2 = playerFactory
            .createPlayer(PlayerType.COMPUTER, ConnectFourColor.BLACK);
    assertSame(compPlayer1, compPlayer2);
  }
}