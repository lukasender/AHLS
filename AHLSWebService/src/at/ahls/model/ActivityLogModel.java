package at.ahls.model;

import at.ahls.database.DBConnectionController;
import at.ahls.web.rest.api.jaxb.ActivityDto;


public class ActivityLogModel extends ActivityDto {
	
	public ActivityLogModel() {}

	public ActivityLogModel(String data) {
		setData(data);
	}
	
	public static String createSelectSQL(int count) {
		String queryString = "SELECT * " 
				+ "FROM " + "activity_log "
				+ "ORDER BY id DESC " 
				+ "LIMIT " + count;
		
		return queryString;
	}
	
	public String createInsertSQL() {
		String timestamp = DBConnectionController.createTimestamp();
		String insertString = "INSERT INTO activity_log "
				+ "(data, time) " 
				+ "VALUES('" + data + "', '" + timestamp + "')";
		
		return insertString;
	}
}
