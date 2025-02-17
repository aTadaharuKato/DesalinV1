package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import model.MyHelper;

public class DAODevice {
	private DAODevice() {
	}
	
	static final String SQL1 = "SELECT password FROM m_device WHERE device_id = ?";
	static final String SQL2 = "SELECT access_token  FROM m_device WHERE device_id = ?";
	static final String SQL6 = "SELECT refresh_token  FROM m_device WHERE device_id = ?";
	static final String SQL3 = "SELECT update_dt  FROM m_device WHERE device_id = ?";
	static final String SQL4 = "UPDATE m_device SET update_dt= ? WHERE device_id = ?";
	static final String SQL5 = "UPDATE m_device SET access_token = ?, access_token_limit = ?,"
							 + " refresh_token = ?, refresh_token_limit = ? WHERE device_id = ?";
	static final String SQL7 = "SELECT access_token_limit  FROM m_device WHERE device_id = ?";
	static final String SQL8 = "SELECT refresh_token_limit  FROM m_device WHERE device_id = ?";
	
	public static String getPasswordByDeviceId(String deviceId) throws Exception {
		String password = null;
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt_sql1 = con.prepareStatement(SQL1)) {
			pstmt_sql1.setString(1, deviceId);
			try (ResultSet res = pstmt_sql1.executeQuery()) {
				if (res.next()) {
					password = res.getString("password");
				} else {
					throw new Exception("The specified device ID is not registered.");
				}
			}
		}
		return password;
	}
	
	public static String getAccessTokenByDeviceId(String deviceId) throws Exception {
		String accessToken = null;
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL2)) {
			pstmt.setString(1, deviceId);
			try (ResultSet res = pstmt.executeQuery()) {
				if (res.next()) {
					accessToken = res.getString("access_token");
				} else {
					throw new Exception("The specified device ID is not registered.");
				}
			}
		}
		return accessToken;
	}
	public static String getRefreshTokenByDeviceId(String deviceId) throws Exception {
		String refreshToken = null;
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL6)) {
			pstmt.setString(1, deviceId);
			try (ResultSet res = pstmt.executeQuery()) {
				if (res.next()) {
					refreshToken = res.getString("refresh_token");
				} else {
					throw new Exception("The specified device ID is not registered.");
				}
			}
		}
		return refreshToken;
	}
	
	
	public static int updateTokens(String deviceId, String accessToken, java.util.Date accessTokenLimit, String refreshToken, java.util.Date refreshTokenLimit) throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL5)) {
			pstmt.setString(1, accessToken);
			pstmt.setString(2, MyHelper.toUTCTimeString(accessTokenLimit));
			pstmt.setString(3, refreshToken);
			pstmt.setString(4, MyHelper.toUTCTimeString(refreshTokenLimit));
			pstmt.setString(5, deviceId);
			return pstmt.executeUpdate();
		}
	}
	
	
	/**
	 * m_device テーブルの，device_id のレコードの，update_dt を更新します.
	 * これは java.util.Date インスタンスから，	SQL の datetime データ型を書き換えるお試し用のメソッドで，おそらく試作のみのメソッドです. 
	 * @param deviceId 書き換えるレコードのデバイスID を指定します.
	 * @param update_dt 書き換える日時を保持する java.util.Date インスタンスを指定します.
	 * @return 成功時，1 を返却します.
	 * @throws Exception
	 */
	public static int setUpdateTime(String deviceId, java.util.Date update_dt) throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL4)) {
			pstmt.setString(1, MyHelper.toUTCTimeString(update_dt));
			pstmt.setString(2, deviceId);
			return pstmt.executeUpdate();
		}
	}
	
	
	/**
	 * m_device テーブルの，device_id のレコードの update_dt を取得します.
	 * これは，SQL の datetime データ型よりに日時を取得して，java.util.Date クラスのインスタンスとして返却するお試し用のメソッドで，おそらく試作のみのメソッドです.
	 * SQL 上の datetime は，UTC であることを想定しています.
	 * @param deviceId 取得するレコードのデバイスID を指定します.
	 * @return レコードの update_dt の示す日時を，java.util.Date クラスのインスタンスとして返却します.
	 * @throws Exception
	 */
	public static java.util.Date getUpdateDateTimeByDeviceId(String deviceId) throws Exception {
		java.util.Date update_dt;
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL3)) {
			pstmt.setString(1, deviceId);
			try (ResultSet res = pstmt.executeQuery()) {
				if (res.next()) {
					String update_dts = res.getString("update_dt");
					//System.out.println("update_dt (String):" + update_dts);
					update_dt = MyHelper.getDatefromDateTimeStringWithTZ(update_dts + " UTC");
				} else {
					throw new Exception("The specified device ID is not registered.");
				}
			}
		}
		return update_dt;
	}
	
	public static java.util.Date getAccessTokenLimitByDeviceId(String deviceId) throws Exception {
		java.util.Date access_token_limit;
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL7)) {
			pstmt.setString(1, deviceId);
			try (ResultSet res = pstmt.executeQuery()) {
				if (res.next()) {
					String update_dts = res.getString("access_token_limit");
					access_token_limit = MyHelper.getDatefromDateTimeStringWithTZ(update_dts + " UTC");
				} else {
					throw new Exception("The specified device ID is not registered.");
				}
			}
		}
		return access_token_limit;
	}

	public static java.util.Date getRefreshTokenLimitByDeviceId(String deviceId) throws Exception {
		java.util.Date refresh_token_limit;
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL8)) {
			pstmt.setString(1, deviceId);
			try (ResultSet res = pstmt.executeQuery()) {
				if (res.next()) {
					String update_dts = res.getString("refresh_token_limit");
					refresh_token_limit = MyHelper.getDatefromDateTimeStringWithTZ(update_dts + " UTC");
				} else {
					throw new Exception("The specified device ID is not registered.");
				}
			}
		}
		return refresh_token_limit;
	}

}
