package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import model.ElemHeartBeat;
import model.MyHelper;
import model.ElemTemperature;

public final class DAOHeartBeat {
	private DAOHeartBeat() {
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

	public static int registNewDataArray(String deviceId, ArrayList<ElemHeartBeat> hbeatArray) throws Exception {
		int cnt = 0;	// 処理件数の初期化
		try (Connection con = MyConnection.getConnection()) {
			try (PreparedStatement pstmt = con.prepareStatement(SQL1)) {
				con.setAutoCommit(false);
				for (ElemHeartBeat elem : hbeatArray) {
					pstmt.setString(1, deviceId);
					pstmt.setFloat(2, (float) elem.getHeartbeat());
					pstmt.setString(3,  MyHelper.toUTCTimeString(elem.getDatetime()));
					cnt += pstmt.executeUpdate();
				}
				con.commit();
			} catch (Exception e) {
				System.out.println("Exception occurred in HeartBeatDAO#registNewDataArray(), rollback update.");
				con.rollback();
				throw e;
			}
		}	
		return cnt;
	}
}
