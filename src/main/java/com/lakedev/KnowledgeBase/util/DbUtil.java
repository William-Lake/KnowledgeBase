package com.lakedev.KnowledgeBase.util;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.orm.jpa.JpaSystemException;

public class DbUtil
{
	public static DbStatus checkDb(CrudRepository...repositories)
	{
		DbStatus dbStatus = DbStatus.OK;
		
		List<CrudRepository> unusableRepositories = new ArrayList<>();
		
		Arrays
		.asList(repositories)
		.stream()
		.forEach(repository -> 
		{
			try
			{
				repository.count();
			} catch (JpaSystemException e)
			{
				unusableRepositories.add(repository);
			}
		});
		
		if (unusableRepositories.size() == repositories.length)
		{
			// Code from: https://www.baeldung.com/java-download-file
			try(
					BufferedInputStream bufferedInputStream = new BufferedInputStream(new URL("https://github.com/William-Lake/KnowledgeBase/raw/master/template.db").openStream());
					FileOutputStream fileOutputStream = new FileOutputStream("template.db")
							)
			{
				byte dataBuffer[]  = new byte[1024];
				
				int bytesRead;
				
				while((bytesRead = bufferedInputStream.read(dataBuffer,0,1024)) != -1)
				{
					fileOutputStream.write(dataBuffer,0,bytesRead);
				}
				
			} catch (IOException e)
			{
				dbStatus = DbStatus.NO_DB_ERROR_DB_DL;
			}
			
			if (dbStatus == DbStatus.OK)
			{
				try
				{
					Files.move(Paths.get("template.db"), Paths.get("knowledge.db"), StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e)
				{
					dbStatus = DbStatus.NO_DB_ERROR_DB_REPLACE;
				}
			}
			
		} else if (0 < unusableRepositories.size() && unusableRepositories.size() < repositories.length)
		{
			dbStatus = DbStatus.PARTIAL_DB;
		}
		
		return dbStatus;
	}
}
