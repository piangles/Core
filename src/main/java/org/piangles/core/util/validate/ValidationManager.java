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
 
 
 
package org.piangles.core.util.validate;

import java.util.HashMap;
import java.util.Map;

public class ValidationManager
{
	private static ValidationManager self = null;
	
	private Map<String, Validator> validatorMap = null;
	
	private ValidationManager()
	{
		 validatorMap = new HashMap<>();
	}
	
	public static synchronized ValidationManager getInstance()
	{
		if (self == null)
		{
			self = new ValidationManager();
		}

		return self;
	}
	
	public void addValidator(Validator validator)
	{
		validatorMap.put(validator.getName(), validator);
	}
	
	public void removeValidator(String name)
	{
		validatorMap.remove(name);
	}

	public void clear()
	{
		validatorMap.clear();
	}

	public Validator getValidator(String name)
	{
		return validatorMap.get(name);
	}
}
