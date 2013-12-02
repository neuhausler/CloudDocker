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

	public static Object listContainers()
	{
		return execGetReturnArray("/containers/json");
	}

	public static void build(File tarFile, String tag)
	{
		String result= execPostFileReturnResult("/build?t="+tag, tarFile, "application/tar");
			
		System.out.println(result);
	}

	private static JSONArray execGetReturnArray(String command)
	{
		JSONArray result= null;

		try
		{
		    URL dockerURL= new URL(Config.accessor().getDockerSocket()+command);
			HttpURLConnection connection= (HttpURLConnection)dockerURL.openConnection();
	        
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

	private static String execPostFileReturnResult(String command, File file, String fileType)
	{
		String result= null;

		try
		{
		    URL dockerURL= new URL(Config.accessor().getDockerSocket()+command);
			HttpURLConnection connection = (HttpURLConnection)dockerURL.openConnection();
	        
	        connection.setRequestMethod("POST");
	        connection.setDoOutput(true);
	        connection.setDoInput(true);
	        connection.setReadTimeout(30000);
	        connection.setRequestProperty("Content-Type", fileType);
	        connection.setRequestProperty("Content-Length", String.valueOf(file.length()));

			InputStream in= new FileInputStream(file);
	        OutputStream out= connection.getOutputStream();

			byte[] buf= new byte[1024];
			int len;
			while ((len= in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			in.close();			
	        out.flush();
	        out.close();
	        
	        BufferedReader reader= new BufferedReader(new InputStreamReader(connection.getInputStream()));;
	        StringBuilder response= new StringBuilder();
	        String line;
	        while ((line= reader.readLine()) != null)
	        	response.append(line);

	        result= response.toString();

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
