package edu.nyu.pqs.connectfour;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ConnectFourComputerPlayerTest {

  @Test
  public void testComputerPlayerSingleton() {
    ConnectFourPlayer compPlayer1 = ConnectFourComputerPlayer.getComputerPlayer();
    ConnectFourPlayer compPlayer2 = ConnectFourComputerPlayer.getComputerPlayer();
    assertSame(compPlayer1, compPlayer2);
  }
}
