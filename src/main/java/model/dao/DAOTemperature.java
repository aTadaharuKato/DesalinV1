package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import model.MyHelper;
import model.ElemTemperature;

public class DAOTemperature {
	private DAOTemperature() {
	}
	static final String SQL1 = "INSERT INTO t_temperature(device_id, temperature, rec_dt) VALUES(?, ?, ?)";
	
	
	public static int registNewData(String deviceId, double temperature, java.util.Date recDate) throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL1)) {
			pstmt.setString(1, deviceId);
			pstmt.setFloat(2, (float) temperature);
			pstmt.setString(3, MyHelper.toUTCTimeString(recDate));
			return pstmt.executeUpdate();
		}
	}


	public static int registNewDataArray(String deviceId, ArrayList<ElemTemperature> tempArray) throws Exception {
		int cnt = 0;	// 処理件数の初期化
		try (Connection con = MyConnection.getConnection()) {
			try (PreparedStatement pstmt = con.prepareStatement(SQL1)) {
				con.setAutoCommit(false);
				for (ElemTemperature elem : tempArray) {
					pstmt.setString(1, deviceId);
					pstmt.setFloat(2, (float) elem.getTemperature());
					pstmt.setString(3,  MyHelper.toUTCTimeString(elem.getDatetime()));
					cnt += pstmt.executeUpdate();
				}
				con.commit();
			} catch (Exception e) {
				System.out.println("Exception occurred in TemperatureDAO#registNewDataArray(), rollback update.");
				con.rollback();
				throw e;
			}
		}	
		return cnt;
	}
}
