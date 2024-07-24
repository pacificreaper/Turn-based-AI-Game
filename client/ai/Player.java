package ai;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exceptions.PlayerMoveException;
import exceptions.UndefinedPositionException;
import map.ClientMap;
import map.PlayerHalfMapNode;
import map.TerrainType;

public class Player {
	private boolean treasureFound;
	private boolean treasurePickedUp;
	private boolean fortFound;
	private PlayerHalfMapNode playerPosition;
	private int neededMoves;
	private PlayerHalfMapNode nextNode;
	private final static Logger logger = LoggerFactory.getLogger(Player.class);
	private List<PlayerHalfMapNode> currentPath;
	private boolean goToEnemyHalfMap;
	private boolean calculatePathToEnemyMap;

	public Player() {
		treasureFound = false;
		fortFound = false;
		neededMoves = 0;
		nextNode = new PlayerHalfMapNode();
		currentPath = new ArrayList<>();
		treasurePickedUp = false;
		goToEnemyHalfMap = false;
		calculatePathToEnemyMap = true;
	}

	public MovementDirection moveTowardsGoal(ClientMap map) {
		playerPosition = map.getPlayerPosition();
		PathFinder pathFinder = new PathFinder();

		// already on a given path
		if (!currentPath.isEmpty()) {
			setNextNodeFromPath();
			setNeededMoves(nextNode.getTerrain(), playerPosition.getTerrain());
			return getMovementDirection(playerPosition, nextNode);
		}

		if (fortFound) {
			currentPath = pathFinder.shortestPath(playerPosition, map.getEnemyFortPosition(), map);
			setNextNodeFromPath();
			setNeededMoves(nextNode.getTerrain(), playerPosition.getTerrain());
			return getMovementDirection(playerPosition, nextNode);
		}

		if (treasureFound && !treasurePickedUp) {
			currentPath = pathFinder.shortestPath(playerPosition, map.getTreasurePosition(), map);
			setNextNodeFromPath();
			logger.debug("Player Position is at " + playerPosition.getX() + " " + playerPosition.getY());
			logger.debug("Player wants to go to " + nextNode.getX() + " " + nextNode.getY());
			setNeededMoves(nextNode.getTerrain(), playerPosition.getTerrain());
			return getMovementDirection(playerPosition, nextNode);
		}

		logger.info("Player Position is at " + playerPosition.getX() + " " + playerPosition.getY());

		Set<PlayerHalfMapNode> neighboringFields = map.getAccessibleNeighboringFields(playerPosition);

		// check if undiscovered fields around you - if yes then do the below
		if (neighboringFields.stream().anyMatch(node -> !node.isDiscovered())) {
			goToUndiscoveredNeighbor(neighboringFields);

		} else {
			// find the next undiscovered field and go there
			lookForNextUndiscoveredField(neighboringFields, map);
		}
		return getMovementDirection(playerPosition, nextNode);
	}

	private void goToUndiscoveredNeighbor(Set<PlayerHalfMapNode> neighboringFields) {
		// go to mountain if present
		if (neighboringFields.stream().anyMatch(node -> node.getTerrain() == TerrainType.Mountain)) {
			nextNode = neighboringFields.stream().filter(node -> node.getTerrain() == TerrainType.Mountain).findAny()
					.get();
		} else {
			// get a random undiscovered node
			nextNode = neighboringFields.stream().filter(node -> !node.isDiscovered()).findAny().get();
		}
		setNeededMoves(nextNode.getTerrain(), playerPosition.getTerrain());
	}
	
	private void lookForNextUndiscoveredField(Set<PlayerHalfMapNode> neighboringFields, ClientMap map) {
		PathFinder pathFinder = new PathFinder();
		PlayerHalfMapNode undiscoveredNode = new PlayerHalfMapNode();
		logger.debug("My position: " + playerPosition.getX() + "," + playerPosition.getY());
		NodeFinder nodeFinder = new NodeFinder();
		while (currentPath.isEmpty()) {
			undiscoveredNode = nodeFinder.getNextUndiscoveredNode(neighboringFields, map, playerPosition);
			currentPath = pathFinder.shortestPath(playerPosition, undiscoveredNode, map);
		}

		setNextNodeFromPath();
		setNeededMoves(nextNode.getTerrain(), playerPosition.getTerrain());
	}

