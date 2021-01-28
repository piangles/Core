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

import java.util.concurrent.TimeUnit;

import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.Service;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;
import org.piangles.core.util.Logger;
import org.piangles.core.util.coding.Decoder;
import org.piangles.core.util.coding.Encoder;
import org.piangles.core.util.instrument.InstrumentationConductor;

/**
 * On the server side each request needs to be processed on a separate thread.
 * This needs to be pooled eventually for faster creation of thread. 
 *
 */
public final class RequestProcessingThread extends AbstractContextAwareThread
{
	private String serviceName = null;
	private String preApprovedSessionId = null;
	private Service service = null;
	private SessionValidator sessionValidator = null;
	private SessionDetails sessionDetails = null;
	private Request request = null;
	
	private Encoder encoder = null;
	private Decoder decoder = null;

	private byte[] requestAsBytes = null;
	private ResponseSender responseSender = null; 
	private ServicePerformanceDetails spDetails = null;
	
	
	public RequestProcessingThread(	String serviceName, Service service, 
									String preApprovedSessionId, SessionValidator sessionValidator, 
									Encoder encoder, Decoder decoder,
									byte[] requestAsBytes,
									ResponseSender responseSender)
	{
		this.serviceName = serviceName;
		this.service = service;
		this.preApprovedSessionId = preApprovedSessionId;
		this.sessionValidator = sessionValidator;
		this.encoder = encoder;
		this.decoder = decoder;
		this.requestAsBytes = requestAsBytes;
		this.responseSender = responseSender;
		
		ServiceInstrumentator si = (ServiceInstrumentator) InstrumentationConductor.getInstance().getInstrumentator(ServicePerformanceDetails.NAME);
		this.spDetails = si.getServicePerformanceDetails();
	}
	
	public void run()
	{
		long startTime = System.nanoTime();

		spDetails.incrementNoOfRequests();
		
		Response response = null;
		try
		{
			request = decoder.decode(requestAsBytes, Request.class);
			
			if (request != null)
			{
				request.markTransitTime();
				
				sessionDetails = new SessionDetails(request.getUserId(), request.getSessionId());
				super.init(sessionDetails, request.getTraceId());
				
				response = processRequest(request);
			}
			else
			{
				RuntimeException e = new RuntimeException("Request received was null.");
				response = new Response("Unknown Service", "Unknown Endpoint", e); 
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			if (request != null)
			{
				response = new Response(request.getServiceName(), request.getEndPoint(), request.getTransitTime(), e);
			}
			else
			{
				response = new Response(serviceName, new String(requestAsBytes), e);
			}
		}
		
		if (response != null)
		{
			try
			{
				if (response.isSuccessful())
				{
					spDetails.incrementNoOfSuccessfulResponses();
				}
				else
				{
					spDetails.incrementNoOfFailedResponses();
				}

				if (responseSender != null) //else it is FireAndForgetService
				{
					byte[] encodedBytes = null;
					encodedBytes = encoder.encode(response);
					
					responseSender.send(encodedBytes);
				}
			}
			catch (Exception e)
			{
				System.err.println("Exception trying to send response because of: " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		else
		{
			spDetails.incrementNoOfFailedResponses();
		}
		
		long delayNS = System.nanoTime() - startTime;
		long delayMiS = TimeUnit.NANOSECONDS.toMicros(delayNS);
		long delayMS = TimeUnit.NANOSECONDS.toMillis(delayNS);
		String endpoint = request.getServiceName() + "::" + request.getEndPoint();
		String traceId = null;
		if (request != null && request.getTraceId() != null)
		{
			traceId = request.getTraceId().toString();
		}
		spDetails.record(traceId, delayNS);
		Logger.getInstance().info(String.format("ServerSide-TimeTaken for traceId %s by %s is %d MilliSeconds and %d MicroSeconds.", traceId, endpoint, delayMS, delayMiS));
	}
	
	public String getServiceName()
	{
		return serviceName;
	}
	
	public String getPreApprovedSessionId()
	{
		return preApprovedSessionId;
	}
	
	private Response processRequest(Request request) throws Exception
	{
		Response response = null;

		if (sessionValidator.isSessionValid(request))
		{
			if (service.getServiceMetadata().isEndpointStreamBased(request.getEndPoint()))
			{
				String queueName = request.getTraceId().toString();
				StreamDetails details = new StreamDetails(queueName);

				//Step 1 create StreamProcessingThread
				Stream<?> stream = responseSender.createStream(details);
				StreamRequestProcessingThread spt = new StreamRequestProcessingThread(service, request, stream);
				spt.start();

				//Step 2 return response with StreamDetails so client can start processing the stream
				response = new Response(request.getServiceName(), request.getEndPoint(), request.getTransitTime(), details);
			}
			else
			{
				/**
				 * Make the actual call to the service
				 */
				response = service.process(request);
			}
		}
		else
		{
			RuntimeException e = new RuntimeException(request.getServiceName() + " : UnAuthorized Request. Session could not be validated.");
			response = new Response(request.getServiceName(), e.getMessage(), request.getTransitTime(), e);
		}
		
		return response; 
	}
}
