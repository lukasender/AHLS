package at.ahls.web.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.datatype.XMLGregorianCalendar;

import at.ahls.web.rest.api.jaxb.ActivitiesType;
import at.ahls.web.rest.api.jaxb.ActivitiesType.Activity;
import at.ahls.web.rest.api.jaxb.ObjectFactory;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class DBConnector {

	private static DBConnector _connector = null;
	private Connection _connection;

	private DBConnector() {
	}

	public static DBConnector getInstance() {
		if (_connector == null) {
			_connector = new DBConnector();
		}
		return _connector;
	}

	public void connect() {
		System.out
				.println("DBConnector: Attempt to establish connection. Just a moment please...");
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/ahls");

			_connection = ds.getConnection();

			System.out
					.println("DBConnector: Connection established. Let's rock :D");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getSQLState());
			System.out.println(e.getErrorCode());
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		if (_connection == null) {
			connect();
		}

		return _connection;
	}

	public boolean testConnection() {
		if (getConnection() != null) {
			try {
				getConnection().close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public int insertActivityLog(String data) {
		System.out.println("DBConnector: insertActivityLog: trying to insert");
		Statement statement = null;
		try {
			statement = getConnection().createStatement();

			String timestamp = createTimestamp();
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
			Statement statement = getConnection().createStatement();
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
				activity.setTime(new XMLGregorianCalendarImpl(cal));

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

	private String createTimestamp() {
		// GregorianCalendar timestamp = new GregorianCalendar();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		XMLGregorianCalendar cal = new XMLGregorianCalendarImpl(new GregorianCalendar());
		String timestamp = cal.getYear() + "-" + cal.getMonth() + "-" + cal.getDay() + " " + cal.getHour() + ":" + cal.getMinute() + ":" + cal.getSecond();
		formatter.applyPattern(timestamp);
		
		return timestamp;
	}
}