	public void setPathToEnemyHalfMap(ClientMap map) {
		PlayerHalfMapNode playerPosition = map.getPlayerPosition();
		// determine enemy halfmap boundaries
		int maxY = map.getHeight();
		int maxX = map.getWidth();
		if (ClientMap.getPlayerHalfMapUpperBoundaryX() == 19) {
			ClientMap.setEnemyHalfMapLowerBoundaryX(0);
			ClientMap.setEnemyHalfMapUpperBoundaryX(9);
			if (maxY == 4) {
				ClientMap.setEnemyHalfMapLowerBoundaryY(0);
				ClientMap.setEnemyHalfMapUpperBoundaryY(4);
			} else {
				ClientMap.setEnemyHalfMapLowerBoundaryY(5);
				ClientMap.setEnemyHalfMapUpperBoundaryY(9);
			}
		} else {
			if (ClientMap.getPlayerHalfMapUpperBoundaryY() == 4) {
				if (maxY == 4) {
					ClientMap.setEnemyHalfMapLowerBoundaryX(10);
					ClientMap.setEnemyHalfMapUpperBoundaryX(19);
					ClientMap.setEnemyHalfMapLowerBoundaryY(0);
					ClientMap.setEnemyHalfMapUpperBoundaryY(4);
				} else {
					ClientMap.setEnemyHalfMapLowerBoundaryX(0);
					ClientMap.setEnemyHalfMapUpperBoundaryX(9);
					ClientMap.setEnemyHalfMapLowerBoundaryY(5);
					ClientMap.setEnemyHalfMapUpperBoundaryY(9);
				}
			} else {
				if (maxX == 19) {
					ClientMap.setEnemyHalfMapLowerBoundaryX(10);
					ClientMap.setEnemyHalfMapUpperBoundaryX(19);
					ClientMap.setEnemyHalfMapLowerBoundaryY(0);
					ClientMap.setEnemyHalfMapUpperBoundaryY(4);
				} else {
					ClientMap.setEnemyHalfMapLowerBoundaryX(0);
					ClientMap.setEnemyHalfMapUpperBoundaryX(9);
					ClientMap.setEnemyHalfMapLowerBoundaryY(0);
					ClientMap.setEnemyHalfMapUpperBoundaryY(4);
				}
			}
		}
		
		nextNode = getRandomEnemyField(map, ClientMap.getPlayerHalfMapUpperBoundaryX(),
				ClientMap.getPlayerHalfMapLowerBoundaryX(), ClientMap.getPlayerHalfMapUpperBoundaryY(),
				ClientMap.getPlayerHalfMapLowerBoundaryY());
		logger.debug("Player wants to go from " + playerPosition.getX() + "," + playerPosition.getY() + " to "
				+ nextNode.getPosition().getX() + "," + nextNode.getY());
		
		// reset player boundaries to cross between halfmaps
		map.setPlayerHalfMapBoundariesToEntireMap();
		PathFinder pathfinder = new PathFinder();
		playerPosition = map.getPlayerPosition();
		currentPath = pathfinder.shortestPath(playerPosition, nextNode, map);
	}

	private PlayerHalfMapNode getRandomEnemyField(ClientMap map, int finalEnemyHalfMapUpperBoundaryX,
			int finalEnemyHalfMapLowerBoundaryX, int finalEnemyHalfMapUpperBoundaryY,
			int finalEnemyHalfMapLowerBoundaryY) {
		return map.getFields().values().stream()
				.filter(node -> node.getPosition().getX() <= finalEnemyHalfMapUpperBoundaryX
						&& node.getPosition().getX() >= finalEnemyHalfMapLowerBoundaryX
						&& node.getPosition().getY() <= finalEnemyHalfMapUpperBoundaryY
						&& node.getPosition().getY() >= finalEnemyHalfMapLowerBoundaryY
						&& node.getTerrain() != TerrainType.Water)
				.findAny().get();
	}

