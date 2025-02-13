package model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;

import model.dao.DeviceDAO;

public class Operation {

	public static JSONObject getAccessTokenFromPassword(String deviceName, String password) throws Exception {
		
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
		
		String newAccessToken = MyHelper.generateToken();
		String newRefreshToken = MyHelper.generateToken();
		
		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		// アクセストークンの期限は，テスト用の短く 3 分を設定します.
		Date accessTokenLimitDate = new Date(utc.getTimeInMillis() + (3L * 60L * 1000L));
		
		// リフレッシュトークンの期限は，1年に設定しておきます.
		utc.set(Calendar.YEAR, utc.get(Calendar.YEAR) + 1);
		Date refreshTokenLimitDate = new Date(utc.getTimeInMillis());
		
		System.out.println("accessTokenLimitDate:" + accessTokenLimitDate);
		System.out.println("refreshTokenLimitDate:" + refreshTokenLimitDate);
		
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
		return myJsonObj;
	}

}
