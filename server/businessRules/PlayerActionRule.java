package server.businessRules;

import java.util.List;

import server.exceptions.PlayerActionException;
import server.gameLogic.Game;
import server.player.EPlayerStatus;
import server.player.Player;

public class PlayerActionRule implements IBusinessRule {

	@Override
	public void verifyPlayerTurn(Player player, Game game) throws PlayerActionException {
			if (player.getStatus() == EPlayerStatus.MustWait)
				throw new PlayerActionException("Player out of turn",
						"Player failed to adhere to assigned turn and took an action when it was not their turn!");
	}

	@Override
	public void checkIfPlayerAlreadySentHalfMap(Player player, Game game) throws PlayerActionException {
		if (player.hasSentHalfMap())
			throw new PlayerActionException("Multiple HalfMaps",
					"Player tried to send another HalfMap, even though they have already sent one before!");
	}

	@Override
	public void checkIfBothPlayersHaveRegistered(List<Player> registeredPlayers, Game game) throws PlayerActionException {
			if (registeredPlayers.size() != 2)
				throw new PlayerActionException("Map exchange too early",
						"Both players must be registered for the game before sending a HalfMap is allowed!");
	}

	@Override
	public void checkIfPlayerAlreadyWonOrLost(Player player, Game game) throws PlayerActionException {
		if (player.getStatus() == EPlayerStatus.Lost || player.getStatus() == EPlayerStatus.Won)
			throw new PlayerActionException("Game already over",
					"Player tried to send an action even though the game has already ended!");
	}

}
