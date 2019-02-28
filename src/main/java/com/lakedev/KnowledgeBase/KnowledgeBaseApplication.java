package com.lakedev.KnowledgeBase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
		
		SpringApplication.run(KnowledgeBaseApplication.class, args);
	}
	
}
