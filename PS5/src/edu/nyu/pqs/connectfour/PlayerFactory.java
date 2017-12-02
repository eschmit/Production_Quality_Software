package edu.nyu.pqs.connectfour;

public interface PlayerFactory {
	ConnectFourPlayer createPlayer(PlayerType playerType, ConnectFourColor color);
}
