package org.piangles.core.services.remoting;

import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ThreadFactory;

import org.piangles.core.services.Service;
import org.piangles.core.services.AppType;
import org.piangles.core.services.remoting.controllers.Controller;
import org.piangles.core.services.remoting.controllers.ControllerException;
import org.piangles.core.util.central.CentralClient;

public abstract class AbstractContainer
{
	private static final String CONTROLLER_CLASS_NAME = "ControllerClassName";

	private String serviceName = null;
	private AppType appType = AppType.Service;
	private Properties discoveryProps = null;

	/**
	 * Contains the default session details
	 */
	private SessionDetails sessionDetails = null;

	/**
	 * Service related classes
	 */
	private Object serviceImpl = null;
	private Service controllerServiceDelegate = null;
	private Controller controller = null;

	/**
	 * Process related classes
	 */
	private ThreadFactory threadFactory = null;

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
		this(serviceName, AppType.Service);
	}

	public AbstractContainer(String serviceName, AppType appType)
	{
		this.serviceName = serviceName;
		this.appType = appType;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println(serviceName + " is terminating.");
		}));
	}

	/**
	 * Called from the main Thread it executes steps for the creation of the
	 * service and it's corresponding classes.
	 * 
	 * @throws ContainerException
	 */
	public final void performSteps(String[] args) throws ContainerException
	{
		try
		{
			/**
			 * TODO create a formal command line parsing logic
			 */
			if (args.length == 2)
			{
				if ("wait".equals(args[0]))
				{
					System.out.println("Waiting for " + args[1] + " seconds to start: " + serviceName);
					Thread.sleep(Integer.parseInt(args[1]) * 1000);
				}
			}
			
			discoveryProps = CentralClient.discover(serviceName);

			/**
			 * Create SessionDetails using Predetermined configuration
			 */
			this.sessionDetails = SessionDetailsCreator.createSessionDetails(serviceName, discoveryProps);

			threadFactory = (runnable) -> {
				return new SessionAwareableThread(runnable);
			};

			if (appType == AppType.Process)
			{
				System.out.println(serviceName + " is a Process and will handle it's own lifecycle events.");

				initializeAndRunProcess();
			}
			else if (appType == AppType.Service)
			{
				System.out.println(serviceName + " is a Service and starting it's lifecycle events.");
				
				initializeAndRunService();
			}
			else //It is a combination of both Process and Service
			{
				System.out.println(serviceName + " is both a Process and Service is managed accordingly.");
				
				initializeAndRunProcess();
				initializeAndRunService();
			}
		}
		catch (Exception e)
		{
			throw new ContainerException(e);
		}
	}
	
	protected Object createServiceImpl() throws ContainerException
	{
		return null;
	};
	
	protected Controller createController() throws ContainerException
	{
		Controller controller = null;
		try
		{
			String controllerClassName = discoveryProps.getProperty(CONTROLLER_CLASS_NAME);
			controller = (Controller) Class.forName(controllerClassName).newInstance();
			controller.init(serviceName, discoveryProps);
		}
		catch (Exception e)
		{
			throw new ContainerException(e);
		}

		return controller;
	}

	protected Service createControllerServiceDelegate()
	{
		return new DefaultService(serviceImpl);
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getServiceImpl()
	{
		return (T) serviceImpl;
	}

	protected final ThreadFactory getThreadFactory()
	{
		return threadFactory;
	}

	protected void createProcessImpl() throws ContainerException
	{
	};

	private void initializeAndRunService()
	{
		Thread initializerThread = threadFactory.newThread(() -> {
			try
			{
				System.out.println("Creating " + serviceName + " ServiceImpl.");
				serviceImpl = createServiceImpl();
				
				if (serviceImpl == null) return;

				System.out.println("Creating " + serviceName + " ControllerServiceDelegate.");
				controllerServiceDelegate = createControllerServiceDelegate();

				System.out.println("Creating " + serviceName + " Controller.");
				controller = createController();

				try
				{
					System.out.println("Controller for " + serviceImpl.getClass().getSimpleName() + " being started...");
					controller.start(controllerServiceDelegate);
				}
				catch (ControllerException e)
				{
					throw new ContainerException(e);
				}
			}
			catch (ContainerException e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		});
		initializerThread.start();
	}
	
	private void initializeAndRunProcess()
	{
		Thread initializerThread = threadFactory.newThread(() -> {
			try
			{
				createProcessImpl();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		});
		initializerThread.start();
	}

	class SessionAwareableThread extends Thread implements SessionAwareable
	{
		private Runnable runnable = null;

		public SessionAwareableThread(Runnable runnable)
		{
			this.runnable = runnable;
		}

		@Override
		public void run()
		{
			runnable.run();
		}

		@Override
		public SessionDetails getSessionDetails()
		{
			return sessionDetails;
		}
	}
}
