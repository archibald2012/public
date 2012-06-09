package edu.hziee.common.xslt2web.data;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ContextConnectionCreator implements IConnectionCreator {
	public static IConnectionCreator Instance = new ContextConnectionCreator();
	
	private static Context context;
	private static DataSource dataSource;
	
	private ContextConnectionCreator() {
	}
	
	private static void createDataSource(String contextString) {
		try {
			context = new InitialContext();
			dataSource = (DataSource)context.lookup(contextString);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	public Connection createConnection(String connectionString)
			throws SQLException {
		if (dataSource == null)
			createDataSource(connectionString);
		return dataSource.getConnection();
	}

	public Connection createConnection(String connectionString, String user,
			String password) throws SQLException {
		return createConnection(connectionString);
	}

}
