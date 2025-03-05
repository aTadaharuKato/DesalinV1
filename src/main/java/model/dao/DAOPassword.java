package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class DAOPassword {
	
	private DAOPassword() {
	}

	private static final String SQL_GET_PASSWD_BY_USERID = "SELECT password FROM m_password WHERE user_id = ?";

	public static String getPasswordByUserId(String userId) throws Exception {
		String password = null;
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt_sql1 = con.prepareStatement(SQL_GET_PASSWD_BY_USERID)) {
			pstmt_sql1.setString(1, userId);
			try (ResultSet res = pstmt_sql1.executeQuery()) {
				if (res.next()) {
					password = res.getString("password");
				//} else {
				//	throw new Exception("The specified user ID is not registered.");
				}
			}
		}
		return password;
	}
}
