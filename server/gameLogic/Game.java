package server.gameLogic;

import java.time.Instant;
import java.util.*;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.businessRules.ActionTimeRule;
import server.businessRules.GameDataValidationRule;
import server.businessRules.IBusinessRule;
import server.businessRules.MapValidationRule;
import server.businessRules.PlayerActionRule;
import server.businessRules.PlayerCountRule;
import server.data.PlayerID;
import server.exceptions.GameOverException;
import server.exceptions.GenericExampleException;
import server.exceptions.IncompleteGameMapException;
import server.exceptions.InvalidPlayerIDException;
import server.exceptions.PlayerActionException;
import server.exceptions.TooManyPlayersException;
import server.gameCreation.GameID;
import server.map.GameMap;
import server.map.GameMapCreation;
import server.map.MapField;
import server.player.EPlayerStatus;
import server.player.Player;
import server.player.PlayerStatus;

public class Game {
	private static final int MAX_NUMBER_OF_PLAYERS = 2;
	private GameID gameid;
	private Map<PlayerID, PlayerStatus> playerStates = new HashMap<>();
	private List<Player> registeredPlayers;
	private String gameStateId;
	private GameMap gameMap;
	private PlayerHalfMap firstHalfMap;
	private int round;
	private Instant start;
	private List<IBusinessRule> businessRules;

	public Game(GameID gameid) {
		this.gameid = gameid;
		this.playerStates = new HashMap<>();
		this.registeredPlayers = new ArrayList<>();
		this.gameStateId = new String(UUID.randomUUID().toString());
		this.gameMap = new GameMap();
		this.round = 0;
		this.start = Instant.now();
		this.businessRules = new ArrayList<>(Arrays.asList(new GameDataValidationRule(), new PlayerActionRule(),
				new MapValidationRule(this), new PlayerCountRule(), new ActionTimeRule()));
	}

