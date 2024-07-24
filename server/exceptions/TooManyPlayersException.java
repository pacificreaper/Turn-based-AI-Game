package server.exceptions;

public class TooManyPlayersException extends GenericExampleException{

	public TooManyPlayersException() {
		super("Invalid player count", "A new player tried to register even though 2 players have already registered for the game!");
	}

}
