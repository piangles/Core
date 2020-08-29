package com.TBD.core.util.central;

import com.TBD.core.util.abstractions.Decrypter;

public final class CentralDecrypter implements Decrypter
{
	private String cipherAuthorizationId = null;
	
	public CentralDecrypter(String cipherAuthorizationId)
	{
		this.cipherAuthorizationId = cipherAuthorizationId;
	}
	
	@Override
	public String decrypt(String toBeDecrypted) throws Exception
	{
		return CentralClient.decrypt(cipherAuthorizationId, toBeDecrypted);
	}
}