	public void verifyPlayerID(PlayerID playerid) throws GenericExampleException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.verifyPlayerID(registeredPlayers, playerid);
			} catch (InvalidPlayerIDException exception) {
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GenericExampleException(e.getErrorName(), e.getMessage());
		}
	}

	public void verifyTurn(Player player) throws GameOverException {
		List<GenericExampleException> exceptions = new ArrayList<>();
			businessRules.forEach(rule -> {
				try {
					rule.checkIfPlayerAlreadyWonOrLost(player, this);
				} catch (PlayerActionException exception) {
					exceptions.add(exception);
				}
			});
			businessRules.forEach(rule -> {
				try {
					rule.verifyPlayerTurn(player, this);
				} catch (PlayerActionException exception) {
					exceptions.add(exception);
				}
			});
			for (GenericExampleException e: exceptions) {
				throw new GameOverException(e.getErrorName(), e.getMessage(), this);
			}
	}

	public void verifyIfHalfMapAlreadySent(Player player) throws GameOverException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.checkIfPlayerAlreadySentHalfMap(player, this);
			} catch (PlayerActionException exception) {
				exceptions.add(exception);

			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), this);
		}
	}

	public void verifyPlayerRegistration() throws GameOverException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.checkIfBothPlayersHaveRegistered(registeredPlayers, this);
			} catch (PlayerActionException exception) {
				//throw new GameOverException(exception.getErrorName(), exception.getMessage(), this);
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), this);
		}
	}

	public void validateMap(PlayerHalfMap halmap) throws GameOverException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.validateMap(halmap.getMapNodes());
			} catch (GameOverException exception) {
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), this);
		}
	}

	public Set<PlayerStatus> setUpPlayerStates(Player player) {
		Set<PlayerStatus> playerstates = new HashSet<>();
		PlayerID playerid = player.getPlayerID();
		if (player.isFortFound()) {
			Player enemy = getOtherPlayer(playerid);
			playerLoses(enemy, player);
		}
		updatePlayerState(player);
		PlayerStatus player1State = getPlayerState(playerid);
		playerstates.add(player1State);
		if (bothPlayersRegistered()) {
			PlayerStatus player2State = playerStates.get(getOtherPlayer(playerid).getPlayerID());

			PlayerID fakePlayerID = new PlayerID(UUID.randomUUID().toString());

			playerstates.add(new PlayerStatus(player2State.getPlayerInfo(), player2State.getStatus(), fakePlayerID,
					player2State.hasCollectedTreaaure()));
		}
		return playerstates;
	}

	public void updatePlayerState(Player player) {
		PlayerID playerid = player.getPlayerID();
		PlayerStatus playerState = getPlayerState(playerid);
		PlayerStatus updatedPlayerState = new PlayerStatus(playerState.getPlayerInfo(), player.getStatus(), playerid,
				player.hasCollectedTreaaure());
		playerStates.remove(playerid);
		playerStates.put(updatedPlayerState.getPlayerid(), updatedPlayerState);
	}

	public PlayerStatus getPlayerState(PlayerID playerid) {
		return playerStates.get(playerid);
	}

	public GameID getGameid() {
		return gameid;
	}

	public void storePlayerStates(PlayerStatus playerStatus) {
		playerStates.put(playerStatus.getPlayerid(), playerStatus);
	}

	public void registerPlayer(Player player) throws GameOverException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.verifyPlayerCount(registeredPlayers);
			} catch (TooManyPlayersException exception) {
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), this);
		}
		registeredPlayers.add(player);
		setPlayerTurn();
		storePlayerStates(new PlayerStatus(player.getPlayerInfo(), player.getStatus(), player.getPlayerID(), player.hasCollectedTreaaure()));
	}

	public void setPlayerTurn() {
		if (bothPlayersRegistered()) {
			Random random = new Random();
			int index = random.nextInt(2);
			Player randomPlayer = registeredPlayers.get(index);
			randomPlayer.setStatus(EPlayerStatus.MustAct);
			randomPlayer.setTimeSinceLastAction(Instant.now());
		}
	}

	public Player getPlayer(PlayerID playerid) {
		return this.registeredPlayers.stream().filter(player -> player.getPlayerID().equals(playerid)).findFirst()
				.get();
	}

	public Player getOtherPlayer(PlayerID playerid) {
		Player otherPlayer = registeredPlayers.stream().filter(player -> !player.getPlayerID().equals(playerid))
				.findFirst().get();
		return otherPlayer;
	}

	public void saveHalfMap(Player player1, PlayerHalfMap halfmap) {
		Player player2 = getOtherPlayer(player1.getPlayerID());
		GameMapCreation gameMap = new GameMapCreation();
		if (player1.hasSentHalfMap() && player2.hasSentHalfMap()) {
			gameMap.combineHalfMaps(getFirstHalfMap(), halfmap, player1, player2, this);
		} else {
			setGameMap(new GameMap(gameMap.createGameMap(halfmap, player1, player2)));
			saveFirstHalfMap(halfmap);
		}
	}

	public String getGameStateId() {
		return this.gameStateId;
	}

	public void updateGameStateId() {
		this.gameStateId = UUID.randomUUID().toString();
	}

	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	public GameMap getGameMap() {
		return this.gameMap;
	}

	public void saveFirstHalfMap(PlayerHalfMap halfmap) {
		this.firstHalfMap = halfmap;
	}

	public PlayerHalfMap getFirstHalfMap() {
		return this.firstHalfMap;
	}

	public int getRound() {
		return this.round;
	}

	public Instant getStartTime() {
		return this.start;
	}

	public boolean bothPlayersRegistered() {
		return registeredPlayers.size() == MAX_NUMBER_OF_PLAYERS;
	}

	public void playerLoses(Player loser, Player winner) {
		loser.setStatus(EPlayerStatus.Lost);
		winner.setStatus(EPlayerStatus.Won);
		updatePlayerState(loser);
		updatePlayerState(winner);
	}

	public void checkIfGameMapIsComplete() throws GameOverException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.checkIfCompleteMapAvailable(gameMap.getMapFields());
			} catch (IncompleteGameMapException exception) {
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), this);
		}
	}

	public void updateTurn(Player player) {
		Player otherPlayer = getOtherPlayer(player.getPlayerID());
		player.setStatus(EPlayerStatus.MustWait);
		otherPlayer.setStatus(EPlayerStatus.MustAct);
		otherPlayer.setTimeSinceLastAction(Instant.now());
		++round;
	}

	public void checkPlayerActionTime(Player player) throws GameOverException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.checkTimePassedSinceLastAction(this, player);
			} catch (GameOverException exception) {
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), this);
		}
	}

	public List<Player> getRegisteredPlayers() {
		return this.registeredPlayers;
	}

	void setRegisteredPlayers(List<Player> players) {
		this.registeredPlayers = players;
	}

	public void checkForIllegalActions(Player player) throws GameOverException {
		verifyTurn(player);
		checkPlayerActionTime(player);
		verifyIfHalfMapAlreadySent(player);
	}
	
	public void checkDestinationNodeValidity(Optional<MapField> destinationField) throws GameOverException {
		List<GenericExampleException> exceptions = new ArrayList<>();
		businessRules.forEach(rule -> {
			try {
				rule.checkDestinationNodeValidity(this, destinationField);
			} catch (GameOverException exception) {
				exceptions.add(exception);
			}
		});
		for (GenericExampleException e: exceptions) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), this);
		}
	}
}
