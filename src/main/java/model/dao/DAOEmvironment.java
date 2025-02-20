package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import model.ElemEnvironmentSensor;
import model.MyHelper;

public final class DAOEmvironment {
	private DAOEmvironment() {
	}
	static final String SQL1 = "INSERT INTO t_environment(device_id, temperature, humidity, pressure, rec_dt) VALUES(?, ?, ?, ?, ?)";
	
	static final String SQL2 = "SELECT * FROM t_environment WHERE device_id = ? ORDER BY rec_id DESC LIMIT 1";
	
	
	public static ElemEnvironmentSensor getLastDataByDeviceId(String deviceId) throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL2)) {
			pstmt.setString(1, deviceId);
			try (ResultSet res = pstmt.executeQuery()) {
				if (res.next()) {
					//String deviceId = res.getString("device_id");
					double temperature = res.getFloat("temperature");
					int humidity = res.getInt("humidity");
					double pressure = res.getFloat("pressure");
					String dtstr = res.getString("rec_dt");
					
					temperature = Math.round(temperature * 10.0) / 10.0;
					pressure = Math.round(pressure * 10.0) / 10.0;
					//System.out.println("temperature:" + temperature);
					//System.out.println("humidity:" + humidity);
					//System.out.println("pressure:" + pressure);
					//System.out.println("dtstr:" + dtstr);
					Date dt = MyHelper.getDatefromDateTimeStringWithTZ(dtstr + " UTC");
					//System.out.println("dt:" + dt);
					return new ElemEnvironmentSensor(temperature, humidity, pressure, dt);
				} else {
					throw new Exception("The specified device ID is not registered.");
				}
			}
		}
	}
	
	public static int registNewData(String deviceId, double temperature, int humidity, double pressure, java.util.Date recDate) throws Exception {
		try (Connection con = MyConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(SQL1)) {
			pstmt.setString(1, deviceId);
			pstmt.setFloat(2, (float) temperature);
			pstmt.setInt(3,  humidity);
			pstmt.setFloat(4,  (float) pressure);
			pstmt.setString(5, MyHelper.toUTCTimeString(recDate));
			return pstmt.executeUpdate();
		}
	}


	public static int registNewDataArray(String deviceId, ArrayList<ElemEnvironmentSensor> envArray) throws Exception {
		int cnt = 0;	// 処理件数の初期化
		try (Connection con = MyConnection.getConnection()) {
			try (PreparedStatement pstmt = con.prepareStatement(SQL1)) {
				con.setAutoCommit(false);
				for (ElemEnvironmentSensor elem : envArray) {
					pstmt.setString(1, deviceId);
					pstmt.setFloat(2, (float) elem.getTemperature());
					pstmt.setInt(3,  elem.getHumidity());
					pstmt.setFloat(4,  (float) elem.getPressure());
					pstmt.setString(5,  MyHelper.toUTCTimeString(elem.getDatetime()));
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

