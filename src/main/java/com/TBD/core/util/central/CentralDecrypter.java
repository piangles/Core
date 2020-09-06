package com.TBD.core.util.central;

import com.TBD.core.util.abstractions.AbstractDecrypter;

public final class CentralDecrypter extends AbstractDecrypter
{
	public CentralDecrypter(String serviceName)
	{
		super(serviceName);
	}

	@Override
	public String decrypt(String encryptedValueName, String encryptedValue) throws Exception {
		System.out.println("encryptedValueName ::: " + encryptedValueName);
		return CentralClient.decrypt(
				getServiceName(), 
				getEncryptedCategory(), encryptedValueName, encryptedValue, 
				getCipherAuthorizationIdName(), getCipherAuthorizationId());
	}
}
