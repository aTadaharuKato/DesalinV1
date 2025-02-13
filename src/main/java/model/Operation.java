package model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONObject;
import model.dao.DeviceDAO;
import model.dao.HeartBeatDAO;
import model.dao.TemperatureDAO;

public final class Operation {
	private Operation() {
	}
	
	public static final int SUCCESS_ACCESSTOKEN_CHECK = 1;
	public static final int ERROR_ACCESSTOKEN_NOT_EXIST = 1;
	public static final int ERROR_ACCESSTOKEN_NOT_MATCH = 2;
	public static final int ERROR_ACCESSTOKEN_IS_EXPIRED = 3;


	public static boolean checkAccessToken(String deviceName, String token, JSONObject myJsonObj) throws Exception {
		
		// TODO 自動生成されたメソッド・スタブ
		String accessToken = DeviceDAO.getAccessTokenByDeviceId(deviceName);
		if (accessToken == null) {
			myJsonObj.putOpt("result", "failed, Access token not exist.");
			return false;
		}
		if (!accessToken.equals(token)) {
			myJsonObj.putOpt("result", "failed, Access token not match.");
			return false;
		}
		Date accessTokenLimit = DeviceDAO.getAccessTokenLimitByDeviceId(deviceName);
		if (accessTokenLimit.before(new Date())) {
			myJsonObj.putOpt("result", "failed, Access token is Expired.");
			return false;
		}
		return true;
	}

	public static JSONObject issueAccessTokenFromPassword(String deviceName, String password) throws Exception {
		
		JSONObject myJsonObj = new JSONObject();
		String accessToken = DeviceDAO.getAccessTokenByDeviceId(deviceName);
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
		String registed_password = DeviceDAO.getPasswordByDeviceId(deviceName);
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
		
		DeviceDAO.updateTokens(deviceName, newAccessToken, accessTokenLimitDate, newRefreshToken, refreshTokenLimitDate);
		
		
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
		
		String curRefreshToken = DeviceDAO.getRefreshTokenByDeviceId(deviceName);
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
		Date accessTokenLimit = DeviceDAO.getRefreshTokenLimitByDeviceId(deviceName);
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
			TemperatureDAO.registNewData(deviceName, temperature, datetime);
		}
		if (heartbeat != Integer.MIN_VALUE) {
			HeartBeatDAO.registNewData(deviceName, heartbeat, datetime);
		}
		myJsonObj.putOpt("result", "success");
		return myJsonObj;
	}
}
