package clientNetwork;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import ai.MovementDirection;
import exceptions.GameStateException;
import exceptions.PlayerMoveException;
import exceptions.PlayerRegistrationException;
import exceptions.SendMapException;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import map.ClientMap;
import map.PlayerHalfMapNode;
import map.Position;
import messagesbase.ResponseEnvelope;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import reactor.core.publisher.Mono;

public class Network {
	private WebClient baseWebClient;
	private String uniqueGameID;
	private String playerID;
	private final static Logger logger = LoggerFactory.getLogger(Network.class); 
	
	public Network(String serverBaseUrl, UniqueGameIdentifier uniqueGameID) {
		this.baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE) 
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
		this.uniqueGameID = uniqueGameID.getUniqueGameID();
	}
	
	public String sendPlayerRegistrationRequest(PlayerRegistration playerReg){
		 Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + uniqueGameID + "/players")
					.body(BodyInserters.fromValue(playerReg)) // specify the data which is sent to the server
					.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server
		 
		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();
		try {
			if (resultReg.getState() == ERequestState.Error) {
				// typically happens if you forgot to create a new game before the client
				// execution or forgot to adapt the run configuration so that it supplies
				// the id of the new game to the client
				throw new PlayerRegistrationException("Error occured during player registration.");
			}
		} catch(PlayerRegistrationException e) {
			logger.error("Exception caught while registering player.", e);
		}
				
			this.playerID = resultReg.getData().get().getUniquePlayerID();
			logger.info("Client PlayerID is " + this.playerID);
			return this.playerID;
	}
	
	public GameState requestGameState(){

		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + uniqueGameID + "/states/" + playerID).retrieve().bodyToMono(ResponseEnvelope.class); // specify the
																											// object
		ResponseEnvelope<GameState> requestResult = webAccess.block();
		
		try {
			if (requestResult.getState() == ERequestState.Error) {
				throw new GameStateException("Error occured while getting GameState.");
			}
		} catch(GameStateException e) {
			logger.error("Exception caught while getting GameState.", e);
		}
		
		return requestResult.getData().get(); 
	}
	
	public void sendMap(ClientMap map) {
		Converter converter = new Converter();
		PlayerHalfMap playerHalfMap  = converter.convertIntoPlayerHalfMap(map, playerID);
		
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + uniqueGameID + "/halfmaps")
		.body(BodyInserters.fromValue(playerHalfMap)) // specify the data which is sent to the server
		.retrieve().bodyToMono(ResponseEnvelope.class);
		
		ResponseEnvelope requestResult = webAccess.block();
		
		try {
			if (requestResult.getState() == ERequestState.Error) {
				throw new SendMapException("Error occured while sending map to server.");
			}
		} catch(SendMapException e) {
			logger.error("Exception caught while sending map to server.", e);
		}
		
		logger.info("Map successfully sent to Server");
	}
	
	public void sendMove(MovementDirection movementDirection) {
		Converter converter = new Converter();
		EMove eMove = converter.convertToEMove(movementDirection);
		PlayerMove move = PlayerMove.of(playerID, eMove);
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + uniqueGameID + "/moves")
				.body(BodyInserters.fromValue(move)) // specify the data which is sent to the server
				.retrieve().bodyToMono(ResponseEnvelope.class);
				
				ResponseEnvelope requestResult = webAccess.block();
				
				try {
					if (requestResult.getState() == ERequestState.Error) {
						throw new PlayerMoveException("Error occured while sending player move to server." + requestResult.getExceptionName() + ": " + requestResult.getExceptionMessage());
					}
				} catch(PlayerMoveException e) {
					logger.error("Exception caught while sending player move to server.", e);
				}
				
				logger.info("Move successfully sent to Server");
	}
	
	public EPlayerGameState waitForTurn(String playerID) {
		EPlayerGameState playerState = getEPlayerGameState(playerID);

		while (playerState == EPlayerGameState.MustWait) {

			playerState = getEPlayerGameState(playerID);
			try {
				Thread.sleep(401);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		logger.info("Current PlayerState is: " + playerState.toString());
		return playerState;
	}
	
	public EPlayerGameState getEPlayerGameState(String playerID) {
		PlayerState player = getPlayerState(playerID);

		EPlayerGameState ePlayerState = player.getState();
		return ePlayerState;
	}
	
	public PlayerState getPlayerState(String playerID) {
		Set<PlayerState> players = new HashSet<>();

		players = requestGameState().getPlayers();

		PlayerState playerState = players.stream().filter(p -> p.getUniquePlayerID().equals(playerID)).findFirst()
				.get();

		return playerState;
	}
	
	public ClientMap getEntireMap(Map<Position, PlayerHalfMapNode> discoveredFields) {
		GameState gameState = requestGameState();

		FullMap fullmap = gameState.getMap();
		Converter converter = new Converter();
		Map<Position, PlayerHalfMapNode> fields = converter.convertIntoPlayerHalfMapNodes(fullmap, discoveredFields);

		logger.debug("Number of fields in map: " + fields.size());

		ClientMap newMap = new ClientMap(fields);
		Set<PlayerHalfMapNode> discoveredNodes = newMap.getFields().values().stream()
				.filter(node -> node.isDiscovered()).collect(Collectors.toSet());
		discoveredNodes.stream().forEach(node -> newMap.addToDiscoveredFields(node));
		newMap.setTreasurePosition();
		newMap.setEnemyFortPosition();

		return newMap;
	}

}
