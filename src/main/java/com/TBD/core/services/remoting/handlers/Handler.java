package com.TBD.core.services.remoting.handlers;

import java.lang.reflect.InvocationHandler;
import java.util.Properties;

public interface Handler extends InvocationHandler
{
	public void init(Properties properties) throws HandlerException;
	public default void destroy() {};
}
