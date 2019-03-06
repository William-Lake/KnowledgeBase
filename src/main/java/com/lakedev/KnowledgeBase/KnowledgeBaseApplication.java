package com.lakedev.KnowledgeBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class KnowledgeBaseApplication 
{
	private static final String PORT_ARG_STRING = "--server.port=";
	
	private static final String DEFAULT_PORT = "8081";
	
	public static void main(String[] args) 
	{
		/*
		 * We need to see if the server.port argument was passed.
		 * If not, we need to add it with the default port.
		 */
		boolean portArgExists = false;
		
		for (String arg : args)
		{
			if (arg.toUpperCase().contains(PORT_ARG_STRING.toUpperCase()))
			{
				portArgExists = true;
				
				break;
			}
		}
		
		if (portArgExists == false)
		{
			String portArg = PORT_ARG_STRING + DEFAULT_PORT;
			
			String[] tmpArgs = new String[args.length + 1];
			
			for (int argIndex = 0; argIndex < args.length; argIndex++)
			{
				tmpArgs[argIndex] = args[argIndex];
			}
			
			tmpArgs[args.length] = portArg;
			
			args = tmpArgs;
		}
		
		SpringApplication app = new SpringApplication(KnowledgeBaseApplication.class);
		
		app.addListeners(new ApplicationPidFileWriter());
		
		app.run(args);
	}
	
	@PreDestroy
	public static void shutDown()
	{
		// This is probably not a good idea.
		try
		{
			Path pidFilePath = Paths.get("application.pid");
			
			String pid = Files.readAllLines(pidFilePath).get(0);
			
//			Files.delete(pidFilePath);
			
			
//			Runtime.getRuntime().exec("TASKKILL /F " + pid);
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
