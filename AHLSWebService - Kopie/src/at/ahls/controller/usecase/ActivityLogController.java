package at.ahls.controller.usecase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import at.ahls.controller.MainController;
import at.ahls.database.DBConnectionController;
import at.ahls.model.ActivityLogModel;
import at.ahls.model.UserModel;
import at.ahls.web.rest.api.jaxb.ActivitiesDto;
import at.ahls.web.rest.api.jaxb.ActivityDto;
import at.ahls.web.rest.api.jaxb.ObjectFactory;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class ActivityLogController {
	
	private static ActivityLogController _controller;
	
	private ActivityLogController() {
	}
	
	public static ActivityLogController getInstance() {
		if (_controller == null) {
			_controller = new ActivityLogController();
		}
		
		return _controller;
	}
	
	public int insertActivityLog(int sensorId, String username, String data) {
		System.out.println("ActivityLogController: insertActivityLog: trying to insert");
		
		Statement statement = null;
		try {
			// find user.
			UserModel user = MainController.getInstance().getUserController().getUser(username);
			
			// prepare SQL
			ActivityLogModel activity = new ActivityLogModel(sensorId, user.getId(), data);
			String sql = activity.createInsertSQL(); 
			
			statement = DBConnectionController.getInstance().getConnection().createStatement();
			int result = statement.executeUpdate(sql);
			
			return result;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(statement);
		}
	}
	
	
	
	public ActivitiesDto getActivities(int count) {
		System.out.println("ActivityLogController: getActivities: trying to fetch data");
		ResultSet result = null;
		try {
			Statement statement = DBConnectionController.getInstance().getConnection().createStatement();
			String queryString = ActivityLogModel.createSelectDataUserRelationSQL(count);

			result = statement.executeQuery(queryString);
			ObjectFactory of = new ObjectFactory();
			ActivitiesDto activities = of.createActivitiesDto();
			List<ActivityDto> activitiesList = activities.getActivity();

			while (result.next()) {
				ActivityDto activity = new ActivityDto();
				activity.setSensorId(result.getInt("sensor"));
				activity.setUsername(result.getString("username"));
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
