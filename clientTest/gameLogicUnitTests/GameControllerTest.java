package gameLogicUnitTests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import clientNetwork.Network;
import exceptions.InvalidPlayerIDException;
import gameLogic.GameController;
import messagesbase.messagesfromclient.PlayerRegistration;
import model.GameModel;

public class GameControllerTest {
	
	@Test
	public void PlayerID_SetToBlank_ShoudlThrowException() {
		Network network = mock(Network.class);
		
		when(network.sendPlayerRegistrationRequest(any(PlayerRegistration.class))).thenReturn("");
		GameModel model = mock(GameModel.class);
		GameController controller = new GameController(model);
		Executable testCode = () -> controller.registerClient(network);
		
       assertThrows(InvalidPlayerIDException.class, testCode);
	}
}
