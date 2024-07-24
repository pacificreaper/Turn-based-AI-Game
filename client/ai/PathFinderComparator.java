package ai;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import map.PlayerHalfMapNode;

public class PathFinderComparator implements Comparator<PlayerHalfMapNode>{
	private Map<PlayerHalfMapNode, Integer> cumulativeCostToNode;

    public PathFinderComparator(Map<PlayerHalfMapNode, Integer> cumulativeCostToNode) {
        this.cumulativeCostToNode = cumulativeCostToNode;
    }

    @Override
    public int compare(PlayerHalfMapNode field1, PlayerHalfMapNode field2) {
        return Integer.compare(cumulativeCostToNode.get(field1), cumulativeCostToNode.get(field2));
    }
}
