package server.exceptions;

import server.gameLogic.Game;

public class InvalidMoveException extends GenericExampleException{

	public InvalidMoveException(String name, String message) {
		super(name, message);
	}

}
