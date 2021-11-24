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
 
 
 
package org.piangles.core.util;

import org.piangles.core.util.abstractions.AbstractDecrypter;

public final class AWSCentralDecrypter extends AbstractDecrypter
{
	@Override
	public String decrypt(String encryptedName, String encryptedValue) throws Exception
	{
		String decryptedData = encryptedValue;
		//TODO - If we need to eventually need to get to AWS for decryption.
		return decryptedData;
	}
}
