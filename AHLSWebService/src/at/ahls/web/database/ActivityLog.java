package at.ahls.web.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import at.ahls.web.rest.api.jaxb.ActivitiesType;
import at.ahls.web.rest.api.jaxb.ObjectFactory;
import at.ahls.web.rest.api.jaxb.ActivitiesType.Activity;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class ActivityLog {
	
	private static ActivityLog _activityLog;
	
	private ActivityLog() {
	}
	
	public static ActivityLog getInstance() {
		if (_activityLog == null) {
			_activityLog = new ActivityLog();
		}
		
		return _activityLog;
	}
	
	public int insertActivityLog(String data) {
		System.out.println("DBConnector: insertActivityLog: trying to insert");
		Statement statement = null;
		try {
			statement = DBConnector.getInstance().getConnection().createStatement();

			String timestamp = DBConnector.createTimestamp();
			String insertString = "INSERT INTO activity_log "
					+ "(data, time) " 
					+ "VALUES('" + data + "', '" + timestamp + "')";
			System.out.println("DBConnector: insertActivityLog: " + insertString);

			int result = statement.executeUpdate(insertString);

			System.out.println();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(statement);
		}
	}

	public ActivitiesType getActivities(int count) {
		System.out.println("DBConnector: getActivities: trying to fetch data");
		ResultSet result = null;
		try {
			Statement statement = DBConnector.getInstance().getConnection().createStatement();
			String queryString = "SELECT * " 
					+ "FROM " + "activity_log "
					+ "ORDER BY id DESC " 
					+ "LIMIT " + count;

			result = statement.executeQuery(queryString);
			ObjectFactory of = new ObjectFactory();
			ActivitiesType activities = of.createActivitiesType();
			List<Activity> activitiesList = activities.getActivity();

			while (result.next()) {
				Activity activity = new Activity();
				activity.setData(result.getString("data"));

				Timestamp time = result.getTimestamp("time");
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeInMillis(time.getTime());
				XMLGregorianCalendar timestamp = new XMLGregorianCalendarImpl(cal);
				timestamp.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
				activity.setTime(timestamp);

				activitiesList.add(activity);
			}

			return activities;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(result);
		}

		return null;
	}
	
	private void close(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void close(ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
