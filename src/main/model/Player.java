package model;

public class Player {

	private String login;
	private String password;
	private int points;
	
	public Player(String login) {
		this.login = login;
	}

	public Player(String login, String password, int points) {
		this.login = login;
		this.password = password;
		this.points = points;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	
}
