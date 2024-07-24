package server.player;

import server.data.PlayerID;
import server.data.PlayerInfo;

public class PlayerStatus {
	private PlayerInfo playerInfo;
	private EPlayerStatus status;
	private PlayerID playerid;
	private boolean collectedTreaaure;
	
	public PlayerStatus(PlayerInfo playerInfo, EPlayerStatus status, PlayerID playerid, boolean collectedTreaaure) {
		this.playerInfo = playerInfo;
		this.status = status;
		this.playerid = playerid;
		this.collectedTreaaure = collectedTreaaure;
	}
	
	public PlayerInfo getPlayerInfo() {
		return playerInfo;
	}
	public EPlayerStatus getStatus() {
		return status;
	}
	public PlayerID getPlayerid() {
		return playerid;
	}
	public boolean hasCollectedTreaaure() {
		return collectedTreaaure;
	}
}
