package org.trackerd.platform.clouddocker.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.trackerd.platform.clouddocker.docker.*;


@Path("/image")
public class ImageHandler
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String listImages()
	{
		return DockerWrapper.listImages().toString();
	}

}
