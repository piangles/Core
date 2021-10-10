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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.piangles.core.expt.BadRequestException;
import org.piangles.core.expt.ServiceRuntimeException;
import org.piangles.core.expt.UnauthorizedException;
import org.piangles.core.services.Request;
import org.piangles.core.util.Logger;

public class DefaultService extends AbstractService
{
	public DefaultService(Object serviceImpl)
	{
		super(serviceImpl);
	}
	
	/**
	 * This is where the final call to the actual underlying Service happens.
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	@Override
	protected Object process(Method method, Object[] args, Request request) throws Exception
	{
		Object result = null;

		try
		{
			result = method.invoke(getServiceImpl(), args);
		}
		catch (IllegalAccessException e)
		{
			Logger.getInstance().error(e.getClass().getSimpleName() + " thrown while making call to the underlying Service.", e);			
			throw new UnauthorizedException(e.getMessage(), e);
		}
		catch (IllegalArgumentException e)
		{
			Logger.getInstance().error(e.getClass().getSimpleName() + " thrown while making call to the underlying Service.", e);
			throw new BadRequestException(e.getMessage(), e);
		}
		catch (InvocationTargetException e)
		{
			/**
			 * The underlying method actually threw an Exception and Java wraps
			 * the Exception with InvocationTargetException. So we have to get
			 * the Cause and propogate it, could be Service Related Exception or ServiceRuntimeException.  
			 */
			if (e.getCause() instanceof Error)
			{
				Error err = (Error) e.getCause();
				Logger.getInstance().error(e.getClass().getSimpleName() + " thrown while making call to the underlying Service.", e);
				Logger.getInstance().error("Error: " + err.getMessage(), err);
				
				throw new ServiceRuntimeException(err.getMessage(), err.getCause());
			}
			else//It is an Throwable Exception
			{
				throw (Exception) e.getCause();
			}
		}
		return result;
	}
}
