package com.lakedev.knowledge_base.database;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lakedev.knowledge_base.Knowledge;
import com.lakedev.knowledge_base.ui.dialogs.ErrorDialog;

/**
 * DataSource
 * 
 * Manages converting DB data into
 * usable POJOs.
 * 
 * Uses the Singleton Design Pattern
 * 
 * @author William Lake
 *
 */
public class DataSource
{
	private final DbConnection dbConnection;
	
	/**
	 * DataSourceHolder
	 * 
	 * Minor class, helps apply a slightly more simplified version
	 * of the Singleton pattern.
	 */
	private static class DataSourceUtil
	{
		static final DataSource DATASOURCE_INSTANCE = new DataSource();
	}
	
	/**
	 * Constructor
	 */
	private DataSource()
	{
		dbConnection = new DbConnection();
	}
	
	/**
	 * Provides the current DataSource instance.
	 * 
	 * @return The current DataSource instance.
	 */
	public static DataSource getInstance()
	{
		return DataSourceUtil.DATASOURCE_INSTANCE;
	}

	/**
	 * Provides knowledge from the DB with the 
	 * given parameters.
	 * 
	 * Called by PrimaryLayout.performSearch()
	 * 
	 * @param date
	 * 			The data filter for the DB knowledge.
	 * @param title
	 * 			The title keywords to search with.
	 * @param content
	 * 			The content keywords to search with.
	 * @return
	 * 		The KnowledgeList of Knowledge found in the DB with the given parameters.
	 */
	public List<Knowledge> gatherKnowledge(LocalDate date, String title, String content)
	{
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT * ");
		query.append("FROM knowledge ");
		query.append(getQueryForParams(date,title,content));
		
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query.toString(),false);
		
		try
		{
			setParamsInPreparedStatement(preparedStatement, date,title,content);
			
			return convertRawKnowledgeToKnowledgeList(preparedStatement.executeQuery());
			
		} catch (SQLException e)
		{
			new ErrorDialog(e);
		}
		
