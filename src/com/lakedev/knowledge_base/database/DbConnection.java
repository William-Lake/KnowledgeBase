package com.lakedev.knowledge_base.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.lakedev.knowledge_base.ui.dialogs.ErrorDialog;

/**
 * DbConnection
 * 
 * Represents a Connection to the Database.
 * 
 * @author William Lake
 *
 */
class DbConnection
{
	private Connection conn;
	
	private Exception connectionException;
	
	/**
	 * Connects to the given .db file.
	 * 
	 * Called by DataSource.createDBConnection()
	 * 
	 * @param dbFile
	 * 			The .db file to connect to.
	 * @return True if the connection was successful.
	 */
	public boolean connect(File dbFile)
	{
		// First check if the file even exists.
		boolean connectionSuccessful = Files.exists(dbFile.toPath().toAbsolutePath());
		
		if (connectionSuccessful == false)
		{
			connectionException = new IOException("KnowledgeBase.db File Not Found");
			
			return connectionSuccessful;
		}
		
		// jdbc:sqlite:/home/wlake/Desktop/KnowledgeBase.db
		String connectionString = "jdbc:sqlite:";
		
		connectionString += dbFile.toPath().toAbsolutePath().toString();
		
		try
		{
			conn = DriverManager.getConnection(connectionString);
		} catch (Exception e)
		{
			// Would normally show the user, but it's about to be shared in another class.
			connectionException = e;
			
			connectionSuccessful = false;
		}
		
		return connectionSuccessful;
	}
	
	/**
	 * Disconnects from the Database.
	 * 
	 * Called by DataSource.closeDBConnection()
	 */
	public void disconnect()
	{
		try
		{
			if (conn != null) conn.close();
			
		} catch (SQLException e)
		{
			new ErrorDialog(e);
		}
	}
	
	/**
	 * Creates a PreparedStatement with the given 
	 * SQL string.
	 * 
	 * Called in a number of places in DataSource.
	 * 
	 * @param sql
	 * 			The SQL string to use when creating the prepared statement.
	 * @param doSetReturnGeneratedKeys
	 * 			Whether or not to setup the PreparedStatement to return DB generated keys.
	 * @return The PreparedStatement.
	 */
	public PreparedStatement prepareStatement(String sql,boolean doSetReturnGeneratedKeys)
	{
		try
		{
			if (doSetReturnGeneratedKeys) return conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			else return conn.prepareStatement(sql);
		} catch (SQLException e)
		{
			new ErrorDialog(e);
		}
		
		return null;
	}
	
	/**
	 * Provides the Connection Exception potentially
	 * generated in the connect() method.
	 * 
	 * Called by DataSource.getConnectionException.
	 * 
	 * @return The Connection Exception.
	 */
	public Exception getConnectionException()
	{
		return connectionException;
	}

	/**
	 * Creates a connection to a new DB file.
	 * 
	 * Called by DataSource.createNewDB()
	 */
	public void createNewDB()
	{
		String connectionString = "jdbc:sqlite:";
		
		// Gets the .jar file's current working directory, but does NOT work in Eclipse.
		String currentWorkingDir = 
				Paths
				.get
				(
						System
						.getProperty("java.class.path"))
				.getParent()
				.toString();
		
		// However, this DOES work in Eclipse, so it can be uncommented for workspace testing.
//		String currentWorkingDir = Paths.get("").toAbsolutePath().toString();
		
		connectionString += Paths.get(currentWorkingDir,"KnowledgeBase.db");
		
		try
		{
			conn = DriverManager.getConnection(connectionString);
		} catch (SQLException e)
		{
			new ErrorDialog(e);
		}
	}
}
