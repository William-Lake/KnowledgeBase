package com.lakedev.knowledge_base;

import java.time.LocalDate;

/**
 * Knowledge
 * 
 * Represents a piece of knowledge including a date saved,
 * title, and content.
 * 
 * @author William Lake
 *
 */
public class Knowledge
{
	private int knowledgeId;
	
	private LocalDate dateSaved = LocalDate.now(); // Don't want it to default to null.
	
	private String title = ""; // Don't want it to default to null.
	
	private String content = ""; // Don't want it to default to null.

	// GETTERS && SETTERS
	
	public int getKnowledgeId()
	{
		return knowledgeId;
	}

	public void setKnowledgeId(int knowledgeId)
	{
		this.knowledgeId = knowledgeId;
	}

	public LocalDate getDateSaved()
	{
		return dateSaved;
	}

	public void setDateSaved(LocalDate dateSaved)
	{
		this.dateSaved = dateSaved;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
	
	@Override
	public int hashCode()
	{
		return (String.valueOf(knowledgeId) + dateSaved.toString() + getTitle() + getContent()).hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Knowledge)
		{
			Knowledge otherKnowledge = (Knowledge) obj;
			
			return hashCode() == otherKnowledge.hashCode();
		}
		
		return false;
	}
}
