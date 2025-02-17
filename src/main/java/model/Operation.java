package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import model.dao.DAODevice;
import model.dao.DAOHeartBeat;
import model.dao.DAOTemperature;

public final class Operation {
	private Operation() {
	}
	
	public static final int SUCCESS_ACCESSTOKEN_CHECK = 1;
	public static final int ERROR_ACCESSTOKEN_NOT_EXIST = 1;
	public static final int ERROR_ACCESSTOKEN_NOT_MATCH = 2;
	public static final int ERROR_ACCESSTOKEN_IS_EXPIRED = 3;


	public static boolean checkAccessToken(String deviceName, String token, JSONObject myJsonObj) throws Exception {
		
		// TODO 自動生成されたメソッド・スタブ
		String accessToken = DAODevice.getAccessTokenByDeviceId(deviceName);
		if (accessToken == null) {
			myJsonObj.putOpt("result", "failed, Access token not exist.");
			return false;
		}
		if (!accessToken.equals(token)) {
			myJsonObj.putOpt("result", "failed, Access token not match.");
			return false;
		}
		Date accessTokenLimit = DAODevice.getAccessTokenLimitByDeviceId(deviceName);
		if (accessTokenLimit.before(new Date())) {
			myJsonObj.putOpt("result", "failed, Access token is Expired.");
			return false;
		}
		return true;
	}

	public static JSONObject issueAccessTokenFromPassword(String deviceName, String password) throws Exception {
		
		JSONObject myJsonObj = new JSONObject();
		String accessToken = DAODevice.getAccessTokenByDeviceId(deviceName);
		System.out.println("accessToken:" + accessToken);
		if (accessToken != null) {
			// すでにアクセストークンが発行されている場合は，パスワードでのアクセストークン発行は出来ません。
			// リフレッシュトークンを用いて，アクセストークンを再発行するか，
			// 管理者にアクセストークンをクリアするよう依頼してください。
			myJsonObj.putOpt("result", "failed, An access token has already been issued."
					+ " Please use the refresh token to reissue the access token.");
			return myJsonObj;
		}
		
		// パスワードをチェックする.
		String registed_password = DAODevice.getPasswordByDeviceId(deviceName);
		System.out.println("registed_password: \"" + registed_password + "\"");
		System.out.println("password: \"" + password + "\"");
		System.out.println("registed_password == password:" + (registed_password == password));
		System.out.println("registed_password.equals(password):" + (registed_password.equals(password)));
		if (!registed_password.equals(password)) {
			// パスワードが一致しない場合は失敗.
			myJsonObj.putOpt("result", "failed, Invalid password.");
			return myJsonObj;
		}
		issueTokens(deviceName, myJsonObj);
		return myJsonObj;
	}
	
	
	private static void issueTokens(String deviceName, JSONObject myJsonObj) throws Exception {
		String newAccessToken = MyHelper.generateToken();
		String newRefreshToken = MyHelper.generateToken();
		
		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		// アクセストークンの期限は，テスト用に短く 3 分を設定します.
		Date accessTokenLimitDate = new Date(utc.getTimeInMillis() + (MyHelper.CONFIG_ACCESSTOKEN_VALIDTIME_IN_MINUTE * 60L * 1000L));
		
		// リフレッシュトークンの期限は，1年に設定しておきます.
		utc.set(Calendar.YEAR, utc.get(Calendar.YEAR) + 1);
		Date refreshTokenLimitDate = new Date(utc.getTimeInMillis());
		
		//System.out.println("accessTokenLimitDate:" + accessTokenLimitDate);
		//System.out.println("refreshTokenLimitDate:" + refreshTokenLimitDate);
		
		DAODevice.updateTokens(deviceName, newAccessToken, accessTokenLimitDate, newRefreshToken, refreshTokenLimitDate);
		
		
		/* update_dt の取得／更新の試験コード
		Date prev_updateDate = DeviceDAO.getUpdateDateTimeByDeviceId(deviceName);
		System.out.println("prev_updateDate:" + prev_updateDate);
		Date new_date = new Date(System.currentTimeMillis());
		System.out.println("new_date:" + new_date);
		DeviceDAO.setUpdateTime(deviceName, new_date);
		*/
		
		myJsonObj.putOpt("token", newAccessToken);
		myJsonObj.putOpt("refresh", newRefreshToken);
		myJsonObj.putOpt("result", "success");
	}

