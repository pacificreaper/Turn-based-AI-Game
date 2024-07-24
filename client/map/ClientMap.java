package map;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;

public class ClientMap {
	private Map<Position, PlayerHalfMapNode> fields;
	private PlayerHalfMapNode playerFortPosition;
	private PlayerHalfMapNode enemyFortPosition;
	private PlayerHalfMapNode playerPosition;
	private PlayerHalfMapNode enemyPosition;
	private PlayerHalfMapNode treasurePosition;
	private static int playerHalfMapUpperBoundaryX;
	private static int playerHalfMapLowerBoundaryX;
	private static int playerHalfMapUpperBoundaryY;
	private static int playerHalfMapLowerBoundaryY;
	private static int enemyHalfMapUpperBoundaryX;
	private static int enemyHalfMapLowerBoundaryX;
	private static int enemyHalfMapUpperBoundaryY;
	private static int enemyHalfMapLowerBoundaryY;
	private final static Logger logger = LoggerFactory.getLogger(ClientMap.class); 
	private Map<Position, PlayerHalfMapNode> discoveredFields = new HashMap<>();

	public ClientMap() {
		this.fields = new HashMap<>();
		this.playerFortPosition = new PlayerHalfMapNode();
		this.enemyFortPosition = new PlayerHalfMapNode();
		this.playerPosition = new PlayerHalfMapNode();
		this.treasurePosition = new PlayerHalfMapNode();
	}
	
	public ClientMap(Map<Position, PlayerHalfMapNode> nodes) {
		this.fields = new HashMap<>(nodes);
		setPlayerFortPosition();
		setEnemyPosition();
		setPlayerPosition();
		this.enemyFortPosition = new PlayerHalfMapNode();
		setEnemyFortPosition();
		this.treasurePosition = new PlayerHalfMapNode();
	}
	
	public Map<Position, PlayerHalfMapNode> getFields() {
		return fields;
	}
	
	public void setFields(Map<Position, PlayerHalfMapNode>fields) {
		this.fields = fields;
	}
	
	public int getHeight() {
		int max_y = 0;
		for (Position pos: fields.keySet()) {
			if (pos.getY() > max_y)
				max_y = pos.getY();
		}
		return max_y;
	}
	
	public int getWidth() {
		int max_x = 0;
		for (Position pos: fields.keySet()) {
			if (pos.getX() > max_x)
				max_x = pos.getX();
		}
		return max_x;
	}
	
	public void setPlayerFortPosition() {
		for (PlayerHalfMapNode node : fields.values()) {
            if (node.getFortState() == EFortState.MyFortPresent) {
                this.playerFortPosition = node;
                break;
            }
        }
	}
	
	public PlayerHalfMapNode getPlayerFortPosition() {
		return this.playerFortPosition;
	}
	
	public void setPlayerPosition() {
		for (PlayerHalfMapNode node : fields.values()) {
            if (node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition || node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition) {
                this.playerPosition = node;
                break;
            }
        }
	}
	
	public PlayerHalfMapNode getPlayerPosition() {
		return this.playerPosition;
	}
	
	public void setEnemyPosition() {
		for (PlayerHalfMapNode node : fields.values()) {
            if (node.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition || node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition) {
                this.enemyPosition = node;
                break;
            }
        }
	}
	
	public PlayerHalfMapNode getEnemyPosition() {
		return this.enemyPosition;
	}
	
	public void setTreasurePosition() {
		for (PlayerHalfMapNode node : discoveredFields.values()) {
            if (node.isTreasurePresent() == ETreasureState.MyTreasureIsPresent) {
                this.treasurePosition = node;
                break;
            }
        }
	}
	
	public PlayerHalfMapNode getTreasurePosition() {
		return this.treasurePosition;
	}
	
	public void setPlayerHalfMapBoundaries() {
		final int fortPositionX = this.playerFortPosition.getX();
		final int fortPositionY = this.playerFortPosition.getY();
		
		if (fortPositionX < 10) {
			if (fortPositionY < 5) {
				ClientMap.playerHalfMapUpperBoundaryX = 9;
				ClientMap.playerHalfMapLowerBoundaryX = 0;
				ClientMap.playerHalfMapUpperBoundaryY = 4;
				ClientMap.playerHalfMapLowerBoundaryY = 0;
			} else {
				ClientMap.playerHalfMapUpperBoundaryX = 9;
				ClientMap.playerHalfMapLowerBoundaryX = 0;
				ClientMap.playerHalfMapUpperBoundaryY = 9;
				ClientMap.playerHalfMapLowerBoundaryY = 5;
			}
		} else {
			ClientMap.playerHalfMapUpperBoundaryX = 19;
			ClientMap.playerHalfMapLowerBoundaryX = 10;
			ClientMap.playerHalfMapUpperBoundaryY = 4;
			ClientMap.playerHalfMapLowerBoundaryY = 0;
		}
		
		logger.debug("My boundries are: " + ClientMap.playerHalfMapUpperBoundaryX + "," + playerHalfMapLowerBoundaryX + "," + ClientMap.playerHalfMapUpperBoundaryY + "," + ClientMap.playerHalfMapLowerBoundaryY);
	}
	
