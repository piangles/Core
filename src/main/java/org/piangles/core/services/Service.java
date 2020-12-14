package org.piangles.core.services;

public interface Service
{
	public ServiceMetadata getServiceMetadata();
	public Response process(Request request);
}
