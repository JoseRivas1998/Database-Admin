package com.tcg.dbadmin;

import java.sql.Connection;
import java.sql.DriverManager;

public class ProgramManager {

	public static String host;
	public static String user;
	public static String password;
	public static String database;
	public static Connection sqlCon;
	
	public static Connection getConnection(String host, String user, String password, String database) {
		try {
			sqlCon = DriverManager.getConnection("jdbc:mysql://"+ host +"/" + database, user, password);
		} catch (Exception e) {
			sqlCon = null;
		}
		return sqlCon;
	}
	
	public static Connection getConnection() {
		return ProgramManager.getConnection(ProgramManager.host, ProgramManager.user, ProgramManager.password, ProgramManager.database);
	}
	
}
