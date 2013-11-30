package org.trackerd.platform.clouddocker.docker;

import java.io.*;
import java.net.*;

import javax.xml.stream.*;

import net.minidev.json.*;

import org.trackerd.platform.clouddocker.config.*;


public class DockerWrapper
{
	public static JSONArray listImages()
	{
		return execGetReturnArray("/images/json");
	}

	private static JSONArray execGetReturnArray(String command)
	{
		JSONArray result= null;

		try
		{
		    URL couchDBURL= new URL(Config.accessor().getDockerSocket()+'/'+command);
			HttpURLConnection connection = (HttpURLConnection)couchDBURL.openConnection();
	        
	        connection.setRequestMethod("GET");
	        connection.setReadTimeout(10000);

	        connection.connect();
	        
	        if(connection.getResponseCode() == 404)
	        	return new JSONArray();

	        InputStreamReader reader= new InputStreamReader(connection.getInputStream(), "UTF-8");
	        result= (JSONArray)JSONValue.parse(reader);
	        reader.close();

	        connection.disconnect();

		} catch(MalformedURLException e)
		{
			e.printStackTrace();
		} catch(IOException e)
		{
			e.printStackTrace();
		} catch(FactoryConfigurationError e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

}
