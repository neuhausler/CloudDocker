package org.trackerd.platform.clouddocker.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.trackerd.platform.clouddocker.docker.*;

@Path("/container")
public class ContainerHandler
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String listContainers()
	{
		return DockerWrapper.listContainers().toString();
	}

}
