package org.piangles.backbone.services.remoting;

import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory
{
	@Override
	public Thread newThread(Runnable r)
	{
		Thread t = new Thread(r);
		// TODO Auto-generated method stub
		return t;
	}
	
}