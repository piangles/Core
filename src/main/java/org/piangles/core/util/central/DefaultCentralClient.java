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
 
 
 
package org.piangles.core.util.central;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.piangles.core.util.Logger;

public final class DefaultCentralClient extends CentralClient
{
	private static final String CENTRAL_HOST = "central.host";
	private static final String CENTRAL_PORT = "central.port";
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String CENTRAL_SERVICE = "/CentralService/";

	private String discoverURL = null;
	private String tier1ConfigURL = null;
	private String cipherURL = null;
	private String centralSocketAddress = null;

	public DefaultCentralClient() throws Exception
	{
		String centralHost = System.getenv(CENTRAL_HOST);
		String centralPort = System.getenv(CENTRAL_PORT);
		if (centralHost != null && centralPort != null)
		{
			//Validate both hostname and port 
			try
			{
				InetAddress.getByName(centralHost);
			}
			catch (UnknownHostException e)
			{
				throw new Exception(CENTRAL_HOST + " is not valid.", e);
			}
			
			try
			{
				Integer.parseInt(centralPort);
			}
			catch (NumberFormatException e)
			{
				throw new Exception(CENTRAL_PORT + " is not valid integer", e);
			}
			
			centralSocketAddress = centralHost + ":" + centralPort;
			Logger.getInstance().info("Central SocketAddress is : " + centralSocketAddress);
		}
		else
		{
			String message = CENTRAL_HOST + " or " + CENTRAL_PORT + " environment variable(s) are missing.";
			Logger.getInstance().fatal(message);
			throw new Exception(message);
		}
	}

	@Override
	public Properties discover(String serviceName) throws Exception
	{
		if (discoverURL == null)
		{
			discoverURL = "http://" + centralSocketAddress + CENTRAL_SERVICE + "discover?ServiceName=";
		}
		Logger.getInstance().info("CentralClient:Discovering properties for Service : " + serviceName);
		return getProperties("Discovery", discoverURL, serviceName);
	}

	@Override
	public Properties tier1Config(String serviceName) throws Exception
	{
		if (tier1ConfigURL == null)
		{
			tier1ConfigURL = "http://" + centralSocketAddress + CENTRAL_SERVICE + "tier1config?ServiceName=";
		}
		Logger.getInstance().info("CentralClient:Retriving tier1Config for Service : " + serviceName);
		return getProperties("Tier1Config", tier1ConfigURL, serviceName);
	}

	@Override
	public String decrypt(String serviceName, String encryptedCategory, String encryptedValueName, String encryptedValue, String cipherAuthorizationIdName, String cipherAuthorizationId)
			throws Exception
	{
		String decryptedString = null;
		StringBuffer response = new StringBuffer();

		try
		{
			Logger.getInstance().info("CentralClient:Decrypting for Service : " + serviceName);
			if (cipherURL == null)
			{
				cipherURL = "http://" + centralSocketAddress + CENTRAL_SERVICE + "decrypt?" 
						+ "ServiceName=%s&encryptedCategory=%s&" 
						+ "encryptedValueName=%s&encryptedValue=%s&"
						+ "cipherAuthorizationIdName=%s&cipherAuthorizationId=%s";
			}
			String finalCipherURL = String.format(cipherURL, URLEncoder.encode(serviceName, "UTF-8"), 
					URLEncoder.encode(encryptedCategory, "UTF-8"), 
					URLEncoder.encode(encryptedValueName, "UTF-8"),
					URLEncoder.encode(encryptedValue, "UTF-8"), 
					URLEncoder.encode(cipherAuthorizationIdName, "UTF-8"), 
					URLEncoder.encode(cipherAuthorizationId, "UTF-8"));
			URL discoveryURL = new URL(finalCipherURL);
			HttpURLConnection discoveryConn = (HttpURLConnection) discoveryURL.openConnection();

			discoveryConn.setRequestMethod("GET");
			discoveryConn.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = discoveryConn.getResponseCode();
			if (responseCode == 200)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(discoveryConn.getInputStream()));
				decryptedString = in.readLine();
				in.close();
			}
			else
			{
				response = handleNon200(discoveryConn, responseCode);
			}
		}
		catch (IOException e)
		{
			throw new Exception(e.getMessage(), e);
		}

		if (decryptedString == null)
		{
			throw new Exception("Unable to decrypt : " + response.toString());
		}

		return decryptedString;
	}

	private Properties getProperties(String propertyName, String URL, String serviceName) throws Exception
	{
		Properties discoveryProps = null;
		StringBuffer response = new StringBuffer();

		try
		{
			URL discoveryURL = new URL(URL + serviceName);
			HttpURLConnection discoveryConn = (HttpURLConnection) discoveryURL.openConnection();

			discoveryConn.setRequestMethod("GET");
			discoveryConn.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = discoveryConn.getResponseCode();
			if (responseCode == 200)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(discoveryConn.getInputStream()));
				String inputLine = null;

				while ((inputLine = in.readLine()) != null)
				{
					response.append(inputLine);
					response.append("\n");
				}
				in.close();

				discoveryProps = new Properties();
				discoveryProps.load(new StringReader(response.toString()));
			}
			else
			{
				response = handleNon200(discoveryConn, responseCode);
			}
		}
		catch (IOException e)
		{
			throw new Exception(e.getMessage(), e);
		}

		if (discoveryProps == null)
		{
			throw new Exception("Unable to retrieve " + propertyName + " Properties for : " + serviceName + " Because : " + response.toString());
		}

		return discoveryProps;
	}
	
	private StringBuffer handleNon200(HttpURLConnection discoveryConn, int responseCode) throws IOException
	{
		StringBuffer response = new StringBuffer();

		StringWriter writer = new StringWriter();
		IOUtils.copy(discoveryConn.getErrorStream(), writer, StandardCharsets.UTF_8);

		response.append("Response Code : ").append(responseCode).append("\n");
		response.append("Response Message : ").append(writer.toString()).append("\n");

		return response;
	}
}
