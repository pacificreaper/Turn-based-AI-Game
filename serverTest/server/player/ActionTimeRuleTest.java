package server.player;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import server.businessRules.ActionTimeRule;
import server.businessRules.IBusinessRule;
import server.data.PlayerID;
import server.data.PlayerInfo;
import server.exceptions.GameOverException;
import server.gameCreation.GameID;
import server.gameLogic.Game;

public class ActionTimeRuleTest {
	private PlayerInfo mockedInfo = mock(PlayerInfo.class);
	private Player player1 = new Player(new PlayerID("Play1"), mockedInfo);
	private Player player2 = new Player(new PlayerID("Play1"), mockedInfo);
	private Game game = new Game(new GameID("Game1"));
	@Test
	public void PlayerSendsMoveAfter5Seconds_CheckTimeSinceLastAction_ShouldThrowGameOverException() throws GameOverException {
		game.registerPlayer(player1);
		player1.setStatus(EPlayerStatus.MustAct);
		game.registerPlayer(player2);
		game.storePlayerStates(new PlayerStatus(mockedInfo, player1.getStatus(), player1.getPlayerID(), false));
		game.storePlayerStates(new PlayerStatus(mockedInfo, player2.getStatus(), player2.getPlayerID(), false));
		IBusinessRule actionTimeRule = new ActionTimeRule();
		player1.setTimeSinceLastAction(Instant.now().minus(Duration.ofMillis(5001)));
		
		Executable testCode = () -> actionTimeRule.checkTimePassedSinceLastAction(game, player1);
		
	    assertThrows(GameOverException.class, testCode);
	}
	
	@Test
	public void PlayerSendsMoveAfter4Seconds_CheckTimeSinceLastAction_ShouldNotThrowException() throws GameOverException {
		game.registerPlayer(player1);
		player1.setStatus(EPlayerStatus.MustAct);
		game.registerPlayer(player2);
		game.storePlayerStates(new PlayerStatus(mockedInfo, player1.getStatus(), player1.getPlayerID(), false));
		game.storePlayerStates(new PlayerStatus(mockedInfo, player2.getStatus(), player2.getPlayerID(), false));
		IBusinessRule actionTimeRule = new ActionTimeRule();
		player1.setTimeSinceLastAction(Instant.now().minus(Duration.ofMillis(4900)));
				
		Executable testCode = () -> actionTimeRule.checkTimePassedSinceLastAction(game, player1);
		
		assertDoesNotThrow(testCode);
	}
}
