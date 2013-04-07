package at.ahls.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class DBConnectionController {

	private static DBConnectionController _connector = null;
	private Connection _connection;

	private DBConnectionController() {
	}

	public static DBConnectionController getInstance() {
		if (_connector == null) {
			_connector = new DBConnectionController();
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

	public static String createTimestamp() {
		// GregorianCalendar timestamp = new GregorianCalendar();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.SSS");
		XMLGregorianCalendar cal = new XMLGregorianCalendarImpl(new GregorianCalendar());
		String timestamp = cal.getYear() + "-" + cal.getMonth() + "-" + cal.getDay() + " " + cal.getHour() + ":" + cal.getMinute() + ":" + cal.getSecond() + "." + cal.getMillisecond();
		formatter.applyPattern(timestamp);
		
		return timestamp;
	}
}
