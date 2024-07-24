package server.businessRules;

import java.util.List;

import server.exceptions.TooManyPlayersException;
import server.player.Player;

public class PlayerCountRule implements IBusinessRule {
	private static final int MAX_NUMBER_OF_PLAYERS = 2;

	@Override
	public void verifyPlayerCount(List<Player> players) throws TooManyPlayersException { 
		if (players.size() >= MAX_NUMBER_OF_PLAYERS) { 
			throw new TooManyPlayersException();
		}
	}
	
}
