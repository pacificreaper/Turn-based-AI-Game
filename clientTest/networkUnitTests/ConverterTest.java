package networkUnitTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;

import clientNetwork.Converter;
import map.ClientMap;
import map.PlayerHalfMapNode;
import map.Position;
import map.TerrainType;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EFortState;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ConverterTest {
	final int undefinedValue = 10000;
	
	@Test
	public void PassClientMap_ConvertIntoPlayerHalfMapForServer_ShouldReturnPlayerHalfMapContainingAllTheCorrectFields() {
		// Arrange 
		final String playerID = "TestPlayerID";
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
		
		ClientMap map = new ClientMap(mapFields);
		Converter converter = new Converter();
		
		// Act
		PlayerHalfMap serverMap = converter.convertIntoPlayerHalfMap(map, playerID);
		
		// Assert
		for (PlayerHalfMapNode node: map.getFields().values()) {
			final int x = node.getX();
			final int y = node.getY();
			final TerrainType terrain = node.getTerrain();
			messagesbase.messagesfromclient.PlayerHalfMapNode serverNode = serverMap.getMapNodes().stream()
					.filter(n -> n.getX() == x && n.getY() == y && n.getTerrain().toString().equals(terrain.toString()))
					.findAny().orElse(new messagesbase.messagesfromclient.PlayerHalfMapNode(undefinedValue, undefinedValue, ETerrain.Grass));
			assertThat(serverNode, is(not(equalTo(new messagesbase.messagesfromclient.PlayerHalfMapNode(undefinedValue, undefinedValue, ETerrain.Grass)))));
		}
	}
}


