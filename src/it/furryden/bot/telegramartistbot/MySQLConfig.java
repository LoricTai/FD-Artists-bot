package it.furryden.bot.telegramartistbot;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Connection;

public class MySQLConfig {
	private static MySQLConfig msqlc = null;
	private String host;
	private String user;
	private String pass;
	private String dbname;
	private java.sql.Connection conn;
	private char alias;

	private MySQLConfig() {
		
	}
	
	public void setConnectionParameters(String host, String user, String pass, String dbname) {
		this.host = host;
		this.user = user;
		this.pass = pass;
		this.dbname = dbname;
	}
	
	public static MySQLConfig getInstance() {
		if (msqlc == null) msqlc = new MySQLConfig();
		return msqlc;
	}

	public Connection getConnection() throws DatabaseException {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbname + "?useSSL=false", user, pass);
			} catch (SQLException e) {
				throw new DatabaseException(null, e, dbname);
			} 
		}
		return conn;
	}
	
	public Connection getGenericConnection() throws DatabaseException {
		try {
			return DriverManager.getConnection("jdbc:mysql://" + host, user, pass);
		} catch (SQLException e) {
				throw new DatabaseException(null, e, dbname);
		} 
	}
	
	public String getDBName() {
		return dbname;
	}

	public char getChar() {
		return alias;
	}
}
