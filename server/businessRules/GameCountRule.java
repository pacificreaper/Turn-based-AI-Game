package server.businessRules;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import server.gameCreation.GameID;
import server.gameLogic.Game;
import server.player.EPlayerStatus;
import server.player.Player;

public class GameCountRule implements IBusinessRule {
	private static final int MAX_NUMBER_OF_GAMES = 99;

	@Override
	public void checkActiveGamesAndMakeSpaceForNewGame(Map<GameID, Game> activeGames) {
		if (activeGames.size() >= MAX_NUMBER_OF_GAMES) {
			Game oldestGame = activeGames.values().stream().min(Comparator.comparing(Game::getStartTime)).get();
			List<Player> players = oldestGame.getRegisteredPlayers();
			boolean playerTurnsHaveBeenSet = players.stream().anyMatch(player -> player.getStatus() == EPlayerStatus.MustAct);
			if (playerTurnsHaveBeenSet) {
				Player loser = players.stream().filter(player -> player.getStatus() == EPlayerStatus.MustAct)
						.findFirst().get();
				Player winner = oldestGame.getOtherPlayer(loser.getPlayerID());
				oldestGame.playerLoses(loser, winner);
			}
			activeGames.remove(oldestGame.getGameid());
		}
	}
}