	public Set<PlayerHalfMapNode> getAccessibleNeighboringFields(PlayerHalfMapNode node){
		Set<PlayerHalfMapNode> neighbors = new HashSet<>();
		final int x = node.getPosition().getX();
		final int y = node.getPosition().getY();
		if (x + 1 <= ClientMap.playerHalfMapUpperBoundaryX)
			neighbors.add(fields.get(new Position(x+1, y)));
		if (x - 1 >= playerHalfMapLowerBoundaryX)
			neighbors.add(fields.get(new Position(x-1, y)));
		if (y + 1 <= playerHalfMapUpperBoundaryY)
			neighbors.add(fields.get(new Position(x, y+1)));
		if (y - 1 >= playerHalfMapLowerBoundaryY)
			neighbors.add(fields.get(new Position(x, y-1)));
		// remove water fields
		logger.debug("No of neighbors before remove: " + neighbors.size());		
	
		if (neighbors.stream().anyMatch(n -> n.getTerrain() == TerrainType.Water)) {
			Set<PlayerHalfMapNode> nodesToRemove = neighbors.stream().filter(n -> n.getTerrain() == TerrainType.Water).collect(Collectors.toSet());
			neighbors.removeAll(nodesToRemove);
		}
		
		logger.debug("Neighbors after: " + neighbors.size());	

		return neighbors;
	}
	
	public Set<PlayerHalfMapNode> getAllNeighboringFields(PlayerHalfMapNode node){
		Set<PlayerHalfMapNode> neighbors = getAccessibleNeighboringFields(node);
		int x = node.getPosition().getX();
		int y = node.getPosition().getY();
		// diagonal neighbors
		if (x + 1 <= ClientMap.playerHalfMapUpperBoundaryX && y + 1 <= ClientMap.playerHalfMapUpperBoundaryY)
			neighbors.add(fields.get(new Position(x+1, y+1)));
		if (x + 1 <= ClientMap.playerHalfMapUpperBoundaryX && y - 1 >= ClientMap.playerHalfMapLowerBoundaryY)
			neighbors.add(fields.get(new Position(x+1, y-1)));
		if (x - 1 >= ClientMap.playerHalfMapLowerBoundaryX && y + 1 <= ClientMap.playerHalfMapUpperBoundaryY)
			neighbors.add(fields.get(new Position(x-1, y+1)));
		if (x - 1 >= ClientMap.playerHalfMapLowerBoundaryX && y - 1 >= ClientMap.playerHalfMapLowerBoundaryY)
			neighbors.add(fields.get(new Position(x-1, y-1)));
		
		// remove water fields
		logger.debug("No of neighbors before remove: " + neighbors.size());		
	
		if (neighbors.stream().anyMatch(n -> n.getTerrain() == TerrainType.Water)) {
			Set<PlayerHalfMapNode> nodesToRemove = neighbors.stream().filter(n -> n.getTerrain() == TerrainType.Water).collect(Collectors.toSet());
			neighbors.removeAll(nodesToRemove);
		}
		
		logger.debug("Neighbors after: " + neighbors.size());	

		return neighbors;
	}
	
	public Map<Position, PlayerHalfMapNode> getDiscoveredFields(){
		return discoveredFields;
	}
	
	public void addToDiscoveredFields(PlayerHalfMapNode node) {
		discoveredFields.put(new Position(node.getPosition().getX(), node.getPosition().getY()), node);
	}

	public static int getPlayerHalfMapUpperBoundaryX() {
		return playerHalfMapUpperBoundaryX;
	}

	public static int getPlayerHalfMapLowerBoundaryX() {
		return playerHalfMapLowerBoundaryX;
	}

	public static int getPlayerHalfMapUpperBoundaryY() {
		return playerHalfMapUpperBoundaryY;
	}

	public static int getPlayerHalfMapLowerBoundaryY() {
		return playerHalfMapLowerBoundaryY;
	}

