package server.exceptions;

import server.gameLogic.Game;

public class InvalidHalfMapException extends GenericExampleException{

	public InvalidHalfMapException(String errorName, String errorMessage, Game game) {
		super(errorName, errorMessage);
	}

}
