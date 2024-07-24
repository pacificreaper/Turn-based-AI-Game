package server.businessRules;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collection;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.businessRules.IBusinessRule;
import server.businessRules.MapValidationRule;
import server.exceptions.InvalidHalfMapException;
import server.gameCreation.GameID;
import server.gameLogic.Game;

public class MapValidationRuleTest {
	private Collection<PlayerHalfMapNode> fields = new HashSet<>();
	private Game game = new Game(new GameID("GameT"));	

	@Test
	public void HalfMapWith45Fields_CheckNumberOfFields_ShouldThrowGameOverException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkNumberOfHalfMapFields();
		
	    assertThrows(InvalidHalfMapException.class, testCode);
		
	}
	
	@Test
	public void HalfMapWith5WaterFields_CheckNumberOfWaterFields_ShouldThrowInvalidHalfMapException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		final int x = 9;
		for (int y = 0; y < 5; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Water));
		}
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkNumberOfWaterFields();
		
	    assertThrows(InvalidHalfMapException.class, testCode);
	}
	
	@Test
	public void HalfMapWith7WaterFields_CheckNumberOfWaterFields_ShouldNotThrowException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 8; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		int x = 8;
		for (int y = 0; y < 5; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Water));
		}
		x = 9;
		for (int y = 0; y < 2; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Water));
		}
		for (int y =2; y < 5; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
		}
		
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);		
		Executable testCode = () -> mapValidationRule.checkNumberOfWaterFields();
		
		assertDoesNotThrow(testCode);
	}
	
	@Test
	public void MapWithFortOnMountain_CheckIfFortOnGrass_ShouldThrowInvalidHalfMapException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		final int x = 9;
		for (int y = 0; y < 4; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Mountain));
		}
		
		final int y = 4;
		fields.add(new PlayerHalfMapNode( x, y, true, ETerrain.Mountain));
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkIfFortOnGrass();
		
	    assertThrows(InvalidHalfMapException.class, testCode);
	}
	
	@Test
	public void MapWithFortOnGrass_CheckIfFortOnGrass_ShouldNotThrowException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		final int x = 9;
		for (int y = 0; y < 4; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Mountain));
		}
		
		final int y = 4;
		fields.add(new PlayerHalfMapNode( x, y, true, ETerrain.Grass));
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkIfFortOnGrass();
		
		assertDoesNotThrow(testCode);
	}
	
	@Test
	public void HalfMapWith23GrassFields_CheckNumberOfGrassFields_ShouldThrowInvalidHalfMapException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 4; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		for (int y = 0; y < 3; y++) {
			int x = 4;
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
		}
		fields.add(new PlayerHalfMapNode(4, 3, false, ETerrain.Mountain));
		fields.add(new PlayerHalfMapNode(4, 4, false, ETerrain.Mountain));
		
		for (int y = 0; y < 5; y++) {
			for(int x = 5; x <= 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Mountain));
			}
		}
		
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkNumberOfGrassFields();
		
	    assertThrows(InvalidHalfMapException.class, testCode);
	}
	
	@Test
	public void HalfMapWith24GrassFields_CheckNumberOfGrassFields_ShouldNotThrowException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 4; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		for (int y = 0; y < 4; y++) {
			int x = 4;
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
		}
		fields.add(new PlayerHalfMapNode(4, 4, false, ETerrain.Mountain));
		
		for (int y = 0; y < 5; y++) {
			for(int x = 5; x <= 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Mountain));
			}
		}
		
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkNumberOfGrassFields();
		
		assertDoesNotThrow(testCode);
	}
	
	@Test
	public void HalfMapWith1MountainField_CheckNumberOfMountainFields_ShouldThrowInvalidHalfMapException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		final int x = 9;
		for (int y = 0; y < 4; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Water));
		}
		int y = 4;
		fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Mountain));
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkNumberOfMountainFields();
		
	    assertThrows(InvalidHalfMapException.class, testCode);
	}
	
	@Test
	public void HalfMapWith5MountainFields_CheckNumberOfMountainFields_ShouldNotThrowException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 8; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		final int x = 9;
		for (int y = 0; y < 5; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Mountain));
		}
		
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkNumberOfMountainFields();
		
		assertDoesNotThrow(testCode);
	}
	
	@Test
	public void MapWithTwoForts_CheckNumberOfForts_ShouldThrowInvalidHalfMapException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		final int x = 9;
		for (int y = 0; y < 3; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Mountain));
		}
		
		int y = 3;
		fields.add(new PlayerHalfMapNode( x, y, true, ETerrain.Grass));
		y = 4;
		fields.add(new PlayerHalfMapNode( x, y, true, ETerrain.Grass));
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkNumberOfForts();
		
	    assertThrows(InvalidHalfMapException.class, testCode);
	}
	
	@Test
	public void MapWithOneFort_CheckNumberOfForts_ShouldNotThrowException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		final int x = 9;
		for (int y = 0; y < 4; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Mountain));
		}
		
		final int y = 5;
		fields.add(new PlayerHalfMapNode( x, y, true, ETerrain.Grass));
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkNumberOfForts();
		
		assertDoesNotThrow(testCode);
	}
	
	@Test
	public void HalfMapWithInaccessibleRightColumn_CheckEdgeFiekds_ShouldThrowInvalidHalfMapException() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 9; x++) {
				fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Grass));
			}
		}
		
		final int x = 9;
		for (int y = 0; y < 5; y++) {
			fields.add(new PlayerHalfMapNode( x, y, false, ETerrain.Water));
		}
		MapValidationRule mapValidationRule = new MapValidationRule(game);
		mapValidationRule.setField(fields);
		Executable testCode = () -> mapValidationRule.checkEdgeFields();
		
	    assertThrows(InvalidHalfMapException.class, testCode);
	}

}
