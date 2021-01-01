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

public final class DefaultCentralClient extends CentralClient
{
	public static final String CENTRAL_HOST = "central.host";
	public static final String CENTRAL_PORT = "central.port";
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
			System.out.println("Central SocketAddress is : " + centralSocketAddress);
		}
		else
		{
			String message = CENTRAL_HOST + " or " + CENTRAL_PORT + " environment variable(s) are missing.";
			System.err.println(message);
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
		System.out.println("CentralClient:Discovering properties for Service : " + serviceName);
		return getProperties(discoverURL, serviceName);
	}

	@Override
	public Properties tier1Config(String serviceName) throws Exception
	{
		if (tier1ConfigURL == null)
		{
			tier1ConfigURL = "http://" + centralSocketAddress + CENTRAL_SERVICE + "tier1config?ServiceName=";
		}
		System.out.println("CentralClient:Retriving tier1Config for Service : " + serviceName);
		return getProperties(tier1ConfigURL, serviceName);
	}

	@Override
	public String decrypt(String serviceName, String encryptedCategory, String encryptedValueName, String encryptedValue, String cipherAuthorizationIdName, String cipherAuthorizationId)
			throws Exception
	{
		String decryptedString = null;
		StringBuffer response = new StringBuffer();

		try
		{
			System.out.println("CentralClient:Decrypting for Service : " + serviceName);
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
			}
			else
			{
				response.append("Response Code : ").append(responseCode).append("\n");
				response.append("Response Message : ").append(discoveryConn.getResponseMessage()).append("\n");
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

	private Properties getProperties(String URL, String serviceName) throws Exception
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
				StringWriter writer = new StringWriter();
				IOUtils.copy(discoveryConn.getErrorStream(), writer, StandardCharsets.UTF_8);

				response.append("Response Code : ").append(responseCode).append("\n");
				response.append("Response Message : ").append(writer.toString()).append("\n");
			}
		}
		catch (IOException e)
		{
			throw new Exception(e.getMessage(), e);
		}

		if (discoveryProps == null)
		{
			throw new Exception("Unable to retrieve discoveryProperties : " + response.toString());
		}

		return discoveryProps;
	}
}