	public static JSONObject issueAccessTokenFromRefreshToken(String deviceName, String refreshtoken) throws Exception {
		JSONObject myJsonObj = new JSONObject();
		
		String curRefreshToken = DAODevice.getRefreshTokenByDeviceId(deviceName);
		System.out.println("curRefreshToken:" + curRefreshToken);
		if (curRefreshToken == null) {
			myJsonObj.putOpt("result", "failed, The refresh token is not set.");
			return myJsonObj;
		}
		if (!curRefreshToken.equals(refreshtoken)) {
			// リフレッシュトークンが一致しない場合は失敗.
			myJsonObj.putOpt("result", "failed, Invalid refresh token.");
			return myJsonObj;
		}
		Date accessTokenLimit = DAODevice.getRefreshTokenLimitByDeviceId(deviceName);
		if (accessTokenLimit.before(new Date())) {
			// リフレッシュトークンが期限切れの場合.
			myJsonObj.putOpt("result", "failed, Refresh Token is expired.");
			return myJsonObj;
		}
		
		issueTokens(deviceName, myJsonObj);
		return myJsonObj;
	}

	public static JSONObject registNewData(String deviceName, String token, Date datetime, Double temperature, int heartbeat) throws Exception {
		JSONObject myJsonObj = new JSONObject();
		if (checkAccessToken(deviceName, token, myJsonObj) == false) {
			return myJsonObj;
		}
		if (temperature != Double.NaN) {
			DAOTemperature.registNewData(deviceName, temperature, datetime);
		}
		if (heartbeat != Integer.MIN_VALUE) {
			DAOHeartBeat.registNewData(deviceName, heartbeat, datetime);
		}
		myJsonObj.putOpt("result", "success");
		return myJsonObj;
	}

	public static JSONObject registNewDataArray(String deviceName, String token, JSONArray array) throws Exception {
		System.out.println("Operation#registNewDataArray()");
		JSONObject myJsonObj = new JSONObject();
		if (checkAccessToken(deviceName, token, myJsonObj) == false) {
			return myJsonObj;
		}
		
		
		System.out.println("array:" + array);
		System.out.println("array length:" + array.length());
		ArrayList<ElemTemperature> tempArray = new ArrayList<>();
		ArrayList<ElemHeartBeat> hbeatArray = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			System.out.println("[" + i + "] " + obj);
			Date datetime = MyHelper.getDatefromDateTimeStringWithTZ(obj.optString("datetime"));
			if (datetime == null) {
				throw new Exception("bad datetime");
			}
			Double temperature = obj.optDouble("temperature", Double.NaN);
			if (temperature != Double.NaN) {
				tempArray.add(new ElemTemperature(temperature, datetime));
			}
			int heartbeat = obj.optInt("heartbeat", Integer.MIN_VALUE);
			if (heartbeat != Integer.MIN_VALUE) {
				hbeatArray.add(new ElemHeartBeat(heartbeat, datetime));
			}
			System.out.println("datetime:" + datetime);
		}
		System.out.println("tempArray:" + tempArray);
		System.out.println("hbeatArray:" + hbeatArray);
		if (!tempArray.isEmpty()) {
			DAOTemperature.registNewDataArray(deviceName, tempArray);
		}
		if (!hbeatArray.isEmpty()) {
			DAOHeartBeat.registNewDataArray(deviceName, hbeatArray);
		}
		myJsonObj.putOpt("result", "success");
		return myJsonObj;
	}
}





