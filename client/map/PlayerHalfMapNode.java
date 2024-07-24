package map;

import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;

public class PlayerHalfMapNode {
	private TerrainType terrain;
	private Position position;
	private boolean visited; // used for map validation (floodfill)
	private boolean discovered; // used for exploration
	private ETreasureState treasurePresent;
	private EPlayerPositionState playerPositionState;
	private EFortState fortState;
	private final int undefinedValue = -1;
	
	public PlayerHalfMapNode() {
		this.terrain = TerrainType.Unknown;
		this.position = new Position(undefinedValue, undefinedValue);
		this.visited = false;
		this.treasurePresent = ETreasureState.NoOrUnknownTreasureState;
		this.playerPositionState = EPlayerPositionState.NoPlayerPresent;
		this.fortState = EFortState.NoOrUnknownFortState;
		this.discovered = false;
	}
	
	public PlayerHalfMapNode(TerrainType terrain, Position position){
		this.terrain = terrain;
		this.position = position;
		this.visited = false;
		this.treasurePresent = ETreasureState.NoOrUnknownTreasureState;
		this.playerPositionState = EPlayerPositionState.NoPlayerPresent;
		this.fortState = EFortState.NoOrUnknownFortState;
		if (terrain == TerrainType.Water)
			discovered = true;
		this.discovered = false;
	}
	
	public boolean isDefined() {
		return this.position.isDefined();
	}

	public ETreasureState isTreasurePresent() {
		return treasurePresent;
	}

	public void setTreasurePresent(ETreasureState eTreasureState) {
		this.treasurePresent = eTreasureState;
	}

	public EPlayerPositionState getPlayerPositionState() {
		return playerPositionState;
	}

	public void setPlayerPositionState(EPlayerPositionState playerPositionState) {
		this.playerPositionState = playerPositionState;
	}

	public EFortState getFortState() {
		return fortState;
	}

	public void setFortState(EFortState fortState) {
		this.fortState = fortState;
	}

	public TerrainType getTerrain() {
		return terrain;
	}
	
	public void setTerrain(TerrainType terrain) {
		this.terrain = terrain;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public int getX() {
		return this.position.getX();
	}
	
	public int getY() {
		return this.position.getY();
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public boolean getVisited() {
		return visited;
	}
	
	public void markFieldAsDiscovered() {
		this.discovered = true;
	}
	
	public boolean isDiscovered() {
		return this.discovered;
	}
	
	public boolean equals(PlayerHalfMapNode node) {
		if (this.position == node.position && this.terrain == node.terrain)
			return true;
		return false;
	}
	
}
