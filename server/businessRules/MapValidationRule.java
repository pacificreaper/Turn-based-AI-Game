package server.businessRules;

import java.util.*;
import java.util.stream.Collectors;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.exceptions.GameOverException;
import server.exceptions.IncompleteGameMapException;
import server.exceptions.InvalidHalfMapException;
import server.gameLogic.Game;
import server.map.MapField;

public class MapValidationRule implements IBusinessRule {
	/*
	 * TAKEN FROM <1>: Used Wikipedia as reference for writing the floodfill
	 * algorithm https://en.wikipedia.org/wiki/Flood_fill
	 */

	private Collection<PlayerHalfMapNode> fields;
	private List<PlayerHalfMapNode> visitedFields;
	private static final int CORRECT_NUMBER_OF_FIEDLS = 50;
	private static final int MIN_NUM_OF_WATER_FIELDS = 7;
	private static final int MIN_NUM_OF_GRASS_FIELDS = 24;
	private static final int MIN_NUM_OF_MOUNTAIN_FIELDS = 5;
	private static final int CORRECT_NUMBER_OF_FORTS = 1;
	private Game game;

	public MapValidationRule(Game game) {
		this.fields = new HashSet<PlayerHalfMapNode>();
		this.visitedFields = new ArrayList<>();
		this.game = game;
	}
	
	public MapValidationRule() {}

