package com.TBD.core.util.central;

import com.TBD.core.util.abstractions.AbstractDecrypter;

public final class CentralDecrypter extends AbstractDecrypter
{
	@Override
	public String decrypt(String encryptedValueName, String encryptedValue) throws Exception {
		return CentralClient.decrypt(
				getServiceName(), 
				getEncryptedCategory(), encryptedValueName, encryptedValue, 
				getCipherAuthorizationIdName(), getCipherAuthorizationId());
	}
}
