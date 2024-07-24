package server.data;

import java.util.Objects;

public class PlayerID {
	private String playerid;
	
	public PlayerID(String playerid) {
		this.playerid = playerid;
	}
	
	public String playerIDToString() {
		return this.playerid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerID other = (PlayerID) obj;
		return Objects.equals(playerid, other.playerid);
	}
}
