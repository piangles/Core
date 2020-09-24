package org.piangles.core.util.central;

import org.piangles.core.util.abstractions.AbstractDecrypter;

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
