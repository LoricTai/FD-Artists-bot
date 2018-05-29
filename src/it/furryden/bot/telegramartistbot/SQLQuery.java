package it.furryden.bot.telegramartistbot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class SQLQuery {
	private PostgreSQLConfig msqlc;
	private String query;
	private String[] params;

	public SQLQuery(String query, String[] params) {
		this.msqlc = PostgreSQLConfig.getInstance();
		this.query = query.replace("£", msqlc.getDBName());
		this.params = params;

	}

	public SQLQuery(String query) {
		this.msqlc = PostgreSQLConfig.getInstance();
		this.query = query.replace("£", msqlc.getDBName());
		this.params = null;
	}

	private void applyParameters(PreparedStatement ps, String[] params) throws SQLException {
		if (params != null) {
			if (params.length != query.chars().filter(num -> num == '?').count())
				throw new SQLException("Insufficient params");
			for (int i = 0; i < params.length; i++) {
				ps.setString(i + 1, params[i]);
			}
		}
	}

	public ResultSet executeQuery() throws DatabaseException, SQLException {
		PreparedStatement ps = null;
		ps = (PreparedStatement) msqlc.getConnection().prepareStatement(query);
		if (params != null && params.length != 0)
			applyParameters(ps, params);
		ResultSet r = ps.executeQuery();
		return r;
	}

	public void executeUpdate() throws SQLException, DatabaseException {
		PreparedStatement ps = null;
		ps = (PreparedStatement) msqlc.getConnection().prepareStatement(query);
		if (params != null && params.length != 0) {
			applyParameters(ps, params);
		}
		ps.executeUpdate();
		return;
	}

	public String getString() {
		return query;
	}

	public void setParams(String[] params) {
		this.params = params;
	}
}