	public static void setPlayerHalfMapUpperBoundaryX(int playerHalfMapUpperBoundaryX) {
		ClientMap.playerHalfMapUpperBoundaryX = playerHalfMapUpperBoundaryX;
	}

	public static void setPlayerHalfMapLowerBoundaryX(int playerHalfMapLowerBoundaryX) {
		ClientMap.playerHalfMapLowerBoundaryX = playerHalfMapLowerBoundaryX;
	}

	public static void setPlayerHalfMapUpperBoundaryY(int playerHalfMapUpperBoundaryY) {
		ClientMap.playerHalfMapUpperBoundaryY = playerHalfMapUpperBoundaryY;
	}

	public static void setPlayerHalfMapLowerBoundaryY(int playerHalfMapLowerBoundaryY) {
		ClientMap.playerHalfMapLowerBoundaryY = playerHalfMapLowerBoundaryY;
	}

	public static int getEnemyHalfMapUpperBoundaryX() {
		return enemyHalfMapUpperBoundaryX;
	}

	public static void setEnemyHalfMapUpperBoundaryX(int enemyHalfMapUpperBoundaryX) {
		ClientMap.enemyHalfMapUpperBoundaryX = enemyHalfMapUpperBoundaryX;
	}

	public static int getEnemyHalfMapLowerBoundaryX() {
		return enemyHalfMapLowerBoundaryX;
	}

	public static void setEnemyHalfMapLowerBoundaryX(int enemyHalfMapLowerBoundaryX) {
		ClientMap.enemyHalfMapLowerBoundaryX = enemyHalfMapLowerBoundaryX;
	}

	public static int getEnemyHalfMapUpperBoundaryY() {
		return enemyHalfMapUpperBoundaryY;
	}

	public static void setEnemyHalfMapUpperBoundaryY(int enemyHalfMapUpperBoundaryY) {
		ClientMap.enemyHalfMapUpperBoundaryY = enemyHalfMapUpperBoundaryY;
	}

	public static int getEnemyHalfMapLowerBoundaryY() {
		return enemyHalfMapLowerBoundaryY;
	}

	public static void setEnemyHalfMapLowerBoundaryY(int enemyHalfMapLowerBoundaryY) {
		ClientMap.enemyHalfMapLowerBoundaryY = enemyHalfMapLowerBoundaryY;
	}
	
	public void setBoundriesToEnemyMapBoundries() {
		playerHalfMapUpperBoundaryX = ClientMap.enemyHalfMapUpperBoundaryX;
		playerHalfMapLowerBoundaryX = ClientMap.enemyHalfMapLowerBoundaryX;
		playerHalfMapUpperBoundaryY = ClientMap.enemyHalfMapUpperBoundaryY;
		playerHalfMapLowerBoundaryY = ClientMap.enemyHalfMapLowerBoundaryY;
	}

	public PlayerHalfMapNode getEnemyFortPosition() {
		return enemyFortPosition;
	}

	public void setEnemyFortPosition() {
		for (PlayerHalfMapNode node : discoveredFields.values()) {
            if (node.getFortState() == EFortState.EnemyFortPresent) {
                this.enemyFortPosition = node;
                break;
            }
        }
	}
	
	public void setPlayerHalfMapBoundariesToEntireMap() {
		ClientMap.playerHalfMapLowerBoundaryX = 0;
		ClientMap.playerHalfMapLowerBoundaryY = 0;
		ClientMap.playerHalfMapUpperBoundaryX = getWidth();
		ClientMap.playerHalfMapUpperBoundaryY = getHeight();
		logger.debug("Temporary boundries are : " + ClientMap.playerHalfMapLowerBoundaryX + "," + ClientMap.playerHalfMapLowerBoundaryY + " "  + ClientMap.playerHalfMapUpperBoundaryX +  "," + ClientMap.playerHalfMapUpperBoundaryY);
		
	}
	
	public void updatedDiscoveredFields(PlayerHalfMapNode nextNode) {
		if (nextNode.getTerrain() == TerrainType.Mountain) {
			nextNode.markFieldAsDiscovered();
			addToDiscoveredFields(nextNode);
			Set<PlayerHalfMapNode> nodeNeighbors = getAllNeighboringFields(nextNode);
			for (PlayerHalfMapNode neighbor : nodeNeighbors) {
				neighbor.markFieldAsDiscovered();
				addToDiscoveredFields(neighbor);
			}
		} else if (nextNode.getTerrain() == TerrainType.Grass) {
			nextNode.markFieldAsDiscovered();
			addToDiscoveredFields(nextNode);
		}
	}
		
}
