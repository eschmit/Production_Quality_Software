package edu.nyu.pqs.connectfour;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ConnectFourHumanPlayerTest {

  @Test
  public void testNumberOfHumanPlayers() {
	ConnectFourPlayerFactory playerFactory = new ConnectFourPlayerFactory();
    ConnectFourPlayer redPlayer1  = playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.RED);
    ConnectFourPlayer blackPlayer1  = playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.BLACK);
    
    ConnectFourPlayer redPlayer2  = playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.RED);
    assertNotSame(redPlayer1, blackPlayer1);
    assertSame(redPlayer1, redPlayer2);
  }
}