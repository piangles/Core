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
 
 
 
package org.piangles.core.services.remoting.locator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.piangles.core.services.remoting.handlers.Handler;
import org.piangles.core.services.remoting.handlers.HandlerException;
import org.piangles.core.util.central.CentralClient;

public abstract class AbstractServiceLocator
{
	private static final String PROPS_HANDLER = "HandlerClassName";
	private HashMap<String, Object> serviceProxyMap = null;
	private HashMap<String, Handler> handlerMap = null;

	protected AbstractServiceLocator()
	{
		serviceProxyMap = new HashMap<String, Object>();
		handlerMap = new HashMap<String, Handler>();
	}

	public void destroy()
	{
		synchronized(handlerMap)
		{
			Set<String> serviceNames = handlerMap.keySet();
			Handler handler = null;
			for (String serviceName : serviceNames)
			{
				handler = handlerMap.get(serviceName);
				handler.destroy();
			}
		}
	}
	
	///////////////////////////////////createProxy////////////////////////////////
	protected Object createProxy(Class<?> serviceClass)
	{
		return createProxy(serviceClass.getSimpleName(), serviceClass);
	}

	protected Object createProxy(String serviceName, Class<?> serviceClass)
	{
		String key = serviceClass.getCanonicalName();
		Object proxy = serviceProxyMap.get(key);

		if (proxy == null)
		{
			ClassLoader loader = serviceClass.getClassLoader();
			Class<?>[] interfaces = new Class[] { serviceClass };
			Handler handler = null;

			Properties props = null;
			try
			{
				props = CentralClient.getInstance().discover(serviceName);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			
			String handlerClassName = (String) props.get(PROPS_HANDLER);
			if (handlerClassName == null)
			{
				throw new RuntimeException(serviceName + " does not have " + PROPS_HANDLER + " configured in CentralService.");
			}
			
			try
			{
				handler = (Handler) Class.forName(handlerClassName).getConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
			
			try
			{
				handler.init(serviceName, props);
			}
			catch (HandlerException e)
			{
				throw new RuntimeException(e);
			}

			proxy = Proxy.newProxyInstance(loader, interfaces, handler);
			serviceProxyMap.put(key, proxy);
			handlerMap.put(key, handler);
		}

		return proxy;
	}
}
