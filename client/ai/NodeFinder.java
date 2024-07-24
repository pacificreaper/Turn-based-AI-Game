package ai;

import java.util.HashSet;
import java.util.Set;

import map.ClientMap;
import map.PlayerHalfMapNode;
import map.TerrainType;

public class NodeFinder {
	public PlayerHalfMapNode getNextUndiscoveredNode(Set<PlayerHalfMapNode> neighboringFields, ClientMap map, PlayerHalfMapNode playerPosition) {
		Set<PlayerHalfMapNode> sourceNodeNeighbors = new HashSet<>();
		sourceNodeNeighbors.addAll(neighboringFields);
		
		boolean undiscoveredNodeFound = false;
		Set<PlayerHalfMapNode> currentNodeNeighbors = new HashSet<>();
		while (!undiscoveredNodeFound) {
			for (PlayerHalfMapNode n :sourceNodeNeighbors) {
				currentNodeNeighbors.addAll(map.getAccessibleNeighboringFields(n));
				if (currentNodeNeighbors.contains(playerPosition))
					currentNodeNeighbors.remove(playerPosition);
				if (currentNodeNeighbors.stream().anyMatch(node -> !node.isDiscovered())) {
					undiscoveredNodeFound = true;
					break;
				}
			}
			sourceNodeNeighbors.addAll(currentNodeNeighbors);
		}
		
		PlayerHalfMapNode undiscoveredNode = sourceNodeNeighbors.stream().filter(node -> !node.isDiscovered() && node.getTerrain() != TerrainType.Water).findAny().get();
		return undiscoveredNode;
	}
}
