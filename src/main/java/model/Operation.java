package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.http.HttpSession;
import model.dao.DAODevice;
import model.dao.DAOEmvironment;
import model.dao.DAOPassword;
import model.dao.DAOUserToken;

public final class Operation {
	private Operation() {
	}
	
	public static final int SUCCESS_ACCESSTOKEN_CHECK = 1;
	public static final int ERROR_ACCESSTOKEN_NOT_EXIST = 1;
	public static final int ERROR_ACCESSTOKEN_NOT_MATCH = 2;
	public static final int ERROR_ACCESSTOKEN_IS_EXPIRED = 3;

	
	/**
	 * デバイスの所有者をデータベースより読み出し，それが引数の userId と一致するか確認します。
	 * 一致しない場合は，false を返却します。
	 * 一致する場合は，ユーザパスワードを確認します。パスワードも一致する場合は true を返却します。
	 * @param userId
	 * @param password
	 * @param deviceName
	 * @param myJsonObj
	 * @return
	 * @throws Exception
	 */
	private static boolean checkUserAndDevice(String userId, String password, String deviceName, JSONObject myJsonObj) throws Exception {
		String owner = DAODevice.getOwnerByDeviceId(deviceName);
		//System.out.println("owner:" + owner);
		if (!owner.equals(userId)) {
			// ユーザ名が，デバイスの所有者と一致しない場合.
			myJsonObj.putOpt("result", "failed, The user is not the device owner.");
			return false;
		}
		return checkUser(userId, password, myJsonObj);
		/*
		String original_password = DAOPassword.getPasswordByUserId(userId);
		//System.out.println("original_password:" + original_password);
		if (original_password == null) {
			myJsonObj.putOpt("result", "failed, The user is not exist.");
		}
		if (!original_password.equals(password)) {
			// パスワードが一致しない.
			myJsonObj.putOpt("result", "failed, Passwords do not match.");
			return false;
		}
		return true;
		*/
	}
	
	private static boolean checkUserTokenAndDevices(String userToken, String[] deviceNames, JSONObject myJsonObj) throws Exception {
		String userId = DAOUserToken.getUserIdFromUserToken(userToken);
		if (userId == null) {
			myJsonObj.putOpt("result", "failed, The specified user token does not exist or has expired.");
		}
		for (int i = 0; i < deviceNames.length; i++) {
			String owner = DAODevice.getOwnerByDeviceId(deviceNames[i]);
			if (!owner.equals(userId)) {
				// ユーザ名が，デバイスの所有者と一致しない場合.
				myJsonObj.putOpt("result", "failed, The user is not the device owner.");
				return false;
			}
		}
		return true;
	}
	
	
	private static boolean checkUser(String userId, String password, JSONObject myJsonObj) throws Exception {
		String original_password = DAOPassword.getPasswordByUserId(userId);
		if (original_password == null) {
			myJsonObj.putOpt("result", "failed, The user is not exist.");
			return false;
		}
		if (!original_password.equals(password)) {
			// パスワードが一致しない.
			myJsonObj.putOpt("result", "failed, Passwords do not match.");
			return false;
		}
		return true;
	}
	

