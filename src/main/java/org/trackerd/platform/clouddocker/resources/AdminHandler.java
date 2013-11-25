package org.trackerd.platform.clouddocker.resources;

import javax.ws.rs.*;


@Path("/admin")
public class AdminHandler
{
	@GET
	@Path("/ping")
	public String ping()
	{
		return "pong";
	}

	@GET
	@Path("/wait10s")
	public String wait10s()
	{
		try
		{
			Thread.sleep(10*1000);
		} catch(InterruptedException e)
		{}
		
		return "done";
	}

}
