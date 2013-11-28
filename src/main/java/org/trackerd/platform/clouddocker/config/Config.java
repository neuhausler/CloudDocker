package org.trackerd.platform.clouddocker.config;

import java.io.*;
import java.util.*;

public class Config
{
	private String dockerSocket= null;

	private static Config config= null;
	
	public static Config accessor()
	{
		if (config!= null) return config;
		
		try
		{
			config= new Config(org.trackerd.platform.clouddocker.config.Config.class.getResourceAsStream("config.properties"));

		} catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		return config;
	}


	private Config(InputStream inputStream) throws IOException
	{
		Properties properties= new Properties();

		properties.load(inputStream);
		inputStream.close();

		dockerSocket= properties.getProperty("dockerSocket");
		
	}

	
	public String getDockerSocket()
	{
		return dockerSocket;
	}


}
