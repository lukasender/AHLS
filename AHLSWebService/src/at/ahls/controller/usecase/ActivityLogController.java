package at.ahls.controller.usecase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import at.ahls.database.DBConnectionController;
import at.ahls.model.ActivityLogModel;
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
	
	public int insertActivityLog(String data) {
		System.out.println("DBConnector: insertActivityLog: trying to insert");
		ActivityLogModel activity = new ActivityLogModel(data);
		String sql = activity.createInsertSQL(); 
		
		Statement statement = null;
		try {
			statement = DBConnectionController.getInstance().getConnection().createStatement();
			int result = statement.executeUpdate(sql);

			System.out.println();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(statement);
		}
	}

	public ActivitiesDto getActivities(int count) {
		System.out.println("DBConnector: getActivities: trying to fetch data");
		ResultSet result = null;
		try {
			Statement statement = DBConnectionController.getInstance().getConnection().createStatement();
			String queryString = ActivityLogModel.createSelectSQL(count);

			result = statement.executeQuery(queryString);
			ObjectFactory of = new ObjectFactory();
			ActivitiesDto activities = of.createActivitiesDto();
			List<ActivityDto> activitiesList = activities.getActivity();

			while (result.next()) {
				ActivityDto activity = new ActivityDto();
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
