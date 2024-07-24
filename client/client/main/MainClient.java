package client.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cli.CLI;
import gameLogic.GameController;
import messagesbase.UniqueGameIdentifier;
import model.GameModel;
import exceptions.*;

public class MainClient {
	private final static Logger logger = LoggerFactory.getLogger(MainClient.class); 
	
	public static void main(String[] args) {

		// http://swe1.wst.univie.ac.at
		String serverBaseUrl = args[1]; 
		UniqueGameIdentifier gameId = new UniqueGameIdentifier(args[2]); 
		
		try {
			if (serverBaseUrl.isBlank()) {
				throw new InvalidURLException("No valid serverBaseUrl was given!");
			}
			
			if (gameId.getUniqueGameID().isBlank()) {
				throw new InvalidGameIDException("No valid gameid was given!");
			}
		} catch (InvalidGameIDException e) {
			logger.error("Exception caught while getting gameid.", e);
		} catch (InvalidURLException e) {
			logger.error("Exception caught while getting serverBaseUrl.", e);
		}
				
		logger.info("Parameters serverBaseUrl = {} and gameid = {} were successfully set.", args[1],args[2]);
		
		GameModel model = new GameModel();
		GameController controller = new GameController(model);
		CLI view = new CLI(model);
		controller.startGame(serverBaseUrl, gameId);
		
		logger.info("Game has been started.");
	}
}
