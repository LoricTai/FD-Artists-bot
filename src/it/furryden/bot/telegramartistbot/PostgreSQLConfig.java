package it.furryden.bot.telegramartistbot;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Connection;

public class PostgreSQLConfig {
	private static PostgreSQLConfig psqlc = null;
	private String host;
	private String user;
	private String pass;
	private String dbname;
	private java.sql.Connection conn;
	private char alias;

	private PostgreSQLConfig() {
		
	}
	
	public void setConnectionParameters(String host, String user, String pass, String dbname) {
		this.host = host;
		this.user = user;
		this.pass = pass;
		this.dbname = dbname;
	}
	
	public static PostgreSQLConfig getInstance() {
		if (psqlc == null) psqlc = new PostgreSQLConfig();
		return psqlc;
	}

	public Connection getConnection() throws DatabaseException {
		if (conn == null) {
			String url = "jdbc:postgresql://" + host + "/" + dbname + "?useSSL=false";
			System.out.println(url);
			try {				
				conn = DriverManager.getConnection(url, user, pass);
			} catch (SQLException e) {
				throw new DatabaseException(null, e, dbname);
			} 
		}
		return conn;
	}
	
	public String getDBName() {
		return dbname;
	}

	public char getChar() {
		return alias;
	}
	
	public static void checkDB() throws SQLException, DatabaseException {
		SQLQuery qr = new SQLQuery("select exists (select 1 from pg_type where typname = 'type_role');", null);
		ResultSet r = qr.executeQuery();
		r.next();
		if(!r.getBoolean("exists")) {
			createDB();
		}
	}
	
	private static void createDB() throws SQLException, DatabaseException {
		SQLQuery q = new SQLQuery("CREATE TYPE type_role AS ENUM('telegram_user','admin','artist');", null);
		q.executeUpdate();
		q = new SQLQuery("CREATE TABLE IF NOT EXISTS artist("
			+ "id varchar(45) not null,"
			+ "profile_pic varchar(56) default null,"
			+ "nickname varchar(45) default null,"
			+ "faurl varchar(100) default null,"
			+ "comm_status boolean not null default false,"
			+ "PRIMARY KEY (id)"
			+ ");", null);
		q.executeUpdate();
		q = new SQLQuery("CREATE TABLE IF NOT EXISTS commission("
			+ "id SERIAL not null ,"
			+ "artist varchar(45) not null,"
			+ "description varchar(100) not null,"
			+ "slots int not null default 1,"
			+ "price int not null,"
			+ "PRIMARY KEY(id),"
			+ "CONSTRAINT fkartistcomm FOREIGN KEY (artist) REFERENCES artist (id) ON DELETE NO ACTION ON UPDATE NO ACTION"
			+ ")", null);
		q.executeUpdate();
		q = new SQLQuery("CREATE TABLE IF NOT EXISTS telegram_user("
				+ "chat_id varchar(45) NOT NULL,"
			 	+ "nickname varchar(45) DEFAULT NULL,"
				+ "role type_role NOT NULL DEFAULT 'telegram_user',"
				+ "PRIMARY KEY (chat_id)"
				+ ");", null);
			q.executeUpdate();
		q = new SQLQuery("CREATE TABLE IF NOT EXISTS follow("
			+ "  telegram_user varchar(45) NOT NULL,"
			+ "  artist varchar(45) NOT NULL,"
			+ "  PRIMARY KEY (telegram_user,artist)," 
			+ "  CONSTRAINT fkartistfol FOREIGN KEY (artist) REFERENCES artist (id) ON DELETE NO ACTION ON UPDATE NO ACTION," 
			+ "  CONSTRAINT fkuserfol FOREIGN KEY (telegram_user) REFERENCES telegram_user (chat_id) ON DELETE NO ACTION ON UPDATE NO ACTION" 
			+ ");", null);
		q.executeUpdate();
		q = new SQLQuery("CREATE TABLE IF NOT EXISTS sample("
			+ "sample varchar(56) NOT NULL,"
			+ "artist varchar(45) NOT NULL,"
			+ "PRIMARY KEY (sample),"
			+ "CONSTRAINT fkartistsample FOREIGN KEY (artist) REFERENCES artist (id) ON DELETE NO ACTION ON UPDATE NO ACTION"
			+ ");");
		q.executeUpdate();
		q = new SQLQuery("CREATE TABLE IF NOT EXISTS telegram_user_artist("
			+ "telegram_user varchar(45) NOT NULL,"
			+ "info varchar(45) NOT NULL,"
			+ "PRIMARY KEY (telegram_user,info),"
			+ "CONSTRAINT fkartist FOREIGN KEY (info) REFERENCES artist (id) ON DELETE NO ACTION ON UPDATE NO ACTION,"
			+ "CONSTRAINT fkuser FOREIGN KEY (telegram_user) REFERENCES telegram_user (chat_id) ON DELETE NO ACTION ON UPDATE NO ACTION"
			+ ");", null);
		q.executeUpdate();
	}
}
