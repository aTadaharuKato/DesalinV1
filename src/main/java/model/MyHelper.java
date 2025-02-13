package model;

public final class MyHelper {

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
}
