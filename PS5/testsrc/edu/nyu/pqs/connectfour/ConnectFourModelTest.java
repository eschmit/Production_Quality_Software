package edu.nyu.pqs.connectfour;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ConnectFourModelTest {
  private ConnectFourModel psuedoOnePlayerGame;
  private ConnectFourModel twoPlayerGame;
  private ConnectFourPlayerFactory playerFactory = new ConnectFourPlayerFactory();

  @Before
  public void setUp() {
    psuedoOnePlayerGame = new ConnectFourModel.Builder(playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.RED) )
            .secondPlayer(playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.BLACK)).build();
    twoPlayerGame = new ConnectFourModel.Builder(playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.RED) )
            .secondPlayer(playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.BLACK)).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConnectFour_nullParameter() {
    new ConnectFourModel.Builder(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConnectFour_nullSecondParameter() {
    new ConnectFourModel.Builder(playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.RED) )
    .secondPlayer(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConnectFour_sameColor() {
    new ConnectFourModel.Builder(playerFactory
            .createPlayer(PlayerType.HUMAN, ConnectFourColor.RED) ).
        secondPlayer(playerFactory
                .createPlayer(PlayerType.HUMAN, ConnectFourColor.RED) ).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConnectFour_twoComputerPlayers() {
    new ConnectFourModel.Builder(playerFactory
            .createPlayer(PlayerType.COMPUTER, ConnectFourColor.RED) ).
        secondPlayer(playerFactory
                .createPlayer(PlayerType.COMPUTER, ConnectFourColor.RED)).build();
  }

  @Test
  public void testFullBoard() {
    twoPlayerGameMoves(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1,
        2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4,
        5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6);
    assertTrue(twoPlayerGame.checkFullBoard());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayerMove_outOfLowerBounds() {
    twoPlayerGame.playerMove(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayerMove_outOfUpperBounds() {
    twoPlayerGame.playerMove(7);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindEmptyRow_outOfLowerBounds() {
    twoPlayerGame.findEmptyRow(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindEmptyRow_outOfUpperBounds() {
    twoPlayerGame.findEmptyRow(7);
  }

  @Test
  public void testFindEmptyRow() {
    assertEquals(5, twoPlayerGame.findEmptyRow(3));
    assertEquals(4, twoPlayerGame.findEmptyRow(3));
    assertEquals(3, twoPlayerGame.findEmptyRow(3));
    assertEquals(5, twoPlayerGame.findEmptyRow(0));
  }

  @Test
  public void testFindEmptyRow_fullBoard() {
    twoPlayerGameMoves(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1,
        2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4,
        5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6);
    assertTrue(twoPlayerGame.checkFullBoard());
    assertEquals(-1, twoPlayerGame.findEmptyRow(0));
    assertEquals(-1, twoPlayerGame.findEmptyRow(1));
    assertEquals(-1, twoPlayerGame.findEmptyRow(2));
    assertEquals(-1, twoPlayerGame.findEmptyRow(3));
    assertEquals(-1, twoPlayerGame.findEmptyRow(4));
    assertEquals(-1, twoPlayerGame.findEmptyRow(5));
    assertEquals(-1, twoPlayerGame.findEmptyRow(6));
  }

  @Test
  public void testCheckWin_emptyBoard() {
    assertFalse(twoPlayerGame.checkWin(2, 3));
  }

  @Test
  public void testCheckWin_oneInARow() {
    twoPlayerGameMoves(3);
    assertFalse(twoPlayerGame.checkWin(5, 3));
  }

  @Test
  public void testCheckWin_twoInARow() {
    twoPlayerGameMoves(3, 3, 4);
    assertFalse(twoPlayerGame.checkWin(5, 3));
  }

  @Test
  public void testCheckWin_threeInARow() {
    twoPlayerGameMoves(3, 3, 4, 4, 5);
    assertFalse(twoPlayerGame.checkWin(5, 3));
  }

  @Test
  public void testCheckWin_wrongBoardLocation() {
    twoPlayerGameMoves(3, 3, 4, 4, 5, 5, 6);
    assertFalse(twoPlayerGame.checkWin(4, 3));  
  }

  @Test
  public void testCheckWin_horizontal() {
    twoPlayerGameMoves(0, 0, 1, 1, 2, 2, 3);
    assertTrue(twoPlayerGame.checkWin(5, 0));
  }

  private void twoPlayerGameMoves(int...column) {
    for (int c : column) {
      twoPlayerGame.playerMove(c);
    }
  }

  @Test
  public void testCheckWin_vertical() {
    twoPlayerGameMoves(3, 4, 3, 4, 3, 4, 3);
    assertTrue(twoPlayerGame.checkWin(5, 3));
  }

  @Test
  public void testCheckWin_positiveDiagonal() {
    twoPlayerGameMoves(0, 1, 1, 2, 2, 3, 2, 3, 3, 0, 3);
    //diagonal up right   
    assertTrue(twoPlayerGame.checkWin(5, 0));
    //diagonal down left
    assertTrue(twoPlayerGame.checkWin(2, 3));
    //check wrong location
    assertFalse(twoPlayerGame.checkWin(5, 1));
    //check non-win
    twoPlayerGame.clearBoard();
    twoPlayerGameMoves(4, 5, 5, 6, 6, 0, 6);
    assertFalse(twoPlayerGame.checkWin(5, 4));
  }

  @Test
  public void testCheckWin_negativeDiagonal() {
    twoPlayerGameMoves(3, 3, 3, 3, 4, 4, 5, 4, 0, 5, 0, 6);
    //diagonal down right
    assertTrue(twoPlayerGame.checkWin(2, 3));
    //diagonal up left
    assertTrue(twoPlayerGame.checkWin(5, 6));
    //check wrong location
    assertFalse(twoPlayerGame.checkWin(3, 3));
  }

  @Test
  public void testCheckXInARowVertical() {
    twoPlayerGameMoves(3);
    assertEquals(3, twoPlayerGame.checkXInARowVertical(1, ConnectFourColor.RED));
    //check wrong column to test
    assertThat(2, not(equalTo(twoPlayerGame.checkXInARowVertical(1, ConnectFourColor.RED))));
    //check if two in a row. Returns -1 since false
    assertEquals(-1, twoPlayerGame.checkXInARowVertical(2, ConnectFourColor.RED));
    assertEquals(-1, twoPlayerGame.checkXInARowVertical(1, ConnectFourColor.BLACK));
    twoPlayerGameMoves(4, 3);
    assertEquals(3, twoPlayerGame.checkXInARowVertical(2, ConnectFourColor.RED));
    assertEquals(4, twoPlayerGame.checkXInARowVertical(1, ConnectFourColor.BLACK));
    twoPlayerGameMoves(4, 3);
    assertEquals(3, twoPlayerGame.checkXInARowVertical(3, ConnectFourColor.RED));
    assertEquals(4, twoPlayerGame.checkXInARowVertical(2, ConnectFourColor.BLACK));
  }

  @Test
  public void testCheckXInARowHorizontal() {
    twoPlayerGameMoves(0);
    assertEquals(1, twoPlayerGame.checkXInARowHorizontal(1, ConnectFourColor.RED));
    //check if two in a row. Returns -1 since false
    assertEquals(-1, twoPlayerGame.checkXInARowHorizontal(2, ConnectFourColor.RED));
    twoPlayerGameMoves(0, 1);
    assertEquals(2, twoPlayerGame.checkXInARowHorizontal(2, ConnectFourColor.RED));
    assertEquals(1, twoPlayerGame.checkXInARowHorizontal(1, ConnectFourColor.BLACK));
    twoPlayerGameMoves(1, 2);
    assertEquals(3, twoPlayerGame.checkXInARowHorizontal(3, ConnectFourColor.RED));
    assertEquals(2, twoPlayerGame.checkXInARowHorizontal(2, ConnectFourColor.BLACK));
  }

  @Test
  public void testCheckXInARowPositiveDiagonal() {
    twoPlayerGameMoves(3);
    assertEquals(-1, twoPlayerGame.checkXInARowPositiveDiagonal(1, ConnectFourColor.RED));
    assertEquals(-1, twoPlayerGame.checkXInARowPositiveDiagonal(1, ConnectFourColor.BLACK));
    twoPlayerGameMoves(4, 4);
    //Two red in a row but red can't make 3 in a row yet 
    assertEquals(-1, twoPlayerGame.checkXInARowPositiveDiagonal(2, ConnectFourColor.RED));
    twoPlayerGameMoves(5, 5, 3, 0);
    //Two red in a row for black to block
    assertEquals(5, twoPlayerGame.checkXInARowPositiveDiagonal(2, ConnectFourColor.RED));
  }

  @Test
  public void testCheckXInARowNegativeDiagonal() {
    twoPlayerGameMoves(3);
    assertEquals(-1, twoPlayerGame.checkXInARowNegativeDiagonal(1, ConnectFourColor.RED));
    twoPlayerGameMoves(2, 2);
    //Two red in a row but red can't make 3 in a row yet
    assertEquals(-1, twoPlayerGame.checkXInARowPositiveDiagonal(2, ConnectFourColor.RED));
    twoPlayerGameMoves(1, 1, 3, 6);
    //Two red in a row for black to block
    assertEquals(1, twoPlayerGame.checkXInARowNegativeDiagonal(2, ConnectFourColor.RED));
  }

  private void onePlayerGameMoves(int...column) {
    for (int c : column) {
      psuedoOnePlayerGame.playerMove(c);
    }
  }

  @Test
  public void testComputerPlayerMove_blockThreeInARowVertical() {
    psuedoOnePlayerGame.playerMove(3);
    //throw away black piece
    psuedoOnePlayerGame.playerMove(0);
    psuedoOnePlayerGame.playerMove(3);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(3, position.column);
  }

  @Test
  public void testComputerPlayerMove_blockThreeInARowHorizontal() {
    psuedoOnePlayerGame.playerMove(3);
    //throw away black piece
    psuedoOnePlayerGame.playerMove(0);
    psuedoOnePlayerGame.playerMove(4);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(5, position.column);
  }

  @Test
  public void testComputerPlayerMove_blockThreeInARowPositiveDiagonal() {
    onePlayerGameMoves(3, 4, 4, 5, 5);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(5,position.column);
  }

  @Test
  public void testComputerPlayerMove_blockThreeInARowNegativeDiagonal() {
    onePlayerGameMoves(3, 2, 3);
    //block two vertical red in a row
    psuedoOnePlayerGame.playerMove(3);
    onePlayerGameMoves(1, 2, 2);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(4, position.column);
    //extra black move to switch back to red
    psuedoOnePlayerGame.playerMove(6);
    onePlayerGameMoves(6, 1, 1);
    //block negative diagonal on other side
    position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(1, position.column);
  }

  @Test
  public void testComputerPlayerMove_blockFourInARowVertical() {
    psuedoOnePlayerGame.playerMove(3);
    //throw away black piece
    psuedoOnePlayerGame.playerMove(0);
    psuedoOnePlayerGame.playerMove(3);
    //throw away black piece
    psuedoOnePlayerGame.playerMove(0);
    psuedoOnePlayerGame.playerMove(3);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(3, position.column);
  }

  @Test
  public void testComputerPlayerMove_blockFourInARowHorizontal() {
    psuedoOnePlayerGame.playerMove(3);
    //throw away black piece
    psuedoOnePlayerGame.playerMove(0);
    psuedoOnePlayerGame.playerMove(4);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    //computer blocks 3 in a row
    assertEquals(5, position.column);
    //throw piece away to switch to player color
    psuedoOnePlayerGame.playerMove(6);
    psuedoOnePlayerGame.playerMove(2);
    position = psuedoOnePlayerGame.computerPlayerMove();
    //computer blocks 4 in a row
    assertEquals(1, position.column);
  }

  @Test
  public void testComputerPlayerMove_blockFourInARowHorizontalMiddle() {
    onePlayerGameMoves(2, 2, 4, 4, 5);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(3, position.column);
    onePlayerGameMoves(3, 5, 2, 2, 4, 4, 5);
    position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(3, position.column);  
  }
  
  @Test
  public void testComputerPlayerMove_blockFourInARowPositiveDiagonal() {
    onePlayerGameMoves(3, 4, 4, 5, 5, 3, 5, 5, 6, 6, 6);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(6, position.column);
    //block positive diagonal on other side
    psuedoOnePlayerGame.clearBoard();
    onePlayerGameMoves(3, 4, 3, 3, 5, 4, 4, 5, 5, 0, 5);
    position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(2, position.column);
  }
  
  @Test
  public void testComputerPlayerMove_blockFourInARowPositiveDiagonalMiddle() {
    onePlayerGameMoves(3, 5, 5, 4, 6, 6, 5, 5, 6, 2, 6);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(4, position.column);
  }

  @Test
  public void testComputerPlayerMove_blockFourInARowNegativeDiagonal() {
    onePlayerGameMoves(3, 2, 3, 3, 1, 2, 2, 1, 1, 6, 1);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(4, position.column);
    //block negative diagonal on other side
    psuedoOnePlayerGame.clearBoard();
    onePlayerGameMoves(3, 2, 3, 3, 1, 2, 2, 1, 1, 6, 4);
    position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(1, position.column);
  }

  @Test
  public void testComputerPlayerMove_blockFourInARowNegativeDiagonalMiddle() {
    onePlayerGameMoves(3, 1, 1, 2, 0, 0, 1, 1, 0, 5, 0);
    ConnectFourModel.ConnectFourPosition position = psuedoOnePlayerGame.computerPlayerMove();
    assertEquals(2, position.column);
  }
}
