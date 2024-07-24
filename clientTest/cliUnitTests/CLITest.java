package cliUnitTests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cli.CLI;
import map.ClientMap;
import map.PlayerHalfMapNode;
import map.Position;
import map.TerrainType;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import model.GameModel;

public class CLITest {
	private ClientMap map;
	private GameModel model = new GameModel();
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	private CLI cli;

	@BeforeEach
	public void setUp() {
		System.setOut(new PrintStream(outputStreamCaptor));
		this.cli = new CLI(model);
	}

	@Test
	public void PlayerHasLost_TestMVC_ShouldPrintLoss() {
		this.model.setPlayerState(EPlayerGameState.Lost);
		assertThat(outputStreamCaptor.toString()
			      .trim(), is(equalTo("LOST!")));
	}
	
	@Test
	public void PlayerHasWon_TestMVC_ShouldPrintWin() {
		this.model.setPlayerState(EPlayerGameState.Won);
		assertThat(outputStreamCaptor.toString()
			      .trim(), is(equalTo("WON!")));
	}
	
	@Test
	public void MapWithDifferentFields_DisplayMap_ShouldCorrectlyPrintMapToConsole() {
		// Assemble
		List<PlayerHalfMapNode> fields = new ArrayList<>();
		fields.add(new PlayerHalfMapNode(TerrainType.Grass, new Position(0, 0)));
		fields.add(new PlayerHalfMapNode(TerrainType.Grass, new Position(1, 0)));
		fields.add(new PlayerHalfMapNode(TerrainType.Grass, new Position(2, 0)));
		fields.get(0).markFieldAsDiscovered();
		fields.add(new PlayerHalfMapNode(TerrainType.Water, new Position(0, 1)));
		fields.add(new PlayerHalfMapNode(TerrainType.Water, new Position(1, 1)));
		fields.get(3).markFieldAsDiscovered();
		fields.get(1).setFortState(EFortState.MyFortPresent);
		fields.add(new PlayerHalfMapNode(TerrainType.Mountain, new Position(2, 1)));
		fields.add(new PlayerHalfMapNode(TerrainType.Mountain, new Position(3, 0)));
		fields.add(new PlayerHalfMapNode(TerrainType.Mountain, new Position(3, 1)));
		fields.get(6).markFieldAsDiscovered();
		
		Map<Position, PlayerHalfMapNode> mapFields = new HashMap<>();
		
		for (PlayerHalfMapNode node: fields) {
			mapFields.put(new Position(node.getPosition().getX(), node.getPosition().getY()), node);
		}
		
		this.map = new ClientMap(mapFields);	
		
		// Act
		cli.displayMap(map);
		
		// Assert
		assertThat(outputStreamCaptor.toString()
			      .trim().replaceAll("\u001B\\[[;\\d]*m", ""), is(equalTo("[32mШШШ ӅӅӅ ШШШ ѦѦѦ \nʬʬʬ ʬʬʬ ѦѦѦ ѦѦѦ" 
			    	  			    		
			      		)));


	}
}
