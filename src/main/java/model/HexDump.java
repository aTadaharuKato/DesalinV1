package model;

import java.io.PrintStream;

public class HexDump {
	private HexDump() {
	}

	
	public static void dumpCode(PrintStream ps, byte[] b, int addr, int length) {
		// java ---> (byte) 0x13, 
		// Kotlin -> 0x13.toByte(),
		
		int column = 0;
		boolean linetop = true;
		for (int i = 0; i < length; i++) {
			System.out.print((linetop ? "0x" : " 0x") + HexDump.toHexStringUpper(b[i]) + ".toByte(),");
			//System.out.print((linetop ? "(byte) 0x" : " (byte) 0x") + HexDump.toHexStringUpper(b[i]) + ",");
			linetop = false;
			column++;
			if (column == 8) {
				column = 0;
				System.out.println();
				linetop = true;
			}
		}
		if (linetop == false) {
			System.out.println();
		}
	}
	
	public static void dumpCodeJ(PrintStream ps, byte[] b, int addr, int length) {
		// java ---> (byte) 0x13, 
		// Kotlin -> 0x13.toByte(),
		
		int column = 0;
		boolean linetop = true;
		for (int i = 0; i < length; i++) {
			//System.out.print((linetop ? "0x" : " 0x") + HexDump.toHexStringUpper(b[i]) + ".toByte(),");
			System.out.print((linetop ? "(byte) 0x" : " (byte) 0x") + HexDump.toHexStringUpper(b[i]) + ",");
			linetop = false;
			column++;
			if (column == 8) {
				column = 0;
				System.out.println();
				linetop = true;
			}
		}
		if (linetop == false) {
			System.out.println();
		}
	}

	public static void dump(PrintStream ps, byte[] b, int addr, int length) {
		char[] ascii = new char[16];
		int ptr = addr;
		int column = 15;
		for (int i = 0; i < length; i++, ptr++) {
			column = (i % 16);
			if (column == 0) {
				for (int j = 0; j < 16; j++) {
					ascii[j] = ' ';
				}
				ps.print(toHexStringUpper(addr) + ": ");
				addr += 16;
			} else if (column == 8) {
				ps.print("- ");
			}
			byte v = b[ptr];
			ascii[column] = ((v >= 0x20) & (v < 0x7F)) ? (char) v : '.';
			ps.print(toHexStringUpper(v) + " ");
			if (column == 15) {
				dumpAscii(ps, ascii);
			}
		}
		if (column != 15) {
			column++;
			int spaces = (16 - column) * 3 + ((column < 9) ? 2 : 0);
			for (; spaces != 0; spaces--) {
				ps.print(' ');
			}
			dumpAscii(ps, ascii);
		}
	}

	private static void dumpAscii(PrintStream ps, char[] ascii) {
		ps.print(": ");
		for (char c: ascii) {
			if (c == '<') {
				ps.print("&lt;");
			} else {
				ps.print(c);
			}
		}
		ps.println();
	}


	/**
	 * バイト値を表現する 16 進文字列を取得します。
	 * <p>
	 * {@link #toHexString(byte)} とは a 〜 f を大文字で表現する点で異なります。
	 * @param b バイト値を指定します。
	 * @return b を 16 進で表現する文字列が返却されます。
	 */
	private static String toHexStringUpper(byte b) {
		int v = b;
		char[] c = new char[2];
		for (int i = 1; i >= 0; i--) {
			c[i] = get4bUpper(v);
			v = v >> 4;
		}
		return new String(c);
	}

	/**
	 * int 値を表現する 16 進文字列を取得します。
	 * <p>
	 * {@link #toHexString(int)} とは a 〜 f を大文字で表現する点で異なります。
	 * @param v int 値を指定します。
	 * @return v を 16 進で表現する文字列が返却されます。
	 */
	private static String toHexStringUpper(int v) {
		char[] c = new char[8];
		for (int i = 7; i >= 0; i--) {
			c[i] = get4bUpper(v);
			v = v >> 4;
		}
		return new String(c);
	}

	private static char get4bUpper(int b) {
		b &= 0x0F;
		return (b < 10) ? ((char) ('0' + b)) : ((char) ('A' + (b - 10)));
	}
	
	
    public static String toHexStringUpperShort(short n) {
        char[] line = new char[4];
        for (int i = 0; i < line.length; i++) {
            line[i] = ' ';
        }
        int v = n;
        for (int j = 3; j >= 0; j--) {
            line[j] = get4bUpper(v);
            v = v >> 4;
        }
        return new String(line);
    }
    public static String toHexStringUpperInt(int n) {
        char[] line = new char[8];
        for (int i = 0; i < line.length; i++) {
            line[i] = ' ';
        }
        int v = n;
        for (int j = 7; j >= 0; j--) {
            line[j] = get4bUpper(v);
            v = v >> 4;
        }
        return new String(line);
    }

    public static String toHexStringUpperLong(long n) {
        char[] line = new char[16];
        for (int i = 0; i < line.length; i++) {
            line[i] = ' ';
        }
        long v = n;
        for (int j = 15; j >= 0; j--) {
            line[j] = get4bUpper((int) v);
            v = v >> 4;
        }
        return new String(line);
    }
}
