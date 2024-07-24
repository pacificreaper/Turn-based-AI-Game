package server.main;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import messagesbase.ResponseEnvelope;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import server.Network.Converter;
import server.businessRules.MapValidationRule;
import server.data.PlayerID;
import server.data.PlayerInfo;
import server.exceptions.GenericExampleException;
import server.exceptions.InvalidGameIDException;
import server.exceptions.InvalidHalfMapException;
import server.exceptions.InvalidPlayerIDException;
import server.exceptions.PlayerActionException;
import server.gameCreation.GameID;
import server.gameLogic.Game;
import server.gameLogic.GameManager;
import server.map.GameMap;
import server.map.GameMapCreation;
import server.map.MapField;
import server.player.EPlayerStatus;
import server.player.Move;
import server.player.Player;
import server.player.PlayerStatus;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {
	private GameManager gameManager = new GameManager();
	private Converter converter = new Converter();
	private final static Logger logger = LoggerFactory.getLogger(ServerEndpoints.class);

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

		GameID gameid = new GameID();
		Game game = new Game(gameid);
		gameManager.addGame(game);
		UniqueGameIdentifier gameIdentifier = converter.toUniqueGameIdentifier(gameid);
		return gameIdentifier;
	}

	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) throws GenericExampleException {

		// Verify Game ID
		GameID gameid = converter.toGameID(gameID);
		gameManager.verifyGameID(gameid);

		// Register player
		UniquePlayerIdentifier newPlayerID = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		PlayerID playerid = converter.toPlayerID(newPlayerID);
		PlayerInfo playerInfo = converter.toPlayerInfo(playerRegistration);
		Player player = new Player(playerid, playerInfo);
		Game game = gameManager.getGame(gameid);
		game.registerPlayer(player);

		ResponseEnvelope<UniquePlayerIdentifier> playerIDMessage = new ResponseEnvelope<>(newPlayerID);
		return playerIDMessage;
	}

	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<?> receiveHalfMap(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerHalfMap halfmap) throws GenericExampleException {

		// Verify Game ID
		GameID gameid = converter.toGameID(gameID);
		gameManager.verifyGameID(gameid);
		Game game = gameManager.getGame(gameid);

		// Verify Player ID
		PlayerID playerid = new PlayerID(halfmap.getUniquePlayerID());
		game.verifyPlayerID(playerid);

		game.verifyPlayerRegistration();

		Player player = game.getPlayer(playerid);
		game.checkForIllegalActions(player);

		game.validateMap(halfmap);

		player.playerHasSentHalfMap();

		game.saveHalfMap(player, halfmap); // needed?

		game.updateTurn(player);

		game.updateGameStateId();

		ResponseEnvelope<?> result = new ResponseEnvelope<>();
		return result;
	}

	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> requestState(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) throws GenericExampleException {

		// Verify Game ID
		GameID gameid = converter.toGameID(gameID);
		gameManager.verifyGameID(gameid);
		Game game = gameManager.getGame(gameid);

		// Verify Player ID
		PlayerID playerid = converter.toPlayerID(playerID);
		game.verifyPlayerID(playerid);
		Player player = game.getPlayer(converter.toPlayerID(playerID));

		// Set up map for client
		Set<MapField> playerFields = game.getGameMap().getRespectivePlayerMap(player, game);
		FullMap fullmap = new FullMap(converter.toFullMapNodes(playerFields));

		// Set up player states for client
		Set<PlayerStatus> playerStatuses = game.setUpPlayerStates(player);
		Set<PlayerState> states = new HashSet<>();
		for (PlayerStatus playerStatus : playerStatuses)
			states.add(converter.toPlayerState(playerStatus));

		GameState gameState = new GameState(fullmap, states, game.getGameStateId());
		ResponseEnvelope<GameState> gameStateMessage = new ResponseEnvelope<>(gameState);
		return gameStateMessage;
	}

	@RequestMapping(value = "/{gameID}/moves", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<?> receiveMove(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerMove move) throws GenericExampleException {

		// Verify Game ID
		GameID gameid = converter.toGameID(gameID);
		gameManager.verifyGameID(gameid);
		Game game = gameManager.getGame(gameid);

		// Verify Player ID
		PlayerID playerid = new PlayerID(move.getUniquePlayerID());
		game.verifyPlayerID(playerid);

		game.checkIfGameMapIsComplete();
		gameManager.checkNumberOfRounds(game);
		Player player = game.getPlayer(playerid);

		game.verifyTurn(player);

		Move movement = new Move(game);
		movement.movePlayer(playerid, move.getMove());

		game.updateGameStateId();
		game.updateTurn(player);
		ResponseEnvelope<?> result = new ResponseEnvelope<>();
		return result;
	}

	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());
		logger.error("Exception thrown with following report: ", ex);
		// reply with 200 OK as defined in the network documentation
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
