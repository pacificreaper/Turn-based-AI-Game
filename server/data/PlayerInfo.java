package server.data;

public class PlayerInfo {
	private String firstName;
	private String lastName;
	private String uAccount;
	
	public PlayerInfo(String firstName, String lastName, String uAccount) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uAccount = uAccount;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getuAccount() {
		return uAccount;
	}
}
