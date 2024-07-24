package gameLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.MovementDirection;
import ai.Player;
import clientNetwork.Network;
import exceptions.InvalidPlayerIDException;
import exceptions.PlayerMoveException;
import map.ClientMap;
import map.MapGenerator;
import messagesbase.UniqueGameIdentifier;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.EPlayerGameState;
import model.GameModel;

public class GameController {

	private GameModel model;
	private final static Logger logger = LoggerFactory.getLogger(GameController.class);

	public GameController(GameModel model) {
		this.model = model;
	}

	public void startGame(String serverBaseUrl, UniqueGameIdentifier gameId) { 
		Network network = new Network(serverBaseUrl, gameId);

		String playerID = "";
		try {
			playerID = registerClient(network);
		} catch (InvalidPlayerIDException e) {
			e.printStackTrace();
		}

		ClientMap map = handleMap(network, playerID);

		Player player = new Player();

		MovementDirection currentMove = MovementDirection.Wait;

		boolean treasureCollected = network.getPlayerState(playerID).hasCollectedTreasure();
		
		EPlayerGameState playerState = network.getEPlayerGameState(playerID);
		
		while (playerState != EPlayerGameState.Lost || playerState != EPlayerGameState.Won) {
			playerState = network.waitForTurn(playerID);
			updateModelPlayerState(playerState);
			
			map = network.getEntireMap(map.getDiscoveredFields());
			updateModelMap(map);
			logger.info("My player's turn!");
			if (!treasureCollected) {
				treasureCollected = network.getPlayerState(playerID).hasCollectedTreasure();
				if (treasureCollected) {
					player.informOfTreasureCollection();
				}
				if (map.getTreasurePosition().isDefined() && !player.hasTeasureBeenFound()) {
					player.informOfTreasureLocation();
					logger.info("Treasure has been located at: " + map.getTreasurePosition().getPosition().getX() + ","
							+ map.getTreasurePosition().getPosition().getY());
				}
			}  
			if (player.shouldGoToEnemyHalfMap()) {
				player.goToEnemyHalfMap(map);
			} 
			
			if (!player.shouldGoToEnemyHalfMap()) {
				player.lookForFort(map);
			}
			
			try {
				currentMove = player.updateCurrentMove(currentMove, map);
			} catch (PlayerMoveException e) {
				e.printStackTrace();
			}
			logger.info("My player wants to move " + currentMove.toString());
			network.sendMove(currentMove);
			player.reduceNeededMoves();
			logger.trace("No. of discovered fields: " + map.getDiscoveredFields().size());
			map = network.getEntireMap(map.getDiscoveredFields());
			updateModelMap(map);
			playerState = network.getEPlayerGameState(playerID);
		}
	}

	public String registerClient(Network network) throws InvalidPlayerIDException {
		PlayerRegistration playerReg = new PlayerRegistration("Asal", "Montakhab", "asalm99");
		String playerID = "";

		playerID = network.sendPlayerRegistrationRequest(playerReg);
		if (playerID.isBlank()) {
			InvalidPlayerIDException exception = new InvalidPlayerIDException("PlayerID is empty!");
			logger.error("Exception caught due to invalid PlayerID.", exception);
			throw exception;
		}

		return playerID;
	}
	
	private ClientMap handleMap(Network network, String playerID){
		MapGenerator mapGenerator = new MapGenerator();
		ClientMap map = new ClientMap(mapGenerator.generateMap());

		setUpModelMap(map);

		EPlayerGameState playerState = network.waitForTurn(playerID);
		
		updateModelPlayerState(playerState);
		
		network.sendMap(map);

		playerState = network.waitForTurn(playerID);
		updateModelPlayerState(playerState);

		model.setMap(map);
		map = network.getEntireMap(map.getDiscoveredFields());

		map.setPlayerHalfMapBoundaries();
		map.setPlayerPosition();
		map.getPlayerFortPosition().markFieldAsDiscovered();

		return map;
	}

	private void setUpModelMap(ClientMap map) {
		if (!this.model.isMapDefined()) {
			this.model.setMapForFirstTime(map);
		}
		this.model.setMap(map);
	}
	
	private void updateModelPlayerState(EPlayerGameState playerState) {
		if (playerState == EPlayerGameState.MustAct) {
			this.model.setPlayerState(playerState);
			return;
		} else {
			this.model.setPlayerState(playerState);
			System.exit(0);
		}
	}

	private void updateModelMap(ClientMap map) {
		this.model.setMap(map);
	}
}