	public boolean hasTeasureBeenFound() {
		return this.treasureFound;
	}

	public boolean hasFortBeenFound() {
		return this.fortFound;
	}

	private MovementDirection getMovementDirection(PlayerHalfMapNode playerPosition, PlayerHalfMapNode nextNode) {
		if (playerPosition.getPosition().getX() < nextNode.getPosition().getX())
			return MovementDirection.Right;
		else if (playerPosition.getPosition().getX() > nextNode.getPosition().getX())
			return MovementDirection.Left;
		else if (playerPosition.getPosition().getY() < nextNode.getPosition().getY())
			return MovementDirection.Down;
		else
			return MovementDirection.Up;
	}

	public int getNeededMoves() {
		return this.neededMoves;
	}

	private void setNeededMoves(TerrainType nextNodeTerrain, TerrainType playerTerrain) {
		this.neededMoves = nextNodeTerrain.getCost() + playerTerrain.getCost();
	}

	public void resetNeededMoves() {
		this.neededMoves = 0;
	}

	public void reduceNeededMoves() {
		--neededMoves;
	}

	public PlayerHalfMapNode getNextNode() {
		return this.nextNode;
	}

	public boolean hasTreasureBeenPickedUp() {
		return this.treasurePickedUp;
	}

	public List<PlayerHalfMapNode> getCurrentPath() {
		return this.currentPath;
	}

	public void resetCurrentPath() {
		this.currentPath.clear();
	}

	private void setNextNodeFromPath() {
		nextNode = currentPath.get(0);
		if (!nextNode.isDefined()) {
			UndefinedPositionException exception = new UndefinedPositionException("Cannot go to field with undefined position!");
			logger.error("Exception caught to due undefined position of map field", exception);
			throw exception;
		}
		currentPath.remove(0);
	}
	
	public void setNextNode(PlayerHalfMapNode nextNode) {
		this.nextNode = nextNode;
	}

	public boolean shouldGoToEnemyHalfMap() {
		return this.goToEnemyHalfMap;
	}

	public void calculatePathToEnemyMap(boolean calculate) {
		this.calculatePathToEnemyMap = calculate;
	}
	
	public MovementDirection updateCurrentMove(MovementDirection currentMove, ClientMap map) throws PlayerMoveException {
		if (neededMoves == 0) {
			currentMove = moveTowardsGoal(map);
			if (nextNode.isDefined() && !nextNode.isDiscovered())
				map.updatedDiscoveredFields(nextNode);
		}
	
			if (nextNode.getTerrain() == TerrainType.Water) {
				PlayerMoveException exception = new PlayerMoveException("Player is not allowed to move onto water fields!");
				logger.error("Exception caught while updating current move", exception);
				throw exception;
			}
				
		
		return currentMove;
	}
	
	public void informOfTreasureCollection() {
		this.treasurePickedUp = true;
		this.treasureFound = true;
		resetNeededMoves();
		resetCurrentPath();
		logger.info("Player collected treasure!");
		this.goToEnemyHalfMap = true;
	}
	
	public void informOfTreasureLocation() {
		this.treasureFound = true;
		resetNeededMoves();
		resetCurrentPath();
	}
	
	public void goToEnemyHalfMap(ClientMap map) {
		if (calculatePathToEnemyMap) {
			setPathToEnemyHalfMap(map);
			calculatePathToEnemyMap = false;
		} else {
			if (currentPath.isEmpty() && neededMoves == 0) {
				goToEnemyHalfMap = false;
				map.setBoundriesToEnemyMapBoundries();
				resetNeededMoves();
			}
		}
	}
	
	public void lookForFort(ClientMap map) {
		if (map.getEnemyFortPosition().isDefined()) {
			this.fortFound = true;
			logger.info("Enemy fort has been located at: " + map.getEnemyFortPosition().getX() + ","
					+ map.getEnemyFortPosition().getY());
			resetNeededMoves();
			resetCurrentPath();
		}
	}
	
	public void setNeededMovesForUnitTest(int noOfMoves) {
		this.neededMoves = noOfMoves;
	}
}