	@Override
	public void validateMap(Collection<PlayerHalfMapNode> fields) throws GameOverException {
		this.fields = fields;
		this.visitedFields.clear();
		try {
			checkNumberOfHalfMapFields();
			checkNumberOfWaterFields();
			checkNumberOfGrassFields();
			checkNumberOfMountainFields();
			checkNumberOfForts();
			checkIfFortOnGrass();
			checkForIslands();
			checkEdgeFields();
		} catch (InvalidHalfMapException e) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), game);
		}
		
	}

	void checkNumberOfHalfMapFields() throws InvalidHalfMapException {
		if (fields.size() != CORRECT_NUMBER_OF_FIEDLS) {
			throw new InvalidHalfMapException("Invalid number of fields",
					"The HalfMap contains too many or not enough fields!", game);
		}
	}

	void checkNumberOfWaterFields() throws InvalidHalfMapException {
		int numberOfWaterFields = (int) fields.stream().filter(node -> node.getTerrain() == ETerrain.Water).count();
		if (numberOfWaterFields < MIN_NUM_OF_WATER_FIELDS) {
			throw new InvalidHalfMapException("Invalid number of water fields",
					"The HalfMap contains not enough water fields!", game);
		}
	}

	void checkNumberOfGrassFields() throws InvalidHalfMapException {
		int numberOfGrassFields = (int) fields.stream().filter(node -> node.getTerrain() == ETerrain.Grass).count();
		if (numberOfGrassFields < MIN_NUM_OF_GRASS_FIELDS) {
			throw new InvalidHalfMapException("Invalid number of grass fields",
					"The HalfMap contains not enough grass fields!", game);
		}
	}

	void checkNumberOfMountainFields() throws InvalidHalfMapException {
		int numberOfMountainFields = (int) fields.stream().filter(node -> node.getTerrain() == ETerrain.Mountain)
				.count();
		if (numberOfMountainFields < MIN_NUM_OF_MOUNTAIN_FIELDS) {
			throw new InvalidHalfMapException("Invalid number of mountain fields",
					"The HalfMap contains not enough mountain fields!", game);
		}
	}

	void checkNumberOfForts() throws InvalidHalfMapException {
		int numberOfForts = (int) fields.stream().filter(node -> node.isFortPresent()).count();
		if (numberOfForts != CORRECT_NUMBER_OF_FORTS) {
			throw new InvalidHalfMapException("Invalid number of forts", "Each HalfMap should only contain one fort!", game);
		}
	}

	void checkForIslands() throws InvalidHalfMapException {
		PlayerHalfMapNode startingNode = fields.stream().filter(node -> node.isFortPresent()).findFirst().get();
		floodfill(startingNode.getX(), startingNode.getY());

		int numberOfUnvisitedFields = (int) fields.stream()
				.filter(node -> !visitedFields.contains(node) && node.getTerrain() != ETerrain.Water).count();
		if (numberOfUnvisitedFields != 0) {
			throw new InvalidHalfMapException("Island detected", "HalfMap contains one or more unreachable fields!", game);
		}
	}

	// TAKEN FROM START <1>
	private void floodfill(int x, int y) {
		if (x < 0 || x > 9 || y < 0 || y > 4)
			return;
		PlayerHalfMapNode node = fields.stream().filter(field -> field.getX() == x && field.getY() == y).findFirst()
				.get();

		// don't want neighbors of water
		if (node.getTerrain() == ETerrain.Water || visitedFields.contains(node))
			return;

		visitedFields.add(node);
		floodfill(x + 1, y);
		floodfill(x - 1, y);
		floodfill(x, y + 1);
		floodfill(x, y - 1);
	}
	// TAKEN FROM END <1>

	void checkEdgeFields() throws InvalidHalfMapException {
		// 51% of edges accessible
		Set<PlayerHalfMapNode> upperRowEdges = new HashSet<>();
		Set<PlayerHalfMapNode> lowerRowEdges = new HashSet<>();
		Set<PlayerHalfMapNode> rightColumnEdges = new HashSet<>();
		Set<PlayerHalfMapNode> leftColumnEdges = new HashSet<>();

		final int minYRow = 0;
		final int minXRow = 0;
		final int maxYRow = 4;
		final int maxXRow = 9;
		final int minAccessibleRowFields = 6;
		final int minAccessibleColumnFields = 3;

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			final int x = xCoordinate;
			upperRowEdges.addAll(fields.stream().filter(node -> node.getY() == minYRow && node.getX() == x)
					.collect(Collectors.toSet()));
			lowerRowEdges.addAll(fields.stream().filter(node -> node.getY() == maxYRow && node.getX() == x)
					.collect(Collectors.toSet()));
		}

		if (upperRowEdges.stream()
				.filter(node -> node.getTerrain() == ETerrain.Grass || node.getTerrain() == ETerrain.Mountain)
				.count() < minAccessibleRowFields) {
			throw new InvalidHalfMapException("Invalid number of accessible edges",
					"Upper row of HalfMap does not contain enough accessible fields!", game);
		}

		if (lowerRowEdges.stream()
				.filter(node -> node.getTerrain() == ETerrain.Grass || node.getTerrain() == ETerrain.Mountain)
				.count() < minAccessibleRowFields) {
			throw new InvalidHalfMapException("Invalid number of accessible edges",
					"Lower row of HalfMap does not contain enough accessible fields!", game);
		}

		for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {
			final int y = yCoordinate;
			rightColumnEdges.addAll(fields.stream().filter(node -> node.getX() == maxXRow && node.getY() == y)
					.collect(Collectors.toSet()));
			leftColumnEdges.addAll(fields.stream().filter(node -> node.getX() == minXRow && node.getY() == y)
					.collect(Collectors.toSet()));
		}

		if (rightColumnEdges.stream()
				.filter(node -> node.getTerrain() == ETerrain.Grass || node.getTerrain() == ETerrain.Mountain)
				.count() < minAccessibleColumnFields) {
			throw new InvalidHalfMapException("Invalid number of accessible edges",
					"Right column of HalfMap does not contain enough accessible fields!", game);
		}

		if (leftColumnEdges.stream()
				.filter(node -> node.getTerrain() == ETerrain.Grass || node.getTerrain() == ETerrain.Mountain)
				.count() < minAccessibleColumnFields) {
			throw new InvalidHalfMapException("Invalid number of accessible edges",
					"Left column of HalfMap does not contain enough accessible fields!", game);
		}
	}

	void checkIfFortOnGrass() throws InvalidHalfMapException {
		PlayerHalfMapNode fort = fields.stream().filter(node -> node.isFortPresent()).findFirst().get();
		if (fort.getTerrain() != ETerrain.Grass) {
			throw new InvalidHalfMapException("Invalid fort position", "Forts can only be placed on grass fields!", game);
		}
	}
	
	@Override
	public void checkIfCompleteMapAvailable(Set<MapField> fields) throws IncompleteGameMapException {
		if (fields.size() <= 50) {
			throw new IncompleteGameMapException();
		}
	}
	
	void setField(Collection<PlayerHalfMapNode> fields) {
		this.fields = fields;
	}
}

