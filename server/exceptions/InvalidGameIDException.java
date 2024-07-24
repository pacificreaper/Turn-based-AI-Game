package server.exceptions;

public class InvalidGameIDException extends GenericExampleException{

	public InvalidGameIDException() {
		super("Invalid Game ID", "The provided Game ID is not valid.");
	}

}
