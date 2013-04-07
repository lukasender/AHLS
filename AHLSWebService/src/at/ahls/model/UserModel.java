package at.ahls.model;

import java.sql.Timestamp;

import at.ahls.database.DBConnectionController;

public class UserModel {

	private int _id;
	private String _username;
	private Timestamp _registered;
	
	public UserModel() {
	}
	
	public UserModel(String username) {
		_username = username;
	}
	
	public UserModel(String username, Timestamp registered) {
		_username = username;
		_registered = registered;
	}
	
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}
	public String getUsername() {
		return _username;
	}
	public void setUsername(String username) {
		_username = username;
	}
	public Timestamp getRegistered() {
		return _registered;
	}
	public void setRegistered(Timestamp registered) {
		_registered = registered;
	}
	
	public String createInsertSQL() {
		String timestamp = DBConnectionController.createTimestamp();
		String statement = "INSERT INTO user "
				+ "(username, registered) "
				+ "VALUES('" + _username + "', '" + timestamp + "')";
		
		return statement;
	}
}