		return null;
	}

	/**
	 * Creates and returns a List of Knowledge
	 * constructed from the Raw DB Data contained in the 
	 * given ResultSet.
	 * 
	 * @param rawKnowledge
	 * 			The ResultSet to process.
	 * @return The List of Knowlege
	 * @throws SQLException
	 */
	private List<Knowledge> convertRawKnowledgeToKnowledgeList(ResultSet rawKnowledge) throws SQLException
	{
		List<Knowledge> knowledgeList = new ArrayList<>();
		
		while (rawKnowledge.next())
		{
			Knowledge knowledge = new Knowledge();
			
			knowledge.setKnowledgeId(rawKnowledge.getInt("knowledge_id"));
			
			knowledge.setDateSaved(LocalDate.ofEpochDay(rawKnowledge.getLong("date_saved")));
			
			knowledge.setTitle(rawKnowledge.getString("title"));
			
			knowledge.setContent(rawKnowledge.getString("content"));
			
			knowledgeList.add(knowledge);
		}
		
		return knowledgeList;
	}

	/**
	 * Provides the appropriate String SQL query for 
	 * the given parameters.
	 * 
	 * Called by the gatherKnowledge method.
	 * 
	 * @param date
	 * 			The date to use in the query.
	 * @param title
	 * 			The title keywords to use in the query.
	 * @param content
	 * 			The content keywords to use in the query.
	 * @return The generated SQL query for the given params.
	 */
	private String getQueryForParams(LocalDate date, String title, String content)
	{
		StringBuilder query = new StringBuilder();
		
		boolean whereAdded = false;
		
		// There's at least SOME data
		if (date != null || title.trim().isEmpty() == false || content.trim().isEmpty() == false) query.append("WHERE ");
		
		// A date was provided
		if (date != null) 
		{
			query.append(" date_saved = ? ");
			
			whereAdded = true;
		}
		
		// A title was provdied
		if (title.trim().isEmpty() == false) // Title Provided
		{
			query.append(whereAdded ? "AND " : "WHERE ");
			
			query.append("title LIKE ? ");
			
			whereAdded = true;
		}
		
		if (content.trim().isEmpty() == false) // Content provided
		{
			query.append(whereAdded ? "AND (" : "WHERE (");
			
			/*
			 * Creating a query using all the content keywords.
			 * 
			 * By wrapping all the keyword OR checks in 
			 * parens and using an AND at the front, you
			 * ensure the content searched for contains
			 * at least one of the given keywords while
			 * also meeting the other needs of the query.
			 * 
			 * E.g. AND (content LIKE ? OR content LIKE ? ... )
			 */
			
			for (int i = 0;i<content.split(",").length;i++)
			{
				if (i > 0) query.append("OR ");
				
				query.append("content LIKE ? ");
			}
			
			query.append(") ");
		}
		
		return query.toString();
	}
	
	/**
	 * Sets the data parameters in the given PreparedStatement.
	 * 
	 * Called by the gatherKnowledge method.
	 * 
	 * @param preparedStatement
	 * 			The PreparedStatement to set the data in.
	 * @param date
	 * 			The Date to set in the statement.
	 * @param title
	 * 			The title to set in the statement.
	 * @param content
	 * 			The content to set in the statement.
	 * @throws SQLException
	 */
	private void setParamsInPreparedStatement(PreparedStatement preparedStatement, LocalDate date,
			String title, String content) throws SQLException
	{
		// Keeps track of the non-zero based index PreparedStatement parameter index.
		int paramIndex = 1;
		
		if (date != null) // If a date was provided
		{
			preparedStatement.setLong(paramIndex, date.toEpochDay()); // SQLITE has no Date Datatype, Integer suggested instead: https://www.sqlite.org/datatype3.html (2.2)
			
			paramIndex++;
		}
		
		if (title.trim().isEmpty() == false) // If a title was provided.
		{
			preparedStatement.setString(paramIndex, String.format("%%%s%%", title)); // Result: %title%
			
			paramIndex++;
		}
		
		if (content.trim().isEmpty() == false)
		{
			String[] contentKeywords = content.split(",");
			
			for (int i = 0;i<contentKeywords.length;i++) preparedStatement.setString(paramIndex, String.format("%%%s%%", contentKeywords[i])); // Result: %content%
		}
	}
	
	/**
	 * Updates existing knowledge in the DB with the 
	 * data contained in the given Knowledge.
	 * 
	 * Called by EditorKnowledgeTab.save()
	 * 
	 * @param knowledge
	 * 			The Knowledge to update in the database.
	 */
	public void updateKnowledge(Knowledge knowledge)
	{
		StringBuilder query = new StringBuilder();
		
		query.append("UPDATE knowledge ");
		query.append("SET title = ?, ");
		query.append("content = ?");
		query.append("WHERE knowledge_id = ?");
		
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query.toString(),false);
		
		try
		{
			preparedStatement.setString(1, knowledge.getTitle());
			
			preparedStatement.setString(2, knowledge.getContent());
			
			preparedStatement.setInt(3, knowledge.getKnowledgeId());
			
			preparedStatement.execute();
		} catch(SQLException e)
		{
			new ErrorDialog(e);
		}
	}

	/**
	 * Saves the given knowledge to the database.
	 * 
	 * Called by EditorKnowledgeTab.save()
	 * 
	 * @param knowledge
	 * 			The Knowledge to save to the database.
	 */
	public void saveKnowledge(Knowledge knowledge)
	{
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO knowledge ");
		query.append("(date_saved,title,content) ");
		query.append("VALUES ");
		query.append("(?,?,?) ");
		
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query.toString(),true);
		
		try
		{
			preparedStatement.setLong(1, knowledge.getDateSaved().toEpochDay());
			
			preparedStatement.setString(2, knowledge.getTitle());
			
			preparedStatement.setString(3, knowledge.getContent());
			
			preparedStatement.execute();
			
			ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
			
			// Save the knowledge id generated when you inserted it.
            if (generatedKeys.next()) 
            {
                knowledge.setKnowledgeId(generatedKeys.getInt(1));
            }
            
            
		} catch(SQLException e)
		{
			new ErrorDialog(e);
		} finally
		{
			if (preparedStatement != null)
			{
				try
				{
					preparedStatement.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Deletes the given knowledge from the database.
	 * 
	 * Called by the EditorKnowledgeTab's Delete button.
	 * 
	 * @param knowledge
	 * 			The Knowledge to delete from the DB.
	 */
	public void deleteKnowledge(Knowledge knowledge)
	{
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM knowledge ");
		query.append("WHERE knowledge_id = ? ");
		
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query.toString(),true);
		
		try
		{
			preparedStatement.setInt(1, knowledge.getKnowledgeId());
			
			preparedStatement.execute();
			
		} catch(SQLException e)
		{
			new ErrorDialog(e);
		}
	}

	/**
	 * Creates a Database Connection using the given 
	 * .db file.
	 * 
	 * Called by UI.resolveDBConnection()
	 * 
	 * @param dbFile
	 * 			The .db file to use when creating a conenction.
	 * @return True if the connection was succesful.
	 */
	public boolean createDBConnection(File dbFile)
	{
		return dbConnection.connect(dbFile);
	}
	
	/**
	 * Provides the Connection Exception that was
	 * thrown when trying to connect to the DB.
	 * 
	 * Called by the DBExceptDialog
	 * 
	 * @return The Connection Exception.
	 */
	public Exception getConnectionException()
	{
		return dbConnection.getConnectionException();
	}

	/**
	 * Creates a New .db file to be used with the 
	 * app if one doesn't already exist.
	 * 
	 * Called by UI.resolveDBConnection()
	 */
	public void createNewDB()
	{
		dbConnection.createNewDB();
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("CREATE TABLE knowledge ");
		stringBuilder.append("( ");
		stringBuilder.append("	knowledge_id INTEGER PRIMARY KEY, ");
		stringBuilder.append("	date_saved INTEGER, ");
		stringBuilder.append("	title TEXT, ");
		stringBuilder.append("	content TEXT ");
		stringBuilder.append(") ");
		
		PreparedStatement preparedStatement = dbConnection.prepareStatement(stringBuilder.toString(), false);
		
		try
		{
			preparedStatement.execute();
		} catch (SQLException e)
		{
			new ErrorDialog(e);
		}
	}
	
	/**
	 * Closes the DBConnection.
	 * 
	 * Called by UI.quit()
	 */
	public void closeDBConnection()
	{
		dbConnection.disconnect();
	}
}
