package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MyConnection {
	/**
	 * データベースURL
	 */
	//private final static String URL = "jdbc:mysql://raspi.local/test2025a?characterEncoding=UTF-8&serverTimezone=Asia/Tokyo";
	//private final static String URL = "jdbc:mariadb://raspi.local/test2025a?characterEncoding=UTF-8&serverTimezone=Asia/Tokyo";
	//private final static String URL = "jdbc:mariadb://192.168.11.1/test2025a?characterEncoding=UTF-8&serverTimezone=Asia/Tokyo";
	private final static String URL = "jdbc:mariadb://localhost/test2025a?characterEncoding=UTF-8&serverTimezone=Asia/Tokyo";
	
	
	/**
	 * 接続ユーザ
	 */
	private final static String USER = "kato";
	
	/**
	 * パスワード
	 */
	private final static String PASSWORD = "otou3gogo";

	/**
	 * データベース接続を行い 接続情報を返す
	 * @return 接続情報 (Connection)
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public static Connection getConnection() throws SQLException, ClassNotFoundException {

		// JDBCドライバの読み込み
		//Class.forName("com.mysql.cj.jdbc.Driver");
		Class.forName("org.mariadb.jdbc.Driver");

		return DriverManager.getConnection(URL, USER, PASSWORD);

	}


}