	private static boolean checkAccessToken(String deviceName, String token, JSONObject myJsonObj) throws Exception {
		
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

	/**
	 * デバイスのアクセストークンを発行します。
	 * @param deviceName デバイス名
	 * @param password パスワード
	 * @return
	 * @throws Exception
	 */
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

//	public static JSONObject registNewData(String deviceName, String token, Date datetime, double temperature, int heartbeat) throws Exception {
//		JSONObject myJsonObj = new JSONObject();
//		if (checkAccessToken(deviceName, token, myJsonObj) == false) {
//			return myJsonObj;
//		}
//		if (temperature != Double.NaN) {
//			DAOTemperature.registNewData(deviceName, temperature, datetime);
//		}
//		if (heartbeat != Integer.MIN_VALUE) {
//			DAOHeartBeat.registNewData(deviceName, heartbeat, datetime);
//		}
//		myJsonObj.putOpt("result", "success");
//		return myJsonObj;
//	}

//	public static JSONObject registNewDataArray(String deviceName, String token, JSONArray array) throws Exception {
//		System.out.println("Operation#registNewDataArray()");
//		JSONObject myJsonObj = new JSONObject();
//		if (checkAccessToken(deviceName, token, myJsonObj) == false) {
//			return myJsonObj;
//		}
//		
//		
//		System.out.println("array:" + array);
//		System.out.println("array length:" + array.length());
//		ArrayList<ElemTemperature> tempArray = new ArrayList<>();
//		ArrayList<ElemHeartBeat> hbeatArray = new ArrayList<>();
//		for (int i = 0; i < array.length(); i++) {
//			JSONObject obj = array.getJSONObject(i);
//			System.out.println("[" + i + "] " + obj);
//			Date datetime = MyHelper.getDatefromDateTimeStringWithTZ(obj.optString("datetime"));
//			if (datetime == null) {
//				throw new Exception("bad datetime");
//			}
//			double temperature = obj.optDouble("temperature", Double.NaN);
//			if (temperature != Double.NaN) {
//				tempArray.add(new ElemTemperature(temperature, datetime));
//			}
//			int heartbeat = obj.optInt("heartbeat", Integer.MIN_VALUE);
//			if (heartbeat != Integer.MIN_VALUE) {
//				hbeatArray.add(new ElemHeartBeat(heartbeat, datetime));
//			}
//			System.out.println("datetime:" + datetime);
//		}
//		System.out.println("tempArray:" + tempArray);
//		System.out.println("hbeatArray:" + hbeatArray);
//		if (!tempArray.isEmpty()) {
//			DAOTemperature.registNewDataArray(deviceName, tempArray);
//		}
//		if (!hbeatArray.isEmpty()) {
//			DAOHeartBeat.registNewDataArray(deviceName, hbeatArray);
//		}
//		myJsonObj.putOpt("result", "success");
//		return myJsonObj;
//	}

	public static JSONObject registNewEnvDataArray(String deviceName, String token, JSONArray array) throws Exception {
		System.out.println("Operation#registNewEnvDataArray()");
		JSONObject myJsonObj = new JSONObject();
		if (checkAccessToken(deviceName, token, myJsonObj) == false) {
			return myJsonObj;
		}
		System.out.println("array:" + array);
		System.out.println("array length:" + array.length());
		ArrayList<ElemEnvironmentSensor> envArray = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			System.out.println("[" + i + "] " + obj);
			Date datetime = MyHelper.getDatefromDateTimeStringWithTZ(obj.optString("datetime"));
			double temperature = obj.optDouble("temperature", Double.NaN);
			int humidity = obj.optInt("humidity", Integer.MIN_VALUE);
			double pressure = obj.optDouble("pressure", Double.NaN);
			if ((datetime == null) || (temperature == Double.NaN) || (humidity == Integer.MIN_VALUE) || (pressure == Double.NaN)) {
				throw new Exception("bad datetime");
			}
			envArray.add(new ElemEnvironmentSensor(temperature, humidity, pressure, datetime));
		}
		DAOEmvironment.registNewDataArray(deviceName, envArray);
		myJsonObj.putOpt("result", "success");
		return myJsonObj;
	}

	public static JSONObject getLastEnvData(String userId, String password, String deviceName) throws Exception {
		System.out.println("Operation#getLastEnvData()");
		JSONObject myJsonObj = new JSONObject();
		
		if (checkUserAndDevice(userId, password, deviceName, myJsonObj) == false) {
			return myJsonObj;
		}
		ElemEnvironmentSensor rec = DAOEmvironment.getLastDataByDeviceId(deviceName);
		myJsonObj.put("temperature", rec.getTemperature());
		myJsonObj.put("humidity", rec.getHumidity());
		myJsonObj.put("pressure", rec.getPressure());
		myJsonObj.put("datetime", MyHelper.toUTCTimeString(rec.getDatetime()) + " UTC");

		myJsonObj.putOpt("result", "success");
		return myJsonObj;
	}
	
