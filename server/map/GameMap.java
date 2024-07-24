package server.map;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import server.exceptions.GameOverException;
import server.gameLogic.Game;
import server.player.Player;

public class GameMap {
	private Set<MapField> mapFields;

	public GameMap() {
		this.mapFields = new HashSet<>();
	}

	public GameMap(Set<MapField> mapFields) {
		this.mapFields = mapFields;
	}

	public Set<MapField> getMapFields() {
		return mapFields;
	}

	public void setMapFields(Set<MapField> mapFields) {
		this.mapFields = mapFields;
	}

	public Set<MapField> getRespectivePlayerMap(Player player, Game game) {
		Position fortPosition = player.getFortPosition();
		Set<MapField> playerFields = new HashSet<>(mapFields);
		resetMapView(playerFields);
		if (fortPosition.isDefined()) {
			markFortPositions(playerFields, player);
			markPlayerPosition(playerFields, player);
		}

		if (game.bothPlayersRegistered()) {
			Player enemy = game.getOtherPlayer(player.getPlayerID());
			if (enemy.getCurrentPlayerPosition().isDefined()) {
				final int numberOfRoundsWhereEnemyPosRandom = 8;
				if (game.getRound() <= numberOfRoundsWhereEnemyPosRandom) {
					setRandomEnemyLocation(playerFields, enemy);
				} else {
					setRealEnemyLocation(playerFields, enemy);
				}
			}
		}

		return playerFields;
	}

	private void setRealEnemyLocation(Set<MapField> playerFields, Player enemy) {
		MapField realEnemyPosition = playerFields.stream()
				.filter(field -> field.getX() == enemy.getCurrentPlayerPosition().getX()
						&& field.getY() == enemy.getCurrentPlayerPosition().getY())
				.findFirst().get();
		if (realEnemyPosition.getPlayerPos() == EPlayerPositionState.MyPlayerPosition)
			realEnemyPosition.setPlayerPos(EPlayerPositionState.BothPlayerPosition);
		else
			realEnemyPosition.setPlayerPos(EPlayerPositionState.EnemyPlayerPosition);
	}

	private void setRandomEnemyLocation(Set<MapField> playerFields, Player enemy) {
		Random random = new Random();
		final int index = random.nextInt(4);
		MapField randomField = playerFields.stream().filter(field -> field.getX() == index).findFirst().get();
		while (randomField.getX() == enemy.getCurrentPlayerPosition().getX()
				&& randomField.getY() == enemy.getCurrentPlayerPosition().getY()) {
			final int anotherIndex = random.nextInt(4);
			randomField = playerFields.stream().filter(field -> field.getX() == anotherIndex).findFirst().get();
		}
		if (randomField.getPlayerPos() == EPlayerPositionState.MyPlayerPosition)
			randomField.setPlayerPos(EPlayerPositionState.BothPlayerPosition);
		else
			randomField.setPlayerPos(EPlayerPositionState.EnemyPlayerPosition);
	}

	private void resetMapView(Set<MapField> playerFields) {
		if (playerFields.stream().anyMatch(field -> field.getFort() == EFortState.MyFortPresent))
			playerFields.stream().filter(node -> node.getFort() == EFortState.MyFortPresent).findAny().get()
					.setFort(EFortState.NoOrUnknownFortState);
		if (playerFields.stream().anyMatch(field -> field.getPlayerPos() == EPlayerPositionState.MyPlayerPosition))
			playerFields.stream().filter(field -> field.getPlayerPos() == EPlayerPositionState.MyPlayerPosition)
					.findAny().get().setPlayerPos(EPlayerPositionState.NoPlayerPresent);
		if (playerFields.stream().anyMatch(field -> field.getPlayerPos() == EPlayerPositionState.EnemyPlayerPosition))
			playerFields.stream().filter(field -> field.getPlayerPos() == EPlayerPositionState.EnemyPlayerPosition)
					.findAny().get().setPlayerPos(EPlayerPositionState.NoPlayerPresent);
		if (playerFields.stream().anyMatch(field -> field.getPlayerPos() == EPlayerPositionState.BothPlayerPosition))
			playerFields.stream().filter(field -> field.getPlayerPos() == EPlayerPositionState.BothPlayerPosition)
					.findAny().get().setPlayerPos(EPlayerPositionState.NoPlayerPresent);
		if (playerFields.stream().anyMatch(field -> field.getFort() == EFortState.EnemyFortPresent))
			playerFields.stream().filter(field -> field.getFort() == EFortState.EnemyFortPresent).findAny().get()
					.setFort(EFortState.NoOrUnknownFortState);
	}

	private void markFortPositions(Set<MapField> playerFields, Player player) {
		markPlayerFortPosition(playerFields, player);
		markEnemyFortLocation(playerFields, player);
	}

	private void markEnemyFortLocation(Set<MapField> playerFields, Player player) {
		if (player.isFortFound()) {
			MapField enemyFort = playerFields.stream()
					.filter(field -> field.getX() == player.getEnemyFortPosition().getX()
							&& field.getY() == player.getEnemyFortPosition().getY())
					.findAny().get();
			enemyFort.setFort(EFortState.EnemyFortPresent);
		}
	}

	private void markPlayerFortPosition(Set<MapField> playerFields, Player player) {
		Position fortPosition = player.getFortPosition();
		MapField fort = playerFields.stream()
				.filter(node -> node.getX() == fortPosition.getX() && node.getY() == fortPosition.getY()).findFirst()
				.get();
		fort.setFort(EFortState.MyFortPresent);
	}

	private void markPlayerPosition(Set<MapField> playerFields, Player player) {
		Position playerPosition = player.getCurrentPlayerPosition();
		MapField playerPositionField = playerFields.stream()
				.filter(node -> node.getX() == playerPosition.getX() && node.getY() == playerPosition.getY())
				.findFirst().get();
		playerPositionField.setPlayerPos(EPlayerPositionState.MyPlayerPosition);
	}

	public void calculateNeededMoves(Player player, EMove move, Game game) throws GameOverException {
		MapField playerPosition = getCurrentPlayerPosition(player);
		Optional<MapField> destinationNode = getDestinationNode(playerPosition, move);
		game.checkDestinationNodeValidity(destinationNode);
		player.setNeededMoves(playerPosition.getTerrain(), destinationNode.get().getTerrain());
	}

	public MapField getCurrentPlayerPosition(Player player) {
		Position playerPosition = player.getCurrentPlayerPosition();
		return mapFields.stream()
				.filter(field -> field.getX() == playerPosition.getX() && field.getY() == playerPosition.getY())
				.findAny().get();
	}

	public Optional<MapField> getDestinationNode(MapField playerPosition, EMove move) {
		final int playerPosX = playerPosition.getX();
		final int playerPosY = playerPosition.getY();
		return switch (move) {
		case Right ->
			mapFields.stream().filter(field -> field.getX() == playerPosX + 1 && field.getY() == playerPosY).findAny();
		case Left ->
			mapFields.stream().filter(field -> field.getX() == playerPosX - 1 && field.getY() == playerPosY).findAny();
		case Up ->
			mapFields.stream().filter(field -> field.getX() == playerPosX && field.getY() == playerPosY - 1).findAny();
		case Down ->
			mapFields.stream().filter(field -> field.getX() == playerPosX && field.getY() == playerPosY + 1).findAny();

		};
	}
}
