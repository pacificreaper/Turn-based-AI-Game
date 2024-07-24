package server.businessRules;

import java.time.Duration;
import java.time.Instant;

import server.exceptions.GameOverException;
import server.gameLogic.Game;
import server.player.Player;

public class ActionTimeRule implements IBusinessRule{
	@Override
	public void checkTimePassedSinceLastAction(Game game, Player player) throws GameOverException {
		Instant currentTime = Instant.now();
		Instant timeSinceLastAction = player.getTimeSinceLastAction();
		long duration = Duration.between(timeSinceLastAction, currentTime).toMillis();
		if (duration > 5000) {
			throw new GameOverException("Action time limit", "Player took longer than 5 seconds to send an action! Time taken was: " + duration, game);
		}
			
	} 
}
