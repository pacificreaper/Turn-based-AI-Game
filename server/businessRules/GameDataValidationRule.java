package server.businessRules;

import java.util.List;
import java.util.Map;

import server.data.PlayerID;
import server.exceptions.InvalidGameIDException;
import server.exceptions.InvalidPlayerIDException;
import server.gameCreation.GameID;
import server.gameLogic.Game;
import server.player.Player;

public class GameDataValidationRule implements IBusinessRule{
	
	@Override
	public void verifyPlayerID(List<Player> players, PlayerID playerid) throws InvalidPlayerIDException{
		if (!players.stream().anyMatch(player -> player.getPlayerID().equals(playerid)))
			throw new InvalidPlayerIDException(); 
	}
	
	@Override
	public void verifyGameID(GameID gameid, Map<GameID, Game> activeGames) throws InvalidGameIDException {
		if (!activeGames.containsKey(gameid))
			throw new InvalidGameIDException();
	};
}
