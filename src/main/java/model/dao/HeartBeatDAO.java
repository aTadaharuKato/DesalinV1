package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import model.MyHelper;

public final class HeartBeatDAO {
	private HeartBeatDAO() {
	}
	static final String SQL1 = "INSERT INTO t_heartbeat(device_id, heartbeat, rec_dt) VALUES(?, ?, ?)";
	
	public static int registNewData(String deviceId, int heartbeat, java.util.Date recDate) throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL1)) {
			pstmt.setString(1, deviceId);
			pstmt.setInt(2, heartbeat);
			pstmt.setString(3, MyHelper.toUTCTimeString(recDate));
			return pstmt.executeUpdate();
		}
	}
}
