package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import model.MyHelper;

public class TemperatureDAO {
	private TemperatureDAO() {
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
}
