package mapUnitTests;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;

import map.ClientMap;
import map.MapGenerator;
import map.MapValidator;
import map.PlayerHalfMapNode;
import map.Position;
import map.TerrainType;
import messagesbase.messagesfromserver.EFortState;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapValidationTest {
	private ClientMap map;
	
	@BeforeEach
	public void setUp() {
		MapGenerator mapGenerator = new MapGenerator();
		Map<Position, PlayerHalfMapNode> fields = mapGenerator.generateMap();
		map = new ClientMap(fields);
		ClientMap.setPlayerHalfMapLowerBoundaryX(0);
		ClientMap.setPlayerHalfMapLowerBoundaryY(0);
		ClientMap.setPlayerHalfMapUpperBoundaryX(9);
		ClientMap.setPlayerHalfMapUpperBoundaryY(4);
	}
	
	// test for islands
	
	@Test
	public void RandomMapWithIsland_CheckForIslands_shouldReturnFalse() {
		// Ensure an island exists on the map
		List<PlayerHalfMapNode> fields = new ArrayList<>();
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 10; x++) {
				fields.add(new PlayerHalfMapNode(TerrainType.Grass, new Position(x,y)));
			}
		}
		
		fields.get(1).setTerrain(TerrainType.Water);
		fields.get(10).setTerrain(TerrainType.Water);
		
		for (int i = 1; i < 6; i++) {
			fields.get(i*3).setTerrain(TerrainType.Water);
		}
		for (int i = 1; i < 6; i++) {
			fields.get(36-i).setTerrain(TerrainType.Mountain);
		}
		
		fields.get(20).setFortState(EFortState.MyFortPresent);
		
		Map<Position, PlayerHalfMapNode> mapFields = new HashMap<>();
		
		for (PlayerHalfMapNode node: fields) {
			mapFields.put(new Position(node.getPosition().getX(), node.getPosition().getY()), node);
		}

		
		MapValidator mapValidator = new MapValidator(mapFields);
		
		boolean hasNoIslands = mapValidator.checkForIslands();
		
		assertThat(hasNoIslands, is(false));
	}
	
	// test for 51% accessible edges
	/*
	 * Turn upper and lower edges into water
	 * if fort then skip*/
	@Test 
	public void RandomMapWithTooLittleAccessibleEdges_CheckIfNoOfEdgesCorrect_ShouldReturnFalse() {
		// Ensure that there are less than 51% accessible edge fields
		Set<PlayerHalfMapNode> upperEdges = this.map.getFields().values().stream().filter(node -> node.getPosition().getY() == 0 && node.getFortState() != EFortState.MyFortPresent).collect(Collectors.toSet());
		Set<PlayerHalfMapNode> lowerEdges = this.map.getFields().values().stream().filter(node -> node.getPosition().getY() == 4 && node.getFortState() != EFortState.MyFortPresent).collect(Collectors.toSet());
		upperEdges.forEach(node -> node.setTerrain(TerrainType.Water));
		lowerEdges.forEach(node -> node.setTerrain(TerrainType.Water));
		MapValidator mapValidator = new MapValidator(map.getFields());
		
		boolean enoughAccessibleEdgeFields = mapValidator.checkEdgeFields();
		
		assertThat(enoughAccessibleEdgeFields, is(false));
		
	}
}
