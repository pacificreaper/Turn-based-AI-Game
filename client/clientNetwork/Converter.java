package clientNetwork;

import java.util.*;

import ai.MovementDirection;
import map.ClientMap;
import map.Position;
import map.TerrainType;
import messagesbase.messagesfromclient.*;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;

public class Converter {
	// to send to server
	public PlayerHalfMap convertIntoPlayerHalfMap(ClientMap map, String playerID) {
		Set<PlayerHalfMapNode> nodes = new HashSet<>();
		for (map.PlayerHalfMapNode node: map.getFields().values()) {
			boolean fortPresent = false;
			if (node.getFortState() == EFortState.MyFortPresent)
				fortPresent = true;
			nodes.add(new PlayerHalfMapNode(node.getX(), node.getY(), fortPresent, convertToETerrain(node.getTerrain())));
		}
		PlayerHalfMap playerHalfMap = new PlayerHalfMap(playerID, nodes);
		return playerHalfMap;
	}
	
	// to use for client
	public Map<Position,map.PlayerHalfMapNode> convertIntoPlayerHalfMapNodes(FullMap fullMap, Map<Position,map.PlayerHalfMapNode> discoveredFields) {
		Map<Position,map.PlayerHalfMapNode> fields = new HashMap<>();
		for (FullMapNode mapNodes: fullMap) {
			map.PlayerHalfMapNode n = new map.PlayerHalfMapNode(convertToTerrainType(mapNodes.getTerrain()), new Position(mapNodes.getX(), mapNodes.getY()));
			n.setFortState(mapNodes.getFortState());
			n.setPlayerPositionState(mapNodes.getPlayerPositionState());
			n.setTreasurePresent(mapNodes.getTreasureState());
			
			if (!discoveredFields.isEmpty()) {
				if (discoveredFields.containsKey(new Position(n.getX(), n.getY())))
					n.markFieldAsDiscovered();
			}
			
			fields.put(new Position(n.getX(), n.getY()), n);
		}
		return fields;
	}
	
	private ETerrain convertToETerrain(TerrainType terrainType) {
		return switch(terrainType) {
		case Grass -> ETerrain.Grass;
		case Water -> ETerrain.Water;
		case Mountain -> ETerrain.Mountain;
		default -> throw new IllegalArgumentException("Unexpected value: " + terrainType);
		};
	}
	
	private TerrainType convertToTerrainType(ETerrain eTerrain) {
		return switch(eTerrain) {
		case Grass -> TerrainType.Grass;
		case Water -> TerrainType.Water;
		case Mountain -> TerrainType.Mountain;
		};
	}
	
	public EMove convertToEMove(MovementDirection movementDirection) {
		return switch(movementDirection) {
		case Left -> EMove.Left;
		case Right -> EMove.Right;
		case Up -> EMove.Up;
		case Down -> EMove.Down;
		default -> throw new IllegalArgumentException("Unexpected value: " + movementDirection);
		};	
	}
}