package server.exceptions;

public class InvalidPlayerIDException extends GenericExampleException{

	public InvalidPlayerIDException() {
		super("Invalid Player ID", "The provided Player ID was not valid!");
	}

}
