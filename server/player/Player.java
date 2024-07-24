package server.player;

import java.time.Instant;
import java.util.Optional;

import messagesbase.messagesfromclient.EMove;
import server.data.PlayerID;
import server.data.PlayerInfo;
import server.map.Position;
import server.map.TerrainType;

public class Player {
	private PlayerID playerid;
	private PlayerInfo playerInfo;
	private EPlayerStatus status;
	private boolean collectedTreaaure;
	private boolean hasSentHalfMap;
	private Position fortPosition;
	private int neededMoves;
	private Instant timeSinceLastAction;
	private Optional<EMove> currentMove;
	private Position currentPlayerPosition = new Position();
	private Position treasurePosition = new Position();
	private boolean fortFound = false;
	private Position enemyFortPosition;
	
	public Player(PlayerID playerid, PlayerInfo playerInfo) {
		this.playerid = playerid;
		this.playerInfo = playerInfo;
		this.status = EPlayerStatus.MustWait;
		this.collectedTreaaure = false;
		this.hasSentHalfMap = false;
		this.fortPosition = new Position();
		this.neededMoves = 0;
		this.setEnemyFortPosition(new Position());
		this.currentMove = Optional.empty();
		}
	
	public PlayerID getPlayerID() {
		return this.playerid;
	}
	
	public void setStatus(EPlayerStatus status) {
		this.status = status;
	}

	public PlayerInfo getPlayerInfo() {
		return playerInfo;
	}

	public EPlayerStatus getStatus() {
		return status;
	}

	public boolean hasCollectedTreaaure() {
		return collectedTreaaure;
	}
	
	public void setTreasureToCollected() {
		this.collectedTreaaure = true;
	}
	
	public void playerHasSentHalfMap() {
		this.hasSentHalfMap = true;
	}
	
	public boolean hasSentHalfMap() {
		return this.hasSentHalfMap;
	}
	
	public Position getFortPosition() {
		return this.fortPosition;
	}
	
	public void setFortPosition(Position fortPosition) {
		this.fortPosition = fortPosition;
	}
	
	public int getNeededMoves() {
		return this.neededMoves;
	}
	
	public void setNeededMoves(TerrainType nextNodeTerrain, TerrainType playerTerrain) {
		this.neededMoves = nextNodeTerrain.getCost() + playerTerrain.getCost();
	}

	public Instant getTimeSinceLastAction() {
		return timeSinceLastAction;
	}

	public void setTimeSinceLastAction(Instant instant) {
		this.timeSinceLastAction = instant;
	}

	public Optional<EMove> getCurrentMove() {
		return currentMove;
	}

	public void setCurrentMove(EMove currentMove) {
		this.currentMove = Optional.of(currentMove);
	}
	
	public void resetCurrentMove() {
		this.currentMove = Optional.empty();
	}

	public void reduceNeededMoves() {
		--neededMoves;
	}

	public Position getCurrentPlayerPosition() {
		return currentPlayerPosition;
	}

	public void setCurrentPlayerPosition(Position currentPlayerPosition) {
		this.currentPlayerPosition = currentPlayerPosition;
	}

	public Position getTreasurePosition() {
		return treasurePosition;
	}

	public void setTreasurePosition(Position treasurePosition) {
		this.treasurePosition = treasurePosition;
	}

	public boolean isFortFound() {
		return fortFound;
	}

	public void setFortFound(boolean fortFound) {
		this.fortFound = fortFound;
	}

	public Position getEnemyFortPosition() {
		return enemyFortPosition;
	}

	public void setEnemyFortPosition(Position enemyFortPosition) {
		this.enemyFortPosition = enemyFortPosition;
	}
}
