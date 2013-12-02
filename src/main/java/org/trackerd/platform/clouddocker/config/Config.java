package org.trackerd.platform.clouddocker.config;

import java.io.*;
import java.util.*;

public class Config
{
	private String dockerSocket= null;
	private String baseImage= null;
	private String urlForWarLocation= null;

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

		dockerSocket     = properties.getProperty("dockerSocket");
		baseImage        = properties.getProperty("baseImage");
		urlForWarLocation= properties.getProperty("urlForWarLocation");
		
	}

	
	public String getDockerSocket()
	{
		return dockerSocket;
	}

	public String getBaseImage()
	{
		return baseImage;
	}
	
	public String getUrlForWarLocation()
	{
		return urlForWarLocation;
	}
	
}
