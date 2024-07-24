package server.businessRules;

import java.util.Optional;

import server.exceptions.GameOverException;
import server.exceptions.InvalidMoveException;
import server.gameLogic.Game;
import server.map.MapField;
import server.map.TerrainType;

public class PlayerMovementRule implements IBusinessRule{
	@Override
	public void checkDestinationNodeValidity(Game game, Optional<MapField> destinationField) throws GameOverException {
		try {
		if (destinationField.isEmpty())
			throw new InvalidMoveException("Invalid move", "Player tried to move outside the map!");
		if (destinationField.get().getTerrain() == TerrainType.Water)
		throw new InvalidMoveException("Moved towards water", "Player moved towards water field and drowned!");
		} catch(InvalidMoveException e) {
			throw new GameOverException(e.getErrorName(), e.getMessage(), game);
		}
	}
}
