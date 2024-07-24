package server.gameCreation;

import java.util.ArrayList;
import java.util.List;

public class AlphanumericList {
	List<String> alphanumericList;

	public AlphanumericList() {
		this.alphanumericList = new ArrayList<String>(generateAlphanumericList());
	}

	private List<String> generateAlphanumericList() {
		List<String> alphanumericList = new ArrayList<>();
		for (char capitalletter = 'A'; capitalletter <= 'Z'; capitalletter++)
			alphanumericList.add(Character.toString(capitalletter));

		for (char lowercaseLetter = 'a'; lowercaseLetter <= 'z'; lowercaseLetter++)
			alphanumericList.add(Character.toString(lowercaseLetter));

		for (int number = 0; number <= 9; number++)
			alphanumericList.add(Integer.toString(number));

		return alphanumericList;
	}

	public List<String> getAlphanumericList() {
		return alphanumericList;
	}
}
