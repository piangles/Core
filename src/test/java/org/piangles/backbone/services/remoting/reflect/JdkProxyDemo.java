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
 
 
 
package org.piangles.backbone.services.remoting.reflect;

import java.lang.reflect.Proxy;

public class JdkProxyDemo
{
	public static void main(String[] args)
	{
		Handler handler = new Handler(new OriginalImpl());
		MyInterface f = (MyInterface) Proxy.newProxyInstance(MyInterface.class.getClassLoader(), new Class[] { MyInterface.class }, handler);
		//System.out.println("What is this : " + f);
		f.originalMethod("Hallo");
	}

}
