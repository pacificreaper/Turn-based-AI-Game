package server.businessRules;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.data.PlayerID;
import server.exceptions.GameOverException;
import server.exceptions.IncompleteGameMapException;
import server.exceptions.InvalidGameIDException;
import server.exceptions.InvalidPlayerIDException;
import server.exceptions.PlayerActionException;
import server.exceptions.TooManyPlayersException;
import server.gameCreation.GameID;
import server.gameLogic.Game;
import server.map.MapField;
import server.player.Player;

public interface IBusinessRule {

	default public void verifyPlayerID(List<Player> players, PlayerID playerid) throws InvalidPlayerIDException {
	}

	default public void verifyGameID(GameID gameid, Map<GameID, Game> activeGames) throws InvalidGameIDException {
	}

	default public void verifyPlayerCount(List<Player> players) throws TooManyPlayersException {
	}

	default public void verifyPlayerTurn(Player player, Game game) throws PlayerActionException {
	}

	default public void validateMap(Collection<PlayerHalfMapNode> fields) throws GameOverException {
		// check if correct number of HalfMap fields
		// check if correct number of water/grass/mountain fields
		// check if correct number of forts
		// check if fort is placed on grass field
		// check for islands
		// check number of accessible edge fields
	}

	default public void checkIfPlayerAlreadySentHalfMap(Player player, Game game) throws PlayerActionException {
	}

	default public void checkActiveGamesAndMakeSpaceForNewGame(Map<GameID, Game> activeGames) {
	}

	default public void checkIfBothPlayersHaveRegistered(List<Player> registeredPlayers, Game game) throws PlayerActionException {
	}

	default public void checkIfCompleteMapAvailable(Set<MapField> fields) throws IncompleteGameMapException {
	}

	default public void checkNumberOfRounds(Game game) throws GameOverException {
	}

	default public void checkTimePassedSinceLastAction(Game game, Player player) throws GameOverException {
	}

	default public void checkGameDuration(Game game) throws GameOverException {
	}

	default public void checkIfPlayerAlreadyWonOrLost(Player player, Game game) throws PlayerActionException {
	}

	default public void checkDestinationNodeValidity(Game game, Optional<MapField> destinationField) throws GameOverException {
		// check if player moves outside borders
		// check if player wants to move onto water field
	}

}
