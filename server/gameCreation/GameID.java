package server.gameCreation;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameID {
	String uniqueGameID;
	private static final int VALID_GAMEID_LENGTH = 5;
	private final static Logger logger = LoggerFactory.getLogger(GameID.class);

	public GameID() {
		this.uniqueGameID = generateUniqueGameID();
		logger.info("Generated unique Game ID:" + uniqueGameID);
	}

	public GameID(String gameid) {
		this.uniqueGameID = gameid;
	}

	private String generateUniqueGameID() {
		AlphanumericList alphanumericListObject = new AlphanumericList();
		List<String> alphanumericList = alphanumericListObject.getAlphanumericList();
		StringBuilder uniqueGameID = new StringBuilder();

		Random random = new Random();
		for (int gameidLength = 0; gameidLength < VALID_GAMEID_LENGTH; gameidLength++) {
			int index = random.nextInt(alphanumericList.size());
			uniqueGameID.append(alphanumericList.get(index));
		}
		return uniqueGameID.toString();
	}

	public String getUniqueGameID() {
		return uniqueGameID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueGameID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameID other = (GameID) obj;
		return Objects.equals(uniqueGameID, other.uniqueGameID);
	}

}
