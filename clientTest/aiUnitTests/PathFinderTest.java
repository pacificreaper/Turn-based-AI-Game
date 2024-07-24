package aiUnitTests;

import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ai.PathFinder;
import map.ClientMap;
import map.PlayerHalfMapNode;
import map.Position;
import map.TerrainType;

public class PathFinderTest {
	@ParameterizedTest
	@MethodSource("createDataForParameterizedShortestPathTest")
	public void MapWithStartAndDestinationField_LocatedShortestPathToDestination_ReturnShortestPath(ClientMap map, PlayerHalfMapNode start, PlayerHalfMapNode destination, List<PlayerHalfMapNode> expectedPath) {
		PathFinder pathFinder = new PathFinder();
		List<PlayerHalfMapNode> path = pathFinder.shortestPath(start, destination, map);
	    Assertions.assertEquals(expectedPath, path);
	}
	
	private static Stream<Arguments> createDataForParameterizedShortestPathTest() {
		ClientMap map = createSimpleHalfMap();
		PlayerHalfMapNode start = map.getFields().get(new Position(0,0));
		PlayerHalfMapNode destination1 = map.getFields().get(new Position(1,1));
		PlayerHalfMapNode destination2 = map.getFields().get(new Position(0,1));
		return Stream.of(Arguments.of(map, start,destination1, getExpectedPath(map, start, destination1)),
				Arguments.of(map, start, destination2, getExpectedPath(map, start, destination2)));
	}
	
	private static ClientMap createSimpleHalfMap() {
		Map<Position, PlayerHalfMapNode> fields = new HashMap<>();
		fields.put(new Position(0,0), new PlayerHalfMapNode(TerrainType.Grass, new Position(0,0)));
		fields.put(new Position(1,0), new PlayerHalfMapNode(TerrainType.Mountain, new Position(1,0)));
		fields.put(new Position(0,1), new PlayerHalfMapNode(TerrainType.Grass, new Position(0,1)));
		fields.put(new Position(1,1), new PlayerHalfMapNode(TerrainType.Grass, new Position(1,1)));
		ClientMap map = new ClientMap(fields);
		ClientMap.setPlayerHalfMapLowerBoundaryX(0);
		ClientMap.setPlayerHalfMapLowerBoundaryY(0);
		ClientMap.setPlayerHalfMapUpperBoundaryX(1);
		ClientMap.setPlayerHalfMapUpperBoundaryY(1);
		return map;
	}
	
	private static List<PlayerHalfMapNode> getExpectedPath(ClientMap map, PlayerHalfMapNode start, PlayerHalfMapNode destination){
		List<PlayerHalfMapNode> path = new ArrayList<>();
		path.add(map.getFields().get(new Position(0,1)));
		if (destination.getPosition().getX() == 0) {
			return path;
		}
		path.add(map.getFields().get(new Position(1,1)));
		return path;
	}
}
