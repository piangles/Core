package com.TBD.core.services.remoting.rest;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

class HostnameVerifierImpl implements HostnameVerifier
{
	@Override
	public boolean verify(String hostName, SSLSession sslSession)
	{
		return true;
	}
}
