package server.gameLogic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import server.exceptions.GameOverException;
import server.exceptions.TooManyPlayersException;
import server.gameCreation.GameID;
import server.player.Player;


public class PlayerCountRuleTest {
	Game mockedGame =  mock(Game.class);
	Player mockedPlayer1 = mock(Player.class);
	Player mockedPlayer2 = mock(Player.class);
	Player mockedPlayer3 = mock(Player.class);
	
	@Test
	public void GameWithTwoRegisteredPlayers_AThirdPlayerTriesToRegister_MustThrowTooManyPlayersException() {
		Game game = new Game(new GameID("TestP"));
		List<Player> registeredPlayers = new ArrayList<>();
		registeredPlayers.add(mockedPlayer1);
		registeredPlayers.add(mockedPlayer2);
		game.setRegisteredPlayers(registeredPlayers);
				
		Executable testCode = () -> game.registerPlayer(mockedPlayer3);
		
	    assertThrows(GameOverException.class, testCode);
		
	}
	
	@Test
	public void GameWithOneRegisteredPlayer_ASecondPlayerTriesToRegister_ShouldHaveTwoRegisteredPlayersForGame() {
		Game game = new Game(new GameID("TestP"));
		List<Player> registeredPlayers = new ArrayList<>();
		registeredPlayers.add(mockedPlayer1);
		game.setRegisteredPlayers(registeredPlayers);
				
		try {
			game.registerPlayer(mockedPlayer2);
		} catch (GameOverException e) {
			e.printStackTrace();
		}
		
		assertThat(game.getRegisteredPlayers().size(), is(equalTo(2)));
		
	}
	
	@Test
	public void GameWithNoPlayers_PlayerTriesToRegister_ShouldHaveOneRegisteredPlayerForGame() throws GameOverException {
		Game game = new Game(new GameID("TestP"));
				
		game.registerPlayer(mockedPlayer1);
		
		assertThat(game.getRegisteredPlayers().size(), is(equalTo(1)));
		
	}
}
