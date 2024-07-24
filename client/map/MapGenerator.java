package map;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messagesbase.messagesfromserver.EFortState;


public class MapGenerator {
	private final static Logger logger = LoggerFactory.getLogger(MapGenerator.class); 
	private Map<Position, PlayerHalfMapNode> fields;
	
	public MapGenerator() {
		this.fields = new HashMap<>();
	}

	public Map<Position, PlayerHalfMapNode> generateMap() {
		boolean hasBeenValidated = false;
		
		logger.trace("Map generation has started!");
		int numberOfMapGenerations = 0;
		
		while(!hasBeenValidated) {
			++numberOfMapGenerations;
			logger.trace("Map generation attempt: {}",numberOfMapGenerations);
			fields.clear();

			createGrassMap();
			PlayerHalfMapNode fort = placeFort();
		
			Set<PlayerHalfMapNode> fortNeighborFields = getFortNeighbors(fort);
			
			setFortNeighborFieldsTerrain(fortNeighborFields);
			
			List<PlayerHalfMapNode> remainingGrassFields = getRemainingGrassFields(fortNeighborFields);
			
			setMountainFields(remainingGrassFields);
			setWaterFields(remainingGrassFields);
			
			hasBeenValidated = validateMap();
			if (!hasBeenValidated)
				logger.warn("Generated Map was invalid.");
		}
		logger.trace("Map generation completed with {} attempts.", numberOfMapGenerations);
		logger.info("Successfully generated valid map.");
		
		return fields;
	}
	
	private List<PlayerHalfMapNode> getAllNeighboringFields(PlayerHalfMapNode node){
		List<PlayerHalfMapNode> neighbors = new ArrayList<>();
		int x = node.getX();
		int y = node.getY();
		if (x + 1 < 10)
			neighbors.add(fields.get(new Position(x+1, y)));
		if (x - 1 >= 0)
			neighbors.add(fields.get(new Position(x-1, y)));
		if (y + 1 < 5)
			neighbors.add(fields.get(new Position(x, y+1)));
		if (y - 1 >= 0)
			neighbors.add(fields.get(new Position(x, y-1)));
		if (x + 1 < 10 && y + 1 < 5)
			neighbors.add(fields.get(new Position(x+1, y+1)));
		if (x + 1 < 10 && y - 1 >= 0)
			neighbors.add(fields.get(new Position(x+1, y-1)));
		if (x - 1 >= 0 && y + 1 < 5)
			neighbors.add(fields.get(new Position(x-1, y+1)));
		if (x - 1 >= 0 && y - 1 >= 0)
			neighbors.add(fields.get(new Position(x-1, y-1)));
		return neighbors;
	}
	
	private void createGrassMap() {
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 10; x++) {
				Position pos = new Position(x, y);
				fields.put(pos, new PlayerHalfMapNode(TerrainType.Grass, pos));
			}
		}
	}
	
	private PlayerHalfMapNode placeFort() {
		List<PlayerHalfMapNode> potentialFortFields = new ArrayList<>();
		int x = 1;
		for (int y = 1; y <= 3; ++y) {
			potentialFortFields.add(fields.get(new Position(x,y)));
		}
		
		x = 8;
		for (int y = 1; y <= 3; ++y) {
			potentialFortFields.add(fields.get(new Position(x,y)));
		}
		
		Random random = new Random();
		int index = random.nextInt(potentialFortFields.size());


		PlayerHalfMapNode fortField = potentialFortFields.get(index);
		fortField.setFortState(EFortState.MyFortPresent);
		logger.debug("Fort was set to position ({},{})", fortField.getX(), fortField.getY());
		return fortField;
	}
	
	
	private Set<PlayerHalfMapNode> getFortNeighbors(PlayerHalfMapNode fort) {
		Set<PlayerHalfMapNode> neighbors = new HashSet<>();
		final int x = fort.getX();
		final int y = fort.getY();
		neighbors.add(fields.get(new Position(x+1, y)));
		neighbors.add(fields.get(new Position(x-1, y)));
		neighbors.add(fields.get(new Position(x, y+1)));
		neighbors.add(fields.get(new Position(x, y-1)));
		return neighbors;
	}
	
	private void setFortNeighborFieldsTerrain(Set<PlayerHalfMapNode> fortNeighborFields) {
		Random random = new Random();
		List<TerrainType> twoWaterOneMountain = new ArrayList<>();
		twoWaterOneMountain.add(TerrainType.Water);
		twoWaterOneMountain.add(TerrainType.Water);
		twoWaterOneMountain.add(TerrainType.Mountain);
		twoWaterOneMountain.add(TerrainType.Grass);

		
		for (PlayerHalfMapNode node: fortNeighborFields) {
			int index = random.nextInt(twoWaterOneMountain.size());
			node.setTerrain(twoWaterOneMountain.get(index));
			twoWaterOneMountain.remove(index);
		}
	}
	
	private List<PlayerHalfMapNode> getRemainingGrassFields(Set<PlayerHalfMapNode> fortNeighborFields) {
		return fields.values().stream().filter(node -> !fortNeighborFields.contains(node) && node.getFortState() != EFortState.MyFortPresent).collect(Collectors.toList());
	}
	
	private void setMountainFields(List<PlayerHalfMapNode> remainingGrassFields) {
		// set mountains in such a way that they are not neighbored by other mountains on the halfmap
		Random random = new Random();
		for (int numberOfMountains = 0; numberOfMountains < 4; numberOfMountains++) {
			boolean foundNextMountainPositon = false;
			PlayerHalfMapNode randomNode = new PlayerHalfMapNode();
			
			while (!foundNextMountainPositon) {
				int index = random.nextInt(remainingGrassFields.size());
				 randomNode = remainingGrassFields.get(index);
				List<PlayerHalfMapNode> neighbors = getAllNeighboringFields(randomNode);
				if (neighbors.stream().filter(n -> n.getTerrain() == TerrainType.Mountain).count() == 0)
					foundNextMountainPositon = true;
			}
			randomNode.setTerrain(TerrainType.Mountain);
			remainingGrassFields.remove(randomNode);
		}
	}
	
	private void setWaterFields(List<PlayerHalfMapNode> remainingGrassFields) {
		Random random = new Random();
		for(int numberOfWaterFields = 0; numberOfWaterFields < 5; numberOfWaterFields++) {
			int index = random.nextInt(remainingGrassFields.size());
			remainingGrassFields.get(index).setTerrain(TerrainType.Water);
		}
	}

	private boolean validateMap() {
		MapValidator validator = new MapValidator(fields);
		return validator.validateMap();
	}
	
}


