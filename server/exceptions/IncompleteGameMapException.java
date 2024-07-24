package server.exceptions;

public class IncompleteGameMapException extends GenericExampleException{

	public IncompleteGameMapException() {
		super("Incomplete game map", "The player tried to send a move, even though the game map is still incomplete.");
	}

}
