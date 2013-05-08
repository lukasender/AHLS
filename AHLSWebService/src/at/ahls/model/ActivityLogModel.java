package at.ahls.model;

import at.ahls.database.DBConnectionController;
import at.ahls.web.rest.api.jaxb.ActivityDto;


public class ActivityLogModel extends ActivityDto {
	
	protected int _sensorId;
	protected int _userId;
	
	public ActivityLogModel() {}

	public ActivityLogModel(int sensorId, int userId, String data) {
		setSensorId(sensorId);
		setUserId(userId);
		setData(data);
	}
	
	public void setSensorId(int sensorId) {
		_sensorId = sensorId;
	}
	
	public int getSensorId() {
		return _sensorId;
	}
	
	public void setUserId(int userId) {
		_userId = userId;
	}
	
	public int getUserId() {
		return _userId;
	}

	public static String createSelectSQL(int count) {
		String queryString = "SELECT id, sensor, user_id, data " 
				+ "FROM " + "activity_log "
				+ "ORDER BY id DESC " 
				+ "LIMIT " + count;
		
		return queryString;
	}
	
	public static String createSelectDataUserRelationSQL(int count) {
		String queryString = "SELECT activity_log.id, sensor, user_id, username, data, time " 
				+ "FROM " + "activity_log "
				+ "INNER JOIN user "
				+ "ON user_id = user.id "
				+ "ORDER BY activity_log.id DESC " 
				+ "LIMIT " + count;
		
		return queryString;
	}
	
	public String createInsertSQL() {
		String timestamp = DBConnectionController.createTimestamp();
		String insertString = "INSERT INTO activity_log "
				+ "(sensor, user_id, data, time) " 
				+ "VALUES('" + _sensorId + "', '" + _userId + "', '" + data + "', '" + timestamp + "')";
		
		return insertString;
	}
}
