package server.map;

import java.util.*;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import server.Network.Converter;
import server.gameLogic.Game;
import server.player.Player;

public class GameMapCreation {
	private boolean combineMap = false;
	private static final int[] UPPER_LEFT_HALFMAP_BOUNDARIES = { 0, 0, 9, 4 };
	private static final int[] UPPER_RIGHT_HALFMAP_BOUNDARIES = { 10, 0, 19, 4 };
	private static final int[] LOWER_LEFT_HALFMAP_BOUNDARIES = { 0, 5, 9, 9 };

	public Set<MapField> createGameMap(PlayerHalfMap halfmap, Player player1, Player player2) {
		Collection<PlayerHalfMapNode> fields = halfmap.getMapNodes();
		Set<MapField> fullMapFields = new HashSet<>();
		setMapInfo(fields, fullMapFields, UPPER_LEFT_HALFMAP_BOUNDARIES[0], UPPER_LEFT_HALFMAP_BOUNDARIES[1],
				UPPER_LEFT_HALFMAP_BOUNDARIES[2], UPPER_LEFT_HALFMAP_BOUNDARIES[3], true, player1, player2);
		return fullMapFields;
	}

	public void setTreasurePosition(Collection<MapField> p1FullMapFields, Player player) {
		MapField treasureField = p1FullMapFields.stream().filter(
				node -> node.getTerrain() == TerrainType.Grass && node.getFort() == EFortState.NoOrUnknownFortState)
				.findAny().get();
		player.setTreasurePosition(new Position(treasureField.getX(), treasureField.getY()));
	}

	public void combineHalfMaps(PlayerHalfMap firstPlayerHalfMap, PlayerHalfMap secondPlayerHalfMap, Player player2,
			Player player1, Game game) {
		combineMap = true;
		final boolean isMyPlayer = true;
		Collection<MapField> p1FullMapFields = new HashSet<>();
		Collection<MapField> p2FullMapFields = new HashSet<>();
		Random random = new Random();
		final int numberOfPotentialHalfMaps = 3;
		int index = random.nextInt(numberOfPotentialHalfMaps);
		
		// Determine and set up respective HalfMaps for each player
		if (index == 0) {
			int anotherIndex = random.nextInt(2);
			index += anotherIndex + 1;
			if (index == 1) {
				setMapInfo(firstPlayerHalfMap.getMapNodes(), p1FullMapFields, UPPER_LEFT_HALFMAP_BOUNDARIES[0],
						UPPER_LEFT_HALFMAP_BOUNDARIES[1], UPPER_LEFT_HALFMAP_BOUNDARIES[2],
						UPPER_LEFT_HALFMAP_BOUNDARIES[3], isMyPlayer, player1, player2);
				setMapInfo(secondPlayerHalfMap.getMapNodes(), p2FullMapFields, LOWER_LEFT_HALFMAP_BOUNDARIES[0],
						LOWER_LEFT_HALFMAP_BOUNDARIES[1], LOWER_LEFT_HALFMAP_BOUNDARIES[2],
						LOWER_LEFT_HALFMAP_BOUNDARIES[3], !isMyPlayer, player1, player2);
			} else {
				setMapInfo(firstPlayerHalfMap.getMapNodes(), p1FullMapFields, UPPER_LEFT_HALFMAP_BOUNDARIES[0],
						UPPER_LEFT_HALFMAP_BOUNDARIES[1], UPPER_LEFT_HALFMAP_BOUNDARIES[2],
						UPPER_LEFT_HALFMAP_BOUNDARIES[3], isMyPlayer, player1, player2);
				setMapInfo(secondPlayerHalfMap.getMapNodes(), p2FullMapFields, UPPER_RIGHT_HALFMAP_BOUNDARIES[0],
						UPPER_RIGHT_HALFMAP_BOUNDARIES[1], UPPER_RIGHT_HALFMAP_BOUNDARIES[2],
						UPPER_RIGHT_HALFMAP_BOUNDARIES[3], !isMyPlayer, player1, player2);
			}
		} else if (index == 1) {
			setMapInfo(firstPlayerHalfMap.getMapNodes(), p1FullMapFields, LOWER_LEFT_HALFMAP_BOUNDARIES[0],
					LOWER_LEFT_HALFMAP_BOUNDARIES[1], LOWER_LEFT_HALFMAP_BOUNDARIES[2],
					LOWER_LEFT_HALFMAP_BOUNDARIES[3], isMyPlayer, player1, player2);
			setMapInfo(secondPlayerHalfMap.getMapNodes(), p2FullMapFields, UPPER_LEFT_HALFMAP_BOUNDARIES[0],
					UPPER_LEFT_HALFMAP_BOUNDARIES[1], UPPER_LEFT_HALFMAP_BOUNDARIES[2],
					UPPER_LEFT_HALFMAP_BOUNDARIES[3], !isMyPlayer, player1, player2);
			
		} else {
			setMapInfo(firstPlayerHalfMap.getMapNodes(), p1FullMapFields, UPPER_RIGHT_HALFMAP_BOUNDARIES[0],
					UPPER_RIGHT_HALFMAP_BOUNDARIES[1], UPPER_RIGHT_HALFMAP_BOUNDARIES[2],
					UPPER_RIGHT_HALFMAP_BOUNDARIES[3], isMyPlayer, player1, player2);
			setMapInfo(secondPlayerHalfMap.getMapNodes(), p2FullMapFields, UPPER_LEFT_HALFMAP_BOUNDARIES[0],
					UPPER_LEFT_HALFMAP_BOUNDARIES[1], UPPER_LEFT_HALFMAP_BOUNDARIES[2],
					UPPER_LEFT_HALFMAP_BOUNDARIES[3], !isMyPlayer, player1, player2);
		}
		Set<MapField> fullMapFieldsPlayer = new HashSet<>();

		setTreasurePosition(p1FullMapFields, player1);
		setTreasurePosition(p2FullMapFields, player2);

		fullMapFieldsPlayer.addAll(p1FullMapFields);
		fullMapFieldsPlayer.addAll(p2FullMapFields);
		game.setGameMap(new GameMap(fullMapFieldsPlayer));
	}

