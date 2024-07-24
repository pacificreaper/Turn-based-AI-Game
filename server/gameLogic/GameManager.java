package server.gameLogic;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import server.businessRules.GameCountRule;
import server.businessRules.GameDataValidationRule;
import server.businessRules.GameDurationRule;
import server.businessRules.IBusinessRule;
import server.businessRules.RoundCountRule;
import server.exceptions.GameOverException;
import server.exceptions.GenericExampleException;
import server.exceptions.InvalidGameIDException;
import server.gameCreation.GameID;

public class GameManager {
	private static Map<GameID, Game> activeGames;
	private List<IBusinessRule> businessRules;
	private final static Logger logger = LoggerFactory.getLogger(GameManager.class);
	
	public GameManager() {
		GameManager.activeGames = new HashMap<>();
		this.businessRules = new ArrayList<>(Arrays.asList(
				new GameDataValidationRule(),
				new RoundCountRule(),
				new GameDurationRule()));
	}
	
	public void addGame(Game game) {
		IBusinessRule gameCountRule = new GameCountRule();
		gameCountRule.checkActiveGamesAndMakeSpaceForNewGame(activeGames);
		GameManager.activeGames.put(game.getGameid(), game);
	}
	
	public Map<GameID, Game> getActiveGames(){
		return GameManager.activeGames;
	}
	
	public Game getGame(GameID gameid) {
		return activeGames.get(gameid);
	}
	
	public void verifyGameID(GameID gameid) throws GenericExampleException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.verifyGameID(gameid, activeGames);
			} catch (InvalidGameIDException exception) {
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GenericExampleException(e.getErrorName(), e.getMessage());
		}
	}
	
	@Scheduled(fixedRate = 5000)
	private void checkGameDuration() throws GenericExampleException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		for (Game game: activeGames.values())
			businessRules.forEach(rule -> {
				try {
					rule.checkGameDuration(game);
				} catch (GameOverException exception) {
					exceptions.add(exception);
				}
			});
		for (GenericExampleException e: exceptions) {
			throw new GenericExampleException(e.getErrorName(), e.getMessage());
		}
	}
	
	public void checkNumberOfRounds(Game game) throws GenericExampleException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.checkNumberOfRounds(game);
			} catch (GameOverException exception) {
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GenericExampleException(e.getErrorName(), e.getMessage());
		}
	}

}
