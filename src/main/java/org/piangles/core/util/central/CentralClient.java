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

public final class CentralClient {
	private static String TIER1_CONFIG_URL = null;
	private static String DISCOVERY_URL = null;
	private static String CIPHER_URL = null;
	private static final String CENTRAL_PORT = ":80";
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String CENTRAL_SERVICE = "/CentralService/";

	public static Properties discover(String serviceName) throws Exception {
		if (DISCOVERY_URL == null) {
			String centralHost = determineCentralHost();
			DISCOVERY_URL = "http://" + centralHost + CENTRAL_PORT + CENTRAL_SERVICE +"discover?ServiceName=";
		}

		return getProperties(DISCOVERY_URL, serviceName);
	}

	public static Properties tier1Config(String serviceName) throws Exception {
		if (TIER1_CONFIG_URL == null) {
			String centralHost = determineCentralHost();
			TIER1_CONFIG_URL = "http://" + centralHost + CENTRAL_PORT + CENTRAL_SERVICE+"tier1config?ServiceName=";
		}

		return getProperties(TIER1_CONFIG_URL, serviceName);
	}

	public static String decrypt(String serviceName, String encryptedCategory, String encryptedValueName,
			String encryptedValue, String cipherAuthorizationIdName, String cipherAuthorizationId) throws Exception {
		String decryptedString = null;
		StringBuffer response = new StringBuffer();

		try {
			if (CIPHER_URL == null) {
				String centralHost = determineCentralHost();
				CIPHER_URL = "http://" + centralHost + CENTRAL_PORT + CENTRAL_SERVICE +"decrypt?"
						+ "ServiceName=%s&encryptedCategory=%s&" + "encryptedValueName=%s&encryptedValue=%s&"
						+ "cipherAuthorizationIdName=%s&cipherAuthorizationId=%s";
			}
			String cipherURL = String.format(CIPHER_URL, URLEncoder.encode(serviceName, "UTF-8"),
					URLEncoder.encode(encryptedCategory, "UTF-8"),
					URLEncoder.encode(encryptedValueName, "UTF-8"),
					URLEncoder.encode(encryptedValue, "UTF-8"),
					URLEncoder.encode(cipherAuthorizationIdName, "UTF-8"), 
					URLEncoder.encode(cipherAuthorizationId, "UTF-8"));
			URL discoveryURL = new URL(cipherURL);
			HttpURLConnection discoveryConn = (HttpURLConnection) discoveryURL.openConnection();

			discoveryConn.setRequestMethod("GET");
			discoveryConn.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = discoveryConn.getResponseCode();
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(discoveryConn.getInputStream()));
				decryptedString = in.readLine();
			} else {
				response.append("Response Code : ").append(responseCode).append("\n");
				response.append("Response Message : ").append(discoveryConn.getResponseMessage()).append("\n");
			}
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}

		if (decryptedString == null) {
			throw new Exception("Unable to decrypt : " + response.toString());
		}

		return decryptedString;
	}

	private static String determineCentralHost() throws Exception {
		String centralHost = null;
		try {
			centralHost = InetAddress.getLocalHost().getCanonicalHostName();
			if (centralHost != null) {
				if (centralHost.contains(".ec2.internal")) {
					/**
					 * Process is running INSIDE the cloud, give it Private IP
					 */
					centralHost = "172.31.35.139";
				} else {
					/**
					 * Running from out the cloud / development boxes, IP
					 * address of which is 52.23.185.3
					 */
					centralHost = "ec2-52-23-185-3.compute-1.amazonaws.com";
				}
			} else {
				centralHost = "localhost";
			}
		} catch (UnknownHostException e) {
			throw new Exception(e.getMessage(), e);
		}

		return centralHost;
	}

	private static Properties getProperties(String URL, String serviceName) throws Exception {
		Properties discoveryProps = null;
		StringBuffer response = new StringBuffer();

		try {
			URL discoveryURL = new URL(URL + serviceName);
			HttpURLConnection discoveryConn = (HttpURLConnection) discoveryURL.openConnection();

			discoveryConn.setRequestMethod("GET");
			discoveryConn.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = discoveryConn.getResponseCode();
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(discoveryConn.getInputStream()));
				String inputLine = null;

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
					response.append("\n");
				}
				in.close();

				discoveryProps = new Properties();
				discoveryProps.load(new StringReader(response.toString()));
			} else {
				StringWriter writer = new StringWriter();
				IOUtils.copy(discoveryConn.getErrorStream(), writer, StandardCharsets.UTF_8);

				response.append("Response Code : ").append(responseCode).append("\n");
				response.append("Response Message : ").append(writer.toString()).append("\n");
			}
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}

		if (discoveryProps == null) {
			throw new Exception("Unable to retrieve discoveryProperties : " + response.toString());
		}

		return discoveryProps;
	}
}