	private void setMapInfo(Collection<PlayerHalfMapNode> fields, Collection<MapField> fullMapFields, int xMin,
			int yMin, int xMax, int yMax, boolean myPlayer, Player player1, Player player2) {

		int x = xMin;
		int y = yMin;

		Converter converter = new Converter();

		for (int yCoord = 0; yCoord <= 4; yCoord++) {
			final int yCoordinate = yCoord;
			for (int xCoord = 0; xCoord <= 9; xCoord++) {
				final int xCoordinate = xCoord;
				PlayerHalfMapNode playerMapNode = fields.stream()
						.filter(node -> node.getX() == xCoordinate && node.getY() == yCoordinate).findFirst().get();
				EPlayerPositionState playerPositionState = EPlayerPositionState.NoPlayerPresent;
				ETreasureState treasureState = ETreasureState.NoOrUnknownTreasureState;
				EFortState fortState = EFortState.NoOrUnknownFortState;

				if (x > xMax) {
					++y;
					x = xMin;
				}

				if (playerMapNode.isFortPresent()) {
					if (myPlayer) {
						if (!combineMap)
							playerPositionState = EPlayerPositionState.MyPlayerPosition;
						player1.setFortPosition(new Position(x, y));
						player1.setCurrentPlayerPosition(new Position(x, y));

					} else {
						player2.setFortPosition(new Position(x, y));
						player2.setCurrentPlayerPosition(new Position(x, y));
					}
				} 

				MapField fullMapNode = new MapField(converter.toTerrainType(playerMapNode.getTerrain()),
						playerPositionState, treasureState, fortState, x, y);
				fullMapFields.add(fullMapNode);
				++x;
			}
		}
	}
}
