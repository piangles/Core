package org.piangles.core.services.remoting.controllers;

import java.util.Properties;

import org.piangles.core.services.Service;

public interface Controller
{
	public void init(String serviceName, Properties properties) throws ControllerException;
	public void start(Service service) throws ControllerException;
	public void stop() throws ControllerException;
	public default void destroy() throws ControllerException {}
}
