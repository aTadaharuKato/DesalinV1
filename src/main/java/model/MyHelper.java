package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.InputStream;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;

public final class MyHelper {
	private MyHelper() {
	}
	
	/** アクセストークンの有効期限の分数です. */
	public static final int CONFIG_ACCESSTOKEN_VALIDTIME_IN_MINUTE = 30;
	
	public static final int CONFIG_USERTOKEN_VALIDTIME_IN_MINUTE = 60;

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	private static final SimpleDateFormat SDFX = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X");
	private static final SimpleDateFormat SDF_UTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static {
		SDF_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	
	private static final char CH[] = { 
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
			'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' 
	};
	private static final int CH_LENGTH = CH.length; 
	private static final Object lockObj = new Object();
	private static long MY_LFSR = System.currentTimeMillis();

	/**
	 * アクセストークン，リフレッシュトークン用の，でたらめな文字列を生成します。
	 * 文字列は，M 系乱数を用いて，0~9, A-Z の文字を使用した，32 文字から生成します. 
	 * N=64， タップ位置 64, 63, 61, 60
	 * 周期は 2^64-1
	 */
	static String generateToken() {
		char[] token = new char[32];
		int dstindex = 0;
		int chindex = 0;
		while (true) {
			synchronized (lockObj) {
				for (int j = 0; j < 64; j++) {
					MY_LFSR = (MY_LFSR >>> 1) 
							    ^ (-(MY_LFSR & 1) & ((1L << (64 - 1)) | (1L << (63 - 1)) | (1L << (61 - 1)) | (1L << (60 - 1))));
				}
			}
			long v = MY_LFSR;
			System.out.println("MyHelper.generateToken, v: " + Long.toHexString(v));
			for (int k = 0; k < (64 / 4); k++, v = (v >> 4)) {
				int offval = (int) (v & 0xF);
				char ch = CH[((chindex + offval) % CH_LENGTH)];
				token[dstindex++] = ch;
				chindex = (chindex + offval + 1) % CH_LENGTH;
				if (dstindex == token.length) {
					String token_str = new String(token);
					//System.out.println("token_str: " + token_str);
					return token_str;
				}
			}
		}
	}
	
	public static String toUTCTimeString(java.util.Date date) {
		return SDF_UTC.format(date);
	}
	
	public static java.util.Date getDatefromDateTimeStringWithTZ(String dateTimeStr) {
		try {
			return SDF.parse(dateTimeStr);
		} catch (ParseException e) {
			try {
				return SDFX.parse(dateTimeStr);
			} catch (ParseException e1) {
				return null;
			}
		}	
	}

	
	
	
	public static String getDeviceNameFromURI(String requestURI, String pattern) {
		int lastIndex = requestURI.lastIndexOf(pattern);
		String deviceName = requestURI.substring(lastIndex + pattern.length());
		return deviceName;
	}

	
	
	public static JSONObject getJsonObjectFromRequestBody(HttpServletRequest request) throws IOException {
		try {
			String instr = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			System.out.println("instr:" + instr);

			// リクエストボディのバイト列より，ｌJSON オブジェクトを生成.
			JSONObject jsonobj = new JSONObject(instr);
			System.out.println("jsonobj:" + jsonobj);
			
			return jsonobj;

		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException();
		}
		
		
//		// リクエストボディのバイト列を取得.
//		int contentLength = request.getContentLength();
//		byte[] ba = new byte[contentLength];
//		try (InputStream is = request.getInputStream()) {
//			int rdsz;
//			for (int rdpos = 0; rdpos < contentLength; rdpos += rdsz) {
//				rdsz = is.read(ba, 0, contentLength - rdpos);
//				if (rdsz == -1) {
//					// 予期しない EOF を検出.
//					throw new IOException("unexpected requestbody length!");
//				}
//			}
//		}
//			
//		// リクエストボディのバイト列を文字列に変換.
//		String instr = new String(ba, "UTF-8");
//		System.out.println("instr:" + instr);
//		
//		// リクエストボディのバイト列より，ｌJSON オブジェクトを生成.
//		JSONObject jsonobj = new JSONObject(instr);
//		System.out.println("jsonobj:" + jsonobj);
//		
//		return jsonobj;
	}
}
