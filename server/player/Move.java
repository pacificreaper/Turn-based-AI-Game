package server.player;

import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import server.data.PlayerID;
import server.exceptions.GameOverException;
import server.gameLogic.Game;
import server.map.MapField;
import server.map.Position;

public class Move {
	private Game game;
	public Move(Game game) {
		this.game = game;
	}
	public void movePlayer(PlayerID playerid, EMove move) throws GameOverException {
		Player player = game.getPlayer(playerid);
		Player enemy = game.getOtherPlayer(playerid);
		Position playerpos = player.getCurrentPlayerPosition();
		MapField playerPosition = game.getGameMap().getRespectivePlayerMap(player, game).stream().filter(node -> node.getX() == playerpos.getX() && node.getY() == playerpos.getY()).findFirst().get();
		if (player.getCurrentMove().isEmpty()) { 
			setNewPlayerMove(player, move);
		} else if (player.getCurrentMove().get() == move) {
			player.reduceNeededMoves();
			executePlayerMovement(playerPosition, move, player, enemy);
		} else {
			// different move direction
			setNewPlayerMove(player, move);
		}
	}
	
	private void setNewPlayerMove(Player player, EMove move) throws GameOverException {
		game.getGameMap().calculateNeededMoves(player, move, game);
		player.setCurrentMove(move);
		player.reduceNeededMoves(); 
	}
	
	private void executePlayerMovement(MapField playerPosition, EMove move, Player player, Player enemy) {
		if (player.getNeededMoves() == 0) {
			if (playerPosition.getPlayerPos() != EPlayerPositionState.BothPlayerPosition)
				playerPosition.setPlayerPos(EPlayerPositionState.NoPlayerPresent);
			else {
				playerPosition.setPlayerPos(EPlayerPositionState.EnemyPlayerPosition);
			}
			MapField destinatioNode = game.getGameMap().getDestinationNode(playerPosition, move).get();
			player.setCurrentPlayerPosition(new Position(destinatioNode.getX(), destinatioNode.getY()));
			destinatioNode.setPlayerPos(EPlayerPositionState.MyPlayerPosition);
			Position treaurePosition = player.getTreasurePosition();
			Position enemyFortPosition = enemy.getFortPosition();
			if (destinatioNode.getX() == treaurePosition.getX() && destinatioNode.getY() == treaurePosition.getY()) {
				player.setTreasureToCollected();
			}
			if (enemyFortPosition.isDefined())
				if (player.hasCollectedTreaaure() && destinatioNode.getX() == enemyFortPosition.getX() && destinatioNode.getY() == enemyFortPosition.getY()) {
					player.setFortFound(true);
					player.setEnemyFortPosition(enemyFortPosition);
					destinatioNode.setFort(EFortState.EnemyFortPresent);
				}
			player.resetCurrentMove();
		} 
	}
	
}
