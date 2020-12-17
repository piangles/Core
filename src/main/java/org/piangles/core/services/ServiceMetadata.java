package org.piangles.core.services;

import java.util.HashMap;
import java.util.Map;

public final class ServiceMetadata
{
	private Map<String, Metadata> endpointMetadataMap;
	
	public ServiceMetadata()
	{
		endpointMetadataMap = new HashMap<>();
	}
	
	public void addMetadata(String endpoint, Metadata metadata)
	{
		endpointMetadataMap.put(endpoint, metadata);
	}
	
	public boolean isEndpointStreamBased(String endpoint)
	{
		boolean streamBased = false;
		if (endpointMetadataMap.get(endpoint) != null)
		{
			streamBased = endpointMetadataMap.get(endpoint).streamBased;
		}
		return streamBased;
	}
	
	public class Metadata
	{
		public boolean streamBased;
	}
}
