package server.exceptions;

import java.util.Optional;

import server.gameLogic.Game;
import server.player.EPlayerStatus;
import server.player.Player;

public class GameOverException extends GenericExampleException{

	public GameOverException(String errorName, String errorMessage, Game game) {
		super(errorName, errorMessage);
		Optional<Player> loser = game.getRegisteredPlayers().stream().filter(player -> player.getStatus() == EPlayerStatus.MustAct).findAny();
		Optional<Player> winner = game.getRegisteredPlayers().stream().filter(player -> player.getStatus() == EPlayerStatus.MustWait).findAny();
		if (!loser.isEmpty() && !winner.isEmpty())
			game.playerLoses(loser.get(), winner.get());
	}

}
