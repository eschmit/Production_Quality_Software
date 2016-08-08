package edu.nyu.pqs.connectfour;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ConnectFourHumanPlayerTest {

  @Test
  public void testNumberOfHumanPlayers() {
    ConnectFourPlayer redPlayer1  = ConnectFourHumanPlayer.
        getHumanPlayer(ConnectFourColors.RED);
    ConnectFourPlayer blackPlayer1  = ConnectFourHumanPlayer.
        getHumanPlayer(ConnectFourColors.BLACK);
    ConnectFourPlayer redPlayer2  = ConnectFourHumanPlayer.
        getHumanPlayer(ConnectFourColors.RED);
    assertNotSame(redPlayer1, blackPlayer1);
    assertSame(redPlayer1,redPlayer2);
  }
}