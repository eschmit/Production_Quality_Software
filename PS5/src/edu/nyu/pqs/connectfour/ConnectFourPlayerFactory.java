package edu.nyu.pqs.connectfour;

public class ConnectFourPlayerFactory implements PlayerFactory {
	public ConnectFourPlayer createPlayer(PlayerType playerType, ConnectFourColor color) {
		if (playerType.equals(PlayerType.COMPUTER)) {
			return ConnectFourComputerPlayer.getPlayer();
		} else {
			return ConnectFourHumanPlayer.getPlayer(color);
		}
	}
}
