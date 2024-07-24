package ai;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import map.ClientMap;
import map.PlayerHalfMapNode;

public class PathFinder { 
	/*
	 * 	TAKEN FROM <2>:
	 * 	Idea for using uniform cost search from Wikipedia
	 * 	https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
	 * 	Site used as reference for writing the uniform cost search 
	 * 	https://www.baeldung.com/cs/find-path-uniform-cost-search#2-implementation
	 */
	
	private final static Logger logger = LoggerFactory.getLogger(PathFinder.class); 
	
	// TAKEN FROM START <2>
	public List<PlayerHalfMapNode> shortestPath(PlayerHalfMapNode sourceField, PlayerHalfMapNode targetField, ClientMap map){
		
		List<PlayerHalfMapNode> shortestPath = new ArrayList<>();
		
		Map<PlayerHalfMapNode, Integer> cumulativeCostToNode = new HashMap<>();
		
		Map<PlayerHalfMapNode, PlayerHalfMapNode> predecessor = new HashMap<>();
		// need to sort fields based on lowest cumulative cost 		
		PriorityQueue<PlayerHalfMapNode> frontier = new PriorityQueue<>(new PathFinderComparator(cumulativeCostToNode));
		
		for (PlayerHalfMapNode node : map.getFields().values()) {
            cumulativeCostToNode.put(node, Integer.MAX_VALUE);
            predecessor.put(node, null);
        }
		
		cumulativeCostToNode.put(sourceField, 0);
		frontier.add(sourceField);
		
		
		while (!frontier.isEmpty()) {
			PlayerHalfMapNode currentField = frontier.poll();
			
			Set<PlayerHalfMapNode> neighbors = map.getAccessibleNeighboringFields(currentField);
			
			for (PlayerHalfMapNode neighbor: neighbors) {
				int totalCumulativeCostOfField = getCostOfField(currentField, neighbor) + cumulativeCostToNode.get(currentField);
				// update cost if lower-cost path to neighbor found
			    if (totalCumulativeCostOfField < cumulativeCostToNode.get(neighbor)) {
					cumulativeCostToNode.put(neighbor, totalCumulativeCostOfField);
					predecessor.put(neighbor, currentField);
					frontier.add(neighbor);
			    }
			}
		}
		
		shortestPath.addAll(getShortestPath(targetField, predecessor));
		
		return shortestPath;
	}
	
	// TAKEN FROM END <2>
	
	private int getCostOfField(PlayerHalfMapNode currentField, PlayerHalfMapNode nextField) {
		int costOfLeavingCurrentNode = 0;
		costOfLeavingCurrentNode = currentField.getTerrain().getCost();
		
		int costOfEnteringNextNode = 0;
		costOfEnteringNextNode = nextField.getTerrain().getCost();
		
		int totalCost = costOfEnteringNextNode + costOfLeavingCurrentNode;
		return totalCost;
	}
	
	private List<PlayerHalfMapNode> getShortestPath(PlayerHalfMapNode targetField, Map<PlayerHalfMapNode, PlayerHalfMapNode> predecessor){
		List<PlayerHalfMapNode> shortestPath = new ArrayList<>();
		PlayerHalfMapNode current = targetField;
		
		while(current != null) {
			shortestPath.add(current);
			current = predecessor.get(current);
		}
		
		Collections.reverse(shortestPath);
		
		for (PlayerHalfMapNode n: shortestPath)
			logger.trace(n.getX() + "," + n.getY());
		
		shortestPath.remove(0);		
		return shortestPath;
	}
}
