package server.businessRules;

import server.exceptions.GameOverException;
import server.gameLogic.Game;

public class RoundCountRule implements IBusinessRule{
	private static final int MAX_NUMBER_OF_ROUNDS = 320;
	@Override 
	public void checkNumberOfRounds(Game game) throws GameOverException {
		int rounds = game.getRound();
		if (++rounds > MAX_NUMBER_OF_ROUNDS) {
			throw new GameOverException("Max rounds reached", "A game is only allowed to last up to 320 rounds!", game);
		}
	}
}
