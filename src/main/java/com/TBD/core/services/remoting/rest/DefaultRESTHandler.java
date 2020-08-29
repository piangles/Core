package com.TBD.core.services.remoting.rest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.TBD.core.util.coding.JSON;
import com.TBD.core.services.remoting.handlers.AbstractHandler;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class DefaultRESTHandler extends AbstractHandler
{
	private static final String PROPS_HOST_PORT = "HostAndPort";
	private static final String PROPS_SECURE = "Secure";
	
	private static final String DELEGATE = "delegate";
	private static final String METHOD_NAME = "methodName";
	private static final String ARGUMENT = "arg";
	private static final String ARGUMENT_LENGTH = "argLength";
	private static final String CONTEXT_PART = "/services/";
	private String HTTP = "http://";
	private String HTTPS = "https://";
	private String baseUrl = null;

	private Client client = null;
	private HashMap<String, Integer> optionTypeMap = null;

	public DefaultRESTHandler(String serviceName)
	{
		super(serviceName);
	}
	
	@Override
	public void init()
	{
		optionTypeMap = new HashMap<String, Integer>();

		Properties props = getProperties();
		String hostAndPort = props.getProperty(PROPS_HOST_PORT);
		boolean secure = Boolean.valueOf(props.getProperty(PROPS_SECURE));

		ClientConfig clientConfig = new DefaultClientConfig();
		if (secure)
		{
			baseUrl = HTTPS + hostAndPort + CONTEXT_PART + getServiceName()	 + "/";
			SSLContext sslContext = null;
			SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
			try
			{
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new javax.net.ssl.TrustManager[] { secureRestClientTrustManager }, null);
				clientConfig.getProperties().put(com.sun.jersey.client.urlconnection.HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
						new com.sun.jersey.client.urlconnection.HTTPSProperties(new HostnameVerifierImpl(), sslContext));
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			baseUrl = HTTP + hostAndPort + CONTEXT_PART + getServiceName() + "/";
		}

		clientConfig.getClasses().add(JacksonJsonProvider.class);
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		client = Client.create(clientConfig);
		// client.addFilter(new LoggingFilter(System.out));
	}

	@Override
	public final Object processMethodCall(Method method, Object[] args) throws Throwable
	{
		Request request = createRESTRequest(method, args);

		String url = baseUrl + request.getEndPointName();
		WebResource endPoint = client.resource(url);

		ClientResponse response = null;
		switch (request.getAction())
		{
		case GET:
			endPoint = endPoint.queryParams(request.getParams());
			System.out.println("Endpoint URL : " + endPoint.getURI());
			response = endPoint.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			break;
		case POST:
			// endPoint.header("Content-Type", MediaType.APPLICATION_JSON);
			System.out.println("Endpoint URL : " + endPoint.getURI());
			response = endPoint.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, request.getBody());
			break;
		default:
			break;
		}

		return processResponse(response, method);
	}

	protected final Request createDefaultRequestFor(Actions action, Method method, int option, Object[] args)
	{
		Request request = null;

		switch (action)
		{
		case GET:
			request = new Request(method.getName(), action);
			if (args != null)
			{
				/**
				 * For reference ONLY endPoint = endPoint.queryParam("arg0",
				 * encode(args[0]));
				 */
				int count = 0;
				for (Object arg : args)
				{
					request.getParams().add("arg" + count, encode(arg));
					count = count + 1;
				}
			}
			break;
		case POST:
			if (option == 2)
			{
				request = new Request(method.getName(), action);
				if (args != null)
				{
					request.setBody(args[0]);
				}
			}
			else
			{
				request = new Request(DELEGATE, action);
				HashMap<String, String> nvPair = new HashMap<String, String>();
				nvPair.put(METHOD_NAME, method.getName());

				if (args != null)
				{
					nvPair.put(ARGUMENT_LENGTH, "" + args.length);
					int count = 0;
					for (Object arg : args)
					{
						nvPair.put(ARGUMENT + count, encode(arg));
						count = count + 1;
					}
				}
				System.out.println("NVPair : " + nvPair);
				request.setBody(nvPair);
			}
			break;
		}

		return request;
	}

	protected final String encode(Object value)
	{
		String valAsStr = "";
		if (value != null)
		{
			/**
			 * Determine if primitive else encode composite as JSON
			 */
			if (isPrimitiveOrWrapper(value))
			{
				valAsStr = value.toString();
			}
			else
			{
				valAsStr = null;
				try
				{
					valAsStr = new String(JSON.getEncoder().encode(value));
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}

		return valAsStr;
	}

	protected Request createRESTRequest(Method method, Object[] args)
	{
		Request request = null;
		/**
		 * Option 1 : If ALL variables in method are Primitive then it is a GET
		 * Option 2 : If the method has ONLY ONE Composite be it Java Composite
		 * or otherwise, POST to correct Endpoint. Option 3 : If the method has
		 * combination of Primitive and Composite OR Multiple Composite -> It is
		 * TO-FIX (Delegate) and will be directed to Delegate.
		 */
		Integer option = optionTypeMap.get(method.getName());
		if (option == null)
		{
			option = discoverOption(method.getParameterTypes());
			optionTypeMap.put(method.getName(), option);
		}

		switch (option)
		{
		case 1:
			System.out.println("Creating GET Request with Option1 : " + method.getName() + " is Good REST GET API.");
			request = createDefaultRequestFor(Actions.GET, method, option, args);
			break;
		case 2:
			System.out.println("Creating POST Request with Option2 : " + method.getName() + " is Good REST POST API.");
			request = createDefaultRequestFor(Actions.POST, method, option, args);
			break;
		case 3:
		default:
			System.out.println("Creating POST Request with Option3 : " + method.getName() + " is TO-FIX(Delegate) REST API.");
			request = createDefaultRequestFor(Actions.POST, method, option, args);
			break;
		}

		return request;
	}

	private Object processResponse(ClientResponse response, Method method)
	{
		Object result = null;
		try
		{
			if (response.getStatus() != 200)
			{
				result = createException(method, response.getEntity(String.class), new Exception(response.getClientResponseStatus().getReasonPhrase()));
			}
			else
			{
				if (!method.getReturnType().equals(Void.TYPE))
				{
					result = response.getEntity(String.class);
					if (result != null)
					{
						byte[] resultInBytes = ((String) result).getBytes();
						if (method.getGenericReturnType() == null)
						{
							result = JSON.getDecoder().decode(resultInBytes, method.getReturnType());
						}
						else
						{
							result = JSON.getDecoder().decode(resultInBytes, method.getGenericReturnType());
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			result = createException(method, "Invocation Exception: " + e.getMessage(), e);
		}

		return result;
	}

	private Integer discoverOption(Class<?>[] paramTypes)
	{
		int option = 0;

		int primitiveCount = 0;
		int javaCompositeCount = 0;
		int realCompositeCount = 0;

		for (Class<?> paramType : paramTypes)
		{
			if (isPrimitiveOrWrapper(paramType))
			{
				primitiveCount++;
			}
			else if (paramType.getCanonicalName().contains("java.util"))
			{
				javaCompositeCount++;
			}
			else // Not a java.util.* class but a real CompositeObject
			{
				realCompositeCount++;
			}
		}

		/**
		 * The first IF does take care of the GET which does not have any
		 * paramters (0 == 0).
		 */
		if (primitiveCount == paramTypes.length)
		{
			option = 1;
			// if (!method.getReturnType().equals(Void.TYPE))
			// {
			// option = 1;
			// }
			// else
			// {
			// option = 2;
			// }
		}
		else if (realCompositeCount == 1 && paramTypes.length == 1)
		{
			option = 2;
		}
		else
		{
			option = 3;
		}

		return option;
	}
}