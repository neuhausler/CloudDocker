package org.trackerd.platform.clouddocker.resources;

import java.io.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.trackerd.platform.clouddocker.builder.tomcat.*;

import net.minidev.json.*;

@Path("/builder")
public class BuilderHandler
{
	// Json Example
	// {"username":"admin","password":"admin","tag":"my/test", warFiles":["test.war"]}
	//
	@POST
	@Path("/tomcat")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buildAndRunTomcat(
		@HeaderParam("Content-Length") int contentLength,
		@HeaderParam("Content-Type") String contentType,
		InputStream inputStream
	)
	{
		if (contentType == null || contentType.toLowerCase().equals(MediaType.APPLICATION_JSON) == false) throw new WebApplicationException(Response.Status.BAD_REQUEST);
		if (contentLength == 0) throw new WebApplicationException(Response.Status.BAD_REQUEST);

		JSONObject config= (JSONObject) JSONValue.parse(inputStream);
		System.out.println(config.toJSONString());
		
		if (config.isEmpty()
			|| config.containsKey("username") == false
			|| config.containsKey("password") == false
			|| config.containsKey("warFiles") == false
			|| config.get("warFiles") instanceof JSONArray == false) throw new WebApplicationException(Response.Status.BAD_REQUEST);

		TomcatBuilderConfig builderConfig= new TomcatBuilderConfig();
		builderConfig.username= (String) config.get("username");
		builderConfig.password= (String) config.get("password");
		builderConfig.tag     = (String) config.get("tag");
		
		JSONArray warFiles= (JSONArray)config.get("warFiles");
		builderConfig.warFiles= warFiles.toArray(new String[warFiles.size()]);

		String tag= null;
		
		try
		{
			tag= TomcatBuilder.build(builderConfig);

		} catch(Exception e)
		{
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}

		JSONObject result= new JSONObject();
		result.put("tag", tag);
		
	    Response.ResponseBuilder response= Response.ok(result.toJSONString(), MediaType.APPLICATION_JSON_TYPE);
		return response.build();
	}

}
