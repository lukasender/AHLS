package at.ahls.controller.usecase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import at.ahls.database.DBConnectionController;
import at.ahls.model.ActivityLogModel;
import at.ahls.model.UserModel;
import at.ahls.web.rest.api.jaxb.ActivitiesDto;
import at.ahls.web.rest.api.jaxb.ActivityDto;
import at.ahls.web.rest.api.jaxb.ObjectFactory;

public class UserController {

	private static UserController _controller;
	
	private UserController() {}
	
	public static UserController getInstance() {
		if (_controller == null) {
			_controller = new UserController();
		}
		
		return _controller;
	}
	
	public UserModel getUser() {
		return null;
	}
	
	/**
	 * Creates and inserts a new user.
	 * @return If successful: unique user name, null otherwise.
	 */
	public String createNewUser() {
		UserModel user = new UserModel(createNewUsername());
		String sql = user.createInsertSQL();
		
		Statement statement = null;
		try {
			statement = DBConnectionController.getInstance().getConnection().createStatement();
			int result = statement.executeUpdate(sql);
			
			System.out.println();
			return (result == 1) ? user.getUsername() : null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
	}
	
	public UserModel getUser(String username) {
		System.out.println("UserController: getUser: trying to fetch data");
		ResultSet result = null;
		try {
			Statement statement = DBConnectionController.getInstance().getConnection().createStatement();
			String queryString = UserModel.createSelectSQL(username);

			result = statement.executeQuery(queryString);
			UserModel user = new UserModel();

			int count = 0;
			while (result.next()) {
				count++;
				System.out.println("UserController: getUser: found " + count + " with username '" + username + "'.");
				user.setId(result.getInt("id"));
				user.setUsername(result.getString("username"));
				user.setRegistered(result.getTimestamp("registered"));
			}
			
			if (count == 0) {
				throw new IllegalArgumentException("No user found.");
			}
			
			if (count > 1) {
				throw new IllegalArgumentException("More than one user");
			}

			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}

		return null;
	}
	
	private String createNewUsername() {
		String username = "username" + (countUsers() + 1);
		System.out.println(username);
		String hashedUsername = toSHA1(username.getBytes());

		return hashedUsername;
	}
	
	private int countUsers() {
		ResultSet result = null;
		
		try {
			int count = -1;
			Statement statement = DBConnectionController.getInstance().getConnection().createStatement();
			String queryString = "SELECT count(id) " 
					+ "FROM " + "user";

			result = statement.executeQuery(queryString);
			
			while (result.next()) {
				count = result.getInt(1);
			}

			return count;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					// ignore
				}
			}

		}
		
		return -1;
	}
	
	public static String toSHA1(byte[] convertme) {
	    MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
	    
	    return byteArrayToHexString(md.digest(convertme));
	}
	
	public static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
	}
	
}
