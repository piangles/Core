package com.TBD.core.services.remoting;

import java.util.Properties;
import java.util.TimeZone;

import com.TBD.core.services.Service;
import com.TBD.core.services.remoting.controllers.Controller;
import com.TBD.core.services.remoting.controllers.ControllerException;
import com.TBD.core.util.central.CentralClient;

public abstract class AbstractContainer
{
	private static final String CONTROLLER_CLASS_NAME = "ControllerClassName";
	private String serviceName = null;
	private SessionDetails sessionDetails = null;
	private Object serviceImpl = null;
	private Service controllerServiceDelegate = null;
	private Controller controller = null;

	/**
	 * Only static block in all the application.
	 */
	static
	{
		System.setProperty("user.timezone", "UTC");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public AbstractContainer(String serviceName)
	{
		this.serviceName = serviceName;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println(serviceName + " is terminating.");
		}));
	}
	
	public final void performSteps() throws ContainerException
	{
		try
		{
			new ContainerStarter().start();
		}
		catch (Exception e)
		{
			throw new ContainerException(e);
		}
	}
	
	protected final void start() throws ContainerException
	{
		try
		{
			controller.start(controllerServiceDelegate);
		}
		catch (ControllerException e)
		{
			throw new ContainerException(e);
		}
	}
	
	protected Service createControllerServiceDelegate()
	{
		return new DefaultService(serviceImpl);
	}

	private Controller createController() throws ContainerException
	{
		Controller controller = null;
		try
		{
			Properties props = CentralClient.discover(serviceName);
			this.sessionDetails = SessionDetailsCreator.createSessionDetails(serviceName, props);
			
			String controllerClassName = props.getProperty(CONTROLLER_CLASS_NAME);
			controller = (Controller)Class.forName(controllerClassName).newInstance();
			controller.init(serviceName, props);
		}
		catch (Exception e)
		{
			 throw new ContainerException(e);
		}
		
		return controller;
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getServiceImpl()
	{
		return (T)serviceImpl;
	}
	
	protected abstract Object createServiceImpl() throws ContainerException;
	
	class ContainerStarter extends Thread implements SessionAwareable
	{
		
		@Override
		public void run()
		{
			try
			{
				System.out.println("Creating " + serviceName + " Controller.");
				controller = createController();
				
				System.out.println("Creating " + serviceName + " ServiceImpl.");
				serviceImpl = createServiceImpl();
				
				System.out.println("Creating " + serviceName + " ControllerServiceDelegate.");
				controllerServiceDelegate = createControllerServiceDelegate();

				/**
				 * Comment this code
				 * Register with the registration service
				 */
				System.out.println("Container for " + serviceImpl.getClass().getSimpleName() + " being started...");
				AbstractContainer.this.start();
			}
			catch (ContainerException e)
			{
				//Notify
				e.printStackTrace();
				System.exit(-1);
			}
		}

		@Override
		public SessionDetails getSessionDetails()
		{
			return sessionDetails;
		}
	}
}
