package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import model.MyHelper;

public class DAOUserToken {
	private DAOUserToken() {
	}
	
	private static final Object sLockObj = new Object();
	private static long lastCreanupTick = 0L;
	
	private static final String SQL_INSERT_USERTOKEN = 
			"INSERT INTO m_usertoken(user_id, user_token, user_token_limit) VALUES (?, ?, ?)";
	
	private static final String SQL_FIND_USER_TOKEN = 
			"SELECT * FROM m_usertoken WHERE user_token = ?";
	
	private static final String SQL_DELETE_OLD_TOKEN = 
			"DELETE FROM `m_usertoken` WHERE user_token_limit < NOW()";
	
	public static int addUserToken(String userId, String userToken, Date userTokenLimit) throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL_INSERT_USERTOKEN)) {
			pstmt.setString(1, userId);
			pstmt.setString(2, userToken);
			pstmt.setString(3, MyHelper.toUTCTimeString(userTokenLimit));
			return pstmt.executeUpdate();
		}
	}
	
	public static String getUserIdFromUserToken(String userToken) throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL_FIND_USER_TOKEN)) {
			pstmt.setString(1, userToken);
			try (ResultSet res = pstmt.executeQuery()) {
				if (res.next()) {
					String user_id = res.getString("user_id");
					return user_id;
				}
			}
		}
		return null;
	}

	public static int cleanup() throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL_DELETE_OLD_TOKEN)) {
			return pstmt.executeUpdate();
		}
	}
}
