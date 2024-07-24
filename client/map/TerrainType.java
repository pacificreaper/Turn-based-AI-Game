package map;

public enum TerrainType {
	Water(100),
	Grass(1),
	Mountain(2),
	Unknown(100);
	
	public final int cost;

	private TerrainType(int cost) {
		this.cost = cost;
	}

	public int getCost() {
		return cost;
	}
	
}
