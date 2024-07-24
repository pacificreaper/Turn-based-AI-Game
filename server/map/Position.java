package server.map;

public class Position{
	private int x;
	private int y;
	private final int undefinedValue = -1;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position() {
		this.x = undefinedValue;
		this.y = undefinedValue;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        Position point = (Position) obj;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
    
    public boolean isDefined() {
    	if (x == undefinedValue || y == undefinedValue)
    		return false;
    	return true;
    }
	
}
