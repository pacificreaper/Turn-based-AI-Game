package server.map;

import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;

public class MapField {
	private TerrainType terrain;
	private EPlayerPositionState playerPos;
	private ETreasureState treasure;
	private EFortState fort;
	private int x;
	private int y;
	
	public MapField(TerrainType terrain, EPlayerPositionState playerPositionState, ETreasureState treasureState,
			EFortState fortState, int x, int y) {
		this.terrain = terrain;
		this.playerPos = playerPositionState;
		this.treasure = treasureState;
		this.fort = fortState;
		this.x = x;
		this.y = y;
	}
	public TerrainType getTerrain() {
		return terrain;
	}
	public void setTerrain(TerrainType terrain) {
		this.terrain = terrain;
	}
	public EPlayerPositionState getPlayerPos() {
		return playerPos;
	}
	public void setPlayerPos(EPlayerPositionState playerPos) {
		this.playerPos = playerPos;
	}
	public ETreasureState getTreasure() {
		return treasure;
	}
	public void setTreasure(ETreasureState treasure) {
		this.treasure = treasure;
	}
	public EFortState getFort() {
		return fort;
	}
	public void setFort(EFortState fort) {
		this.fort = fort;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
}
