package server.Network;

import java.util.HashSet;
import java.util.Set;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.PlayerState;
import server.data.PlayerID;
import server.data.PlayerInfo;
import server.gameCreation.GameID;
import server.map.MapField;
import server.map.TerrainType;
import server.player.EPlayerStatus;
import server.player.PlayerStatus;

public class Converter {
	public UniqueGameIdentifier toUniqueGameIdentifier(GameID gameid) {
		return new UniqueGameIdentifier(gameid.getUniqueGameID());
	}

	public UniquePlayerIdentifier toUniquePlayerIdentifier(PlayerID playerid) {
		return new UniquePlayerIdentifier(playerid.playerIDToString());
	}

	public GameID toGameID(UniqueGameIdentifier uniqueGameIdentifier) {
		return new GameID(uniqueGameIdentifier.getUniqueGameID());
	}

	public PlayerID toPlayerID(UniquePlayerIdentifier uniquePlayerIdentifier) {
		return new PlayerID(uniquePlayerIdentifier.getUniquePlayerID());
	}

	public PlayerInfo toPlayerInfo(PlayerRegistration playerRegistration) {
		String firstName = playerRegistration.getStudentFirstName();
		String lastName = playerRegistration.getStudentLastName();
		String uAccount = playerRegistration.getStudentUAccount();
		return new PlayerInfo(firstName, lastName, uAccount);
	}

	public EPlayerGameState toEPlayerGameState(EPlayerStatus status) {
		return switch (status) {
		case Lost -> EPlayerGameState.Lost;
		case Won -> EPlayerGameState.Won;
		case MustWait -> EPlayerGameState.MustWait;
		case MustAct -> EPlayerGameState.MustAct;
		};
	}

	public Set<FullMapNode> toFullMapNodes(Set<MapField> gameMap) {
		Set<FullMapNode> fullMapNodes = new HashSet<>();
		for (MapField field : gameMap) {
			fullMapNodes.add(new FullMapNode(toETerrain(field.getTerrain()), field.getPlayerPos(), field.getTreasure(),
					field.getFort(), field.getX(), field.getY()));
		}
		return fullMapNodes;
	}

	public PlayerState toPlayerState(PlayerStatus playerStatus) {
		return new PlayerState(playerStatus.getPlayerInfo().getFirstName(), playerStatus.getPlayerInfo().getLastName(),
				playerStatus.getPlayerInfo().getuAccount(), toEPlayerGameState(playerStatus.getStatus()),
				toUniquePlayerIdentifier(playerStatus.getPlayerid()), playerStatus.hasCollectedTreaaure());
	}
	
	public ETerrain toETerrain(TerrainType terrainType) {
		return switch(terrainType) {
		case Grass -> ETerrain.Grass;
		case Water -> ETerrain.Water;
		case Mountain -> ETerrain.Mountain;
		default -> throw new IllegalArgumentException("Unexpected value: " + terrainType);
		};
	}
	
	public TerrainType toTerrainType(ETerrain eTerrain) {
		return switch(eTerrain) {
		case Grass -> TerrainType.Grass;
		case Water -> TerrainType.Water;
		case Mountain -> TerrainType.Mountain;
		}; 
	}
}
