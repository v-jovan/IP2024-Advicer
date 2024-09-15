package net.etfbl.ip.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import net.etfbl.ip.beans.UserBean;
import net.etfbl.ip.utils.ConnectionPool;
import net.etfbl.ip.utils.SQLQueries;

public class UserManager {
	public UserBean loginUser(String username, String password) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserBean user = null;

		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(SQLQueries.LOGIN_USER);
			ps.setString(1, username);
			rs = ps.executeQuery();

			if (rs.next()) {
				// Retrieve the hashed password from the database
				String storedHash = rs.getString("password");

				// Check if the entered password matches the hashed password in the database
				if (BCrypt.checkpw(password, storedHash)) {
					user = new UserBean();
					user.setId(rs.getInt("id"));
					user.setUsername(rs.getString("username"));
					user.setEmail(rs.getString("email"));
					user.setRole(rs.getString("role"));
					user.setActivated(rs.getBoolean("is_activated"));
					user.setLoggedIn(true); // Mark the user as logged in
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					ConnectionPool.getInstance().checkIn(conn);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return user; // Return the authenticated user or null if authentication fails
	}

	/**
	 * Retrieves a user by their ID. Returns a UserBean populated with user details
	 * or null if the user is not found
	 */
	public UserBean getUserById(int id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserBean user = null;

		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(SQLQueries.GET_USER_BY_ID);
			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				user = new UserBean();
				user.setId(rs.getInt("id"));
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setActivated(rs.getBoolean("is_activated"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					ConnectionPool.getInstance().checkIn(conn);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return user;
	}
}
