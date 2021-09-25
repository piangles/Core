/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.core.services.remoting;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;

import org.piangles.core.expt.NotFoundException;
import org.piangles.core.services.AuditDetails;
import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.Service;
import org.piangles.core.services.ServiceMetadata;
import org.piangles.core.stream.Stream;

public abstract class AbstractService implements Service
{
	private ServiceMetadata serviceMetadata = null;
	private HashMap<String, Method> endPointMethodMap = null;
	private Object serviceImpl = null;

	public AbstractService(Object serviceImpl)
	{
		this.serviceImpl = serviceImpl;
		serviceMetadata = new ServiceMetadata();
		endPointMethodMap = new HashMap<String, Method>();

		Method[] methods = serviceImpl.getClass().getDeclaredMethods();
		for (Method method : methods)
		{
			Parameter[] params = Arrays.stream(method.getParameters()).
										filter(p -> !(p.getType().equals(AuditDetails.class)))
										.toArray(Parameter[]::new);
			endPointMethodMap.put(createKey(method.getName(), params), method);
			
			ServiceMetadata.Metadata metadata = new ServiceMetadata().new Metadata();
			metadata.streamBased = (method.getReturnType().getCanonicalName().equals(Stream.class.getCanonicalName()));
			
			serviceMetadata.addMetadata(method.getName(), metadata);
		}
	}

	@Override
	public final Response process(Request request)
	{
		Object returnValue = null;
		Method method = null;
		Object[] args = request.getParameters();
		
		method = lookupMethod(request.getEndPoint(), args);
		
		if (method != null)
		{
			try
			{
				/**
				 * One step Prior to making the call to the final Servce.
				 * The call below is implemented by the Derived  AbstractService classes.
				 */
				returnValue = process(method, args, request);
			}
			catch (Exception e)
			{
				/**
				 * Along with 
				 * UnauthorizedException <- IllegalAccessException
				 * BadRequestException <- IllegalArgumentException>
				 * Service(Runtime)Exception <- InvocationTargetException
				 * 
				 * Any other Exception from Java are caught here.				
				 */
				returnValue = e;
			}
		}
		else
		{
			returnValue = new NotFoundException("Endpoint : " +  request.getEndPoint() + " for Service : " + request.getServiceName() + " not found.");
		}
		
		return new Response(request.getServiceName(), request.getEndPoint(), request.getTransitTime(), returnValue);
	}
	
	@Override
	public final ServiceMetadata getServiceMetadata()
	{
		return serviceMetadata;
	}
	
	protected final Object getServiceImpl()
	{
		return serviceImpl;
	}
	
	protected Method lookupMethod(String endpoint, Object[] args)
	{
		return endPointMethodMap.get(createKey(endpoint, args));
	}

	private String createKey(String endpoint, Object[] args)
	{
		String key = null;
		if (args != null)
		{
			key = endpoint + args.length;
		}
		else
		{
			key = endpoint + "0";
		}

		return key;
	}
	
	protected abstract Object process(Method method, Object[] args, Request request) throws Exception;
}
