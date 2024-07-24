package server.businessRules;

import java.time.Duration;
import java.time.Instant;

import server.exceptions.GameOverException;
import server.gameLogic.Game;

public class GameDurationRule implements IBusinessRule{
	@Override
	public void checkGameDuration(Game game) throws GameOverException {
		Instant currentTime = Instant.now();
		Instant gameStartTime = game.getStartTime();
		long duration = Duration.between(gameStartTime, currentTime).toMillis();
		final int maximalGameDuration = 600000;
        if (duration > maximalGameDuration) {
        	throw new GameOverException("Game duration", "Game exceeded max duration of 10 minutes!", game);
        }
	}
}