	public static JSONObject getLastEnvDataMulti(String userToken, String[] deviceNames, HttpSession session) throws Exception {
		System.out.println("Operation#getLastEnvDataMulti()");
		JSONObject myJsonObj = new JSONObject();

		// データベースのアクセスを減らすため，セッションスコープに既知のユーザトークンとデバイス名を保存しておく。
		// それと引数が一致するならば，checkUserTokenAndDevices() を省略できる。
		boolean fMatchKnownDevNames = false;
		String knownUserToken = (String) session.getAttribute("userToken");
		if ((knownUserToken != null) && (knownUserToken.equals(userToken))) {
			String[] knownDevNames = (String[]) session.getAttribute("deviceNames");
			System.out.println("knownDeviceNames:" + knownDevNames);
			if ((knownDevNames != null) && (knownDevNames.length == deviceNames.length)) {
				fMatchKnownDevNames = true;
				for (int i = 0; i < knownDevNames.length; i++) {
					if (knownDevNames[i].equals(deviceNames[i]) == false) {
						fMatchKnownDevNames = false;
						break;
					}
				}
			}
		}
		System.out.println("fMatchKnownDevNames:" + fMatchKnownDevNames);
		if (fMatchKnownDevNames == false) {
			if (checkUserTokenAndDevices(userToken, deviceNames, myJsonObj) == false) {
				return myJsonObj;
			}
			session.setAttribute("userToken", userToken);
			session.setAttribute("deviceNames", deviceNames);
		}
		JSONArray recArray = new JSONArray();
		//ElemEnvironmentSensor[] recArray = new ElemEnvironmentSensor[deviceNames.length];
		for (int i = 0; i < deviceNames.length; i++) {
			ElemEnvironmentSensor rec = DAOEmvironment.getLastDataByDeviceId(deviceNames[i]);
			JSONObject elem = new JSONObject();
			elem.put("deviceId",  deviceNames[i]);
			elem.put("temperature", rec.getTemperature());
			elem.put("humidity", rec.getHumidity());
			elem.put("pressure", rec.getPressure());
			elem.put("datetime", MyHelper.toUTCTimeString(rec.getDatetime()) + " UTC");
			recArray.put(elem);
		}
		myJsonObj.put("envdatas", recArray);
		myJsonObj.putOpt("result", "success");
		return myJsonObj;
	}
	


	private static long lastCleanupTick = 0L;
	
	public static JSONObject userLogin(String userId, String password) throws Exception {
		System.out.println("Operation#userLogin(userId:" + userId + ", password:" + password + ")");
		JSONObject myJsonObj = new JSONObject();
		
		if (checkUser(userId, password, myJsonObj) == false) {
			return myJsonObj;
		}
		
		String userToken = MyHelper.generateToken();
		
		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// アクセストークンの期限は，テスト用に短く 3 分を設定します.
		Date userTokenLimitDate = new Date(utc.getTimeInMillis() + (MyHelper.CONFIG_USERTOKEN_VALIDTIME_IN_MINUTE * 60L * 1000L));
		
		DAOUserToken.addUserToken(userId, userToken, userTokenLimitDate);
		List<String> deviceNames = DAODevice.getListOfDeviceNamesOwnedbySpecifiedUser(userId);
		myJsonObj.putOpt("devices", deviceNames);
		myJsonObj.putOpt("token", userToken);
		myJsonObj.putOpt("result", "success");
		
		// ======= 期限切れのユーザトークンを削除する処理. 
		long curTick = System.currentTimeMillis();
		long elapsed = curTick - lastCleanupTick;
		if (elapsed > 3000L) {
			// 頻繁に実行するのもアレなので，前回の実行から 3sec 実行している場合のみ，クリーンナップを実行する.
			lastCleanupTick = curTick;
			new Thread() {
				public void run() {
					System.out.println("UserTolen Cleanup Thread started.");
					try {
						DAOUserToken.cleanup();
					} catch (Throwable ignore) {}
				}
				
			}.start();
		}
		// ========
		
		return myJsonObj;
	}


	public static JSONObject userTokenLogin(String userToken) throws Exception {
		System.out.println("Operation#userTokenLogin(userToken:" + userToken + ")");
		
		JSONObject myJsonObj = new JSONObject();
		String userId = DAOUserToken.getUserIdFromUserToken(userToken);
		if (userId == null) {
			myJsonObj.putOpt("result", "failed, The specified user token does not exist or has expired.");
		} else {
			List<String> deviceNames = DAODevice.getListOfDeviceNamesOwnedbySpecifiedUser(userId);
			myJsonObj.putOpt("devices", deviceNames);
			myJsonObj.putOpt("userId", userId);
			myJsonObj.putOpt("result", "success");
		}
		return myJsonObj;
	}
}





