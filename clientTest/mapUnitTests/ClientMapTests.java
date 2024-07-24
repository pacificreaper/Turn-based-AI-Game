package mapUnitTests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import map.ClientMap;
import map.PlayerHalfMapNode;
import map.Position;
import map.TerrainType;
import messagesbase.messagesfromserver.EFortState;

public class ClientMapTests {
	private ClientMap map;
	
	@BeforeEach
	public void setUp() {
		List<PlayerHalfMapNode> fields = new ArrayList<>();
		for (int y = 0; y < 10; y++) {
			for(int x = 0; x < 10; x++) {
				Position pos = new Position(x, y);
				fields.add(new PlayerHalfMapNode(TerrainType.Grass, pos));
			}
		}
		for (int i = 0; i < 7; i++) {
			fields.get(i*10).setTerrain(TerrainType.Water);
		}
		for (int i = 1; i < 5; i++) {
			fields.get(i*4+1).setTerrain(TerrainType.Mountain);
		}
		fields.get(1).setFortState(EFortState.MyFortPresent);
		
		Map<Position, PlayerHalfMapNode> mapFields = new HashMap<>();
		
		for (PlayerHalfMapNode node: fields) {
			mapFields.put(new Position(node.getPosition().getX(), node.getPosition().getY()), node);
		}
		
		this.map = new ClientMap(mapFields);	
	}
	
	@Test
	public void MapWith100Fields_DetermineClientHalfMap_ShouldReturnCorrectBoundaries() {
		map.setPlayerHalfMapBoundaries();
		
		assertThat(ClientMap.getPlayerHalfMapLowerBoundaryX(), is(equalTo(0)));
		assertThat(ClientMap.getPlayerHalfMapUpperBoundaryX(), is(equalTo(9)));
		assertThat(ClientMap.getPlayerHalfMapLowerBoundaryY(), is(equalTo(0)));
		assertThat(ClientMap.getPlayerHalfMapUpperBoundaryY(), is(equalTo(4)));
	}
	
	@Test
	public void MapWith100Fields_getAllNeighboringFields_ShouldReturnSetContainingAllNeighboringFieldsIncludingDiagonalButExcludingWaterFields() {
		PlayerHalfMapNode node = map.getFields().get(new Position(9,0));
		Set<PlayerHalfMapNode> neighbors = map.getAllNeighboringFields(node);
		
		PlayerHalfMapNode n1 = neighbors.stream().filter(n -> n.getPosition().getX() == 8 && n.getPosition().getY() == 0).findFirst().get();
		PlayerHalfMapNode n2 = neighbors.stream().filter(n -> n.getPosition().getX() == 8 && n.getPosition().getY() == 1).findFirst().get();
		PlayerHalfMapNode n3 = neighbors.stream().filter(n -> n.getPosition().getX() == 9 && n.getPosition().getY() == 1).findFirst().get();
		
		assertThat(neighbors.size(), is(equalTo(3)));
		assertThat(neighbors.contains(n1), is(true));
		assertThat(neighbors.contains(n2), is(true));
		assertThat(neighbors.contains(n3), is(true));

		
	}
}
