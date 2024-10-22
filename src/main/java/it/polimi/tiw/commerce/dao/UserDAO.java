package it.polimi.tiw.commerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.commerce.beans.User;

public class UserDAO {
	
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User checkCredentials(String email, String password) throws SQLException {
		String query = "SELECT * FROM user WHERE email = ? AND password = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, email);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();
			if (result.next() && password.equals(result.getString("password"))) {
				User user = new User();
				user.setUserID(result.getInt("userID"));
				user.setName(result.getString("name"));
				user.setSurname(result.getString("surname"));
				user.setAddress(result.getString("address"));
				return user;
			}
			else 
				return null;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
	}
}
