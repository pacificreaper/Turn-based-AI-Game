package map;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;
import messagesbase.messagesfromserver.EFortState;

public class MapValidator {
	/*
	 * 	TAKEN FROM <1>:
	 * 	Used Wikipedia as reference for writing the floodfill algorithm
	 * 	https://en.wikipedia.org/wiki/Flood_fill
	 */
	
	private Map<Position, PlayerHalfMapNode> fields;
	private final static Logger logger = LoggerFactory.getLogger(MapValidator.class); 

	public MapValidator(Map<Position,PlayerHalfMapNode> fields) {
		this.fields = fields;
	}
	
	public boolean validateMap() {
		 /*
		 * at least 10% Mountains -> 5
		 * at least 48% Grass -> 24
		 * at least 14% Water -> 7
		 * 1 Fort
		 * 50 fields total
		 * 14 fields remain
		 * */
				
		if (!hasCorrectNumberOfMountains())
			return false;
		
		if (!hasCorrectNumberOfGrass())
			return false;
		
		if (!hasCorrectNumberOfWater())
			return false;
		
		if (!hasCorrectNumberOfForts())
			return false;
					
		if (!checkForIslands())
			return false;
			
		return checkEdgeFields();
	}
	
	private boolean hasCorrectNumberOfMountains() {
		int numberOfMountains = (int) this.fields.values().stream().filter(node -> node.getTerrain() == TerrainType.Mountain).count();
		if (numberOfMountains < 5) {
			logger.warn("Not enough mountains generated!");
			return false;
		}
		return true;
	}
	
	private boolean hasCorrectNumberOfGrass() {
		int numberofGrass = (int) this.fields.values().stream().filter(node -> node.getTerrain() == TerrainType.Grass).count();
		if (numberofGrass < 24) {
			logger.warn("Not enough grass generated!");
			return false;
		}
		return true;
	}
	
	private boolean hasCorrectNumberOfWater() {
		int numberOfWater = (int) this.fields.values().stream().filter(node -> node.getTerrain() == TerrainType.Water).count();
		if (numberOfWater < 7) {
			logger.warn("Not enough water generated! Count: {}", numberOfWater);
			return false;
		}
		return true;
	}
	
	private boolean hasCorrectNumberOfForts() {
		int numberOfPlayerForts = (int) this.fields.values().stream().filter(node -> node.getFortState() == EFortState.MyFortPresent).count();
		if (numberOfPlayerForts != 1) {
			logger.warn("Fort count is invalid!");
			return false;
		} 
		return true;
	}
	
	// public for unit testing
	public boolean checkForIslands() {
		PlayerHalfMapNode startingNode = fields.values().stream().filter(node -> node.getFortState() == EFortState.MyFortPresent).findFirst().get();
		floodfill(startingNode.getX(), startingNode.getY());
		
		int numberOfUnvisitedFields = (int) fields.values().stream().filter(node -> !node.getVisited() && node.getTerrain() != TerrainType.Water).count();
		if (numberOfUnvisitedFields!= 0) {
			logger.warn("Island detected!");
			return false;
		}
		return true;
	}
	
	// public for unit testing
	public boolean checkEdgeFields() {
		// 51% of edges accessible
		Set<PlayerHalfMapNode> upperRowEdges = new HashSet<>();
		Set<PlayerHalfMapNode> lowerRowEdges = new HashSet<>();
		Set<PlayerHalfMapNode> rightColumnEdges = new HashSet<>();
		Set<PlayerHalfMapNode> leftColumnEdges = new HashSet<>();

		for (int i = 0; i < 10; i++) {
			final int x = i;
			upperRowEdges.addAll(fields.values().stream().filter(n -> n.getY() == 0 && n.getX() == x).collect(Collectors.toSet()));
			lowerRowEdges.addAll(fields.values().stream().filter(n -> n.getY() == 4 && n.getX() == x).collect(Collectors.toSet()));
		}
		
		if (upperRowEdges.stream().filter(node -> node.getTerrain() == TerrainType.Grass || node.getTerrain() == TerrainType.Mountain).count() < 6) {
			logger.warn("Not enough accessible edge fields on upper row!");
			return false;
		}
		
		if (lowerRowEdges.stream().filter(node -> node.getTerrain() == TerrainType.Grass || node.getTerrain() == TerrainType.Mountain).count() < 6) {
			logger.warn("Not enough accessible edge fields on lower row!");
			return false;
		}
		
		for (int i = 0; i < 5; i++) {
			final int y = i;
			rightColumnEdges.addAll(fields.values().stream().filter(n -> n.getX() == 9 && n.getY() == y).collect(Collectors.toSet()));
			leftColumnEdges.addAll(fields.values().stream().filter(n -> n.getX() == 0 && n.getY() == y).collect(Collectors.toSet()));
		}
		
		if (rightColumnEdges.stream().filter(node -> node.getTerrain() == TerrainType.Grass || node.getTerrain() == TerrainType.Mountain).count() < 3) {
			logger.warn("Not enough accessible edge fields on right column!");
			return false;
		}
		
		if (leftColumnEdges.stream().filter(node -> node.getTerrain() == TerrainType.Grass || node.getTerrain() == TerrainType.Mountain).count() < 3) {
			logger.warn("Not enough accessible edge fields on left column!");
			return false;
		}
			
		return true;
	} 

	// TAKEN FROM START <1>
	private void floodfill(int x, int y) {
		if (x < 0 || x > 9 || y < 0 || y > 4)
			return;
		PlayerHalfMapNode node = fields.get(new Position(x, y));

		// don't want neighbors of water
		if (node.getTerrain() == TerrainType.Water || node.getVisited())
			return;
		
		node.setVisited(true);
		floodfill(x + 1, y);
		floodfill(x - 1, y);
		floodfill(x, y + 1);
		floodfill(x, y - 1);
	}
	// TAKEN FROM END <1>
}
