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
			try {
				conn = DriverManager.getConnection("jdbc:postgresql://" + host + "/" + dbname + "?useSSL=false", user, pass);
			} catch (SQLException e) {
				throw new DatabaseException(null, e, dbname);
			} 
		}
		return conn;
	}
	
	public Connection getGenericConnection() throws DatabaseException {
		try {
			return DriverManager.getConnection("jdbc:postgresql://" + host, user, pass);
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
	
	public static void checkDB() throws DatabaseException, SQLException {
		SQLQuery q = new SQLQuery("SELECT EXISTS("
				+ "SELECT datname"
				+ "FROM pg_catalog.pg_database"
				+ "WHERE lower(datname) = lower('fdartists'));", null);
		ResultSet r = q.executeQuery();
		if(!r.getBoolean("exists")) {
			createDB();
		}		
	}
	
	private static void createDB() throws SQLException, DatabaseException {
		SQLQuery q = new SQLQuery("CREATE SCHEMA 'fdartists'", null);
		q.executeUpdate();
		q = new SQLQuery("CREATE TABLE £.artist("
			+ "idArtist varchar(45) not null,"
			+ " profilePic varchar(56) default null,"
			+ "nickname varchar(45) default null,"
			+ "faurl varchar(100) default null,"
			+ "commStatus tinyint(1) not null default '0',"
			+ "PRIMA;RY KEY ('idArtists')"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;", null);
		q.executeUpdate();
		q = new SQLQuery("CREATE TABLE £.'commission'("
			+ "idCommission int nlt null auto_increment,"
			+ "artist varchar(45) not null,"
			+ "description varchar(100) not null,"
			+ "slots int not null default 1,"
			+ "price int not null,"
			+ "PRIMARY KEY('idCommission'),"
			+ "KEY 'fkartistscomm_idx' ('artist'),"
			+ "CONSTRAINT 'fkartistcomm' FOREIGN KEY ('artist') REFERENCES 'artist' ('idArtist') ON DELETE NO ACTION ON UPDATE NO ACTION"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8", null);
		q.executeUpdate();
		q = new SQLQuery(" £.CREATE TABLE `follow` ("
			+ "  `user` varchar(45) NOT NULL,"
			+ "  `artist` varchar(45) NOT NULL,"
			+ "  PRIMARY KEY (`user`,`artist`)," 
			+ "  KEY `fkartistfol_idx` (`artist`)," 
			+ "  CONSTRAINT `fkartistfol` FOREIGN KEY (`artist`) REFERENCES `artist` (`idArtist`) ON DELETE NO ACTION ON UPDATE NO ACTION," 
			+ "  CONSTRAINT `fkuserfol` FOREIGN KEY (`user`) REFERENCES `user` (`chat_id`) ON DELETE NO ACTION ON UPDATE NO ACTION" 
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;", null);
		q.executeUpdate();
		q = new SQLQuery(" £.CREATE TABLE `sample` ("
			+ "`sample` varchar(56) NOT NULL,"
			+ "`artist` varchar(45) NOT NULL,"
			+ "PRIMARY KEY (`sample`),"
			+ "KEY `fkartistsample_idx` (`artist`),"
			+ "CONSTRAINT `fkartistsample` FOREIGN KEY (`artist`) REFERENCES `artist` (`idArtist`) ON DELETE NO ACTION ON UPDATE NO ACTION"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		q.executeUpdate();
		q = new SQLQuery(" £.CREATE TABLE `user` ("
			+ "`chat_id` varchar(45) NOT NULL,"
		 	+ "`nickname` varchar(45) DEFAULT NULL,"
			+ "`role` enum('user','admin','artist') NOT NULL DEFAULT 'user',"
			+ "PRIMARY KEY (`chat_id`)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;", null);
		q.executeUpdate();
		q = new SQLQuery(" £.CREATE TABLE `user_artist` ("
			+ "`user` varchar(45) NOT NULL,"
			+ "`info` varchar(45) NOT NULL,"
			+ "PRIMARY KEY (`user`,`info`),"
			+ "KEY `fkartist_idx` (`info`),"
			+ "CONSTRAINT `fkartist` FOREIGN KEY (`info`) REFERENCES `artist` (`idArtist`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
			+ "CONSTRAINT `fkuser` FOREIGN KEY (`user`) REFERENCES `user` (`chat_id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;", null);
		q.executeUpdate();
	}
}
