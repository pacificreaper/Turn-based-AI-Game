package aiUnitTests;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ai.MovementDirection;
import ai.Player;
import clientNetwork.Network;
import exceptions.PlayerMoveException;
import map.ClientMap;
import map.PlayerHalfMapNode;
import map.Position;
import map.TerrainType;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerTest {
	ClientMap mockedMap =  mock(ClientMap.class);
	@Test
	public void PlayerSurroundedByASingleUndiscoveredField_GetNextMoveToLookForTreasure_ShouldReturnMoveToNextUndiscoveredField() {
		// Arrange
		Map<Position, PlayerHalfMapNode> fields = new HashMap<>();
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 10; x++) {
				Position pos = new Position(x, y);
				fields.put(pos, new PlayerHalfMapNode(TerrainType.Grass, pos));
			}
		}
		PlayerHalfMapNode fort = fields.get(new Position(4, 2));
		fort.setFortState(EFortState.MyFortPresent);
		fort.markFieldAsDiscovered();
		fort.setPlayerPositionState(EPlayerPositionState.MyPlayerPosition);
		for (int x = 2; x< 9; ++x) {
			fields.get(new Position(x, 3)).setTerrain(TerrainType.Water);
		}
		
		for (int y = 0; y< 5; ++y) {
			fields.get(new Position(9, y)).setTerrain(TerrainType.Mountain);
		}
		
		fields.get(new Position(5, 2)).markFieldAsDiscovered();
		fields.get(new Position(5, 1)).markFieldAsDiscovered();
		fields.get(new Position(4, 1)).markFieldAsDiscovered();
		
		ClientMap map = new ClientMap(fields);
		
		Player player = new Player();
		
		// Act
		MovementDirection move = player.moveTowardsGoal(map);
		
		// Assert
		assertThat(move, is(equalTo(MovementDirection.Left)));
		
	}
	
	@Test
	public void PlayerSurroundedByDiscoveredFields_GetNextMove_ShouldReturnNextMoveToTheNextUndiscoveredField() {
		// Arrange
				Map<Position, PlayerHalfMapNode> fields = new HashMap<>();
				for (int y = 0; y < 5; y++) {
					for(int x = 0; x < 10; x++) {
						Position pos = new Position(x, y);
						fields.put(pos, new PlayerHalfMapNode(TerrainType.Grass, pos));
					}
				}
				PlayerHalfMapNode fort = fields.get(new Position(4, 2));
				fort.setFortState(EFortState.MyFortPresent);
				fort.setPlayerPositionState(EPlayerPositionState.MyPlayerPosition);
				for (int x = 2; x< 9; ++x) {
					fields.get(new Position(x, 3)).setTerrain(TerrainType.Water);
				}
				
				for (int y = 0; y< 5; ++y) {
					fields.get(new Position(9, y)).setTerrain(TerrainType.Mountain);
				}
				
				for (int y = 0; y < 5; y++) {
					for(int x = 0; x < 10; x++) {
						Position pos = new Position(x, y);
						if (x == 2 && y == 2)
							continue; 
						fields.get(pos).markFieldAsDiscovered();;
					}
				}
				
				ClientMap map = new ClientMap(fields);
				
				Player player = new Player();
				
				// Act
				MovementDirection move = player.moveTowardsGoal(map);
				
				// Assert
				assertThat(move, is(equalTo(MovementDirection.Left)));
	}
	
	@Test
	public void PlayerWantsToUpdateCurrentMove_MovesTowardsWaterField_MustThrowException() {
		Player player = new Player();
		player.setNextNode(new PlayerHalfMapNode(TerrainType.Water, new Position(0,0)));
		int noOfMoves = 2;
		player.setNeededMovesForUnitTest(noOfMoves);
		
		Executable testCode = () -> player.updateCurrentMove(MovementDirection.Left, mockedMap);
		
		assertThrows(PlayerMoveException.class, testCode);
	}
	
}
