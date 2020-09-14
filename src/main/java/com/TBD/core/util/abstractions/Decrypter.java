package com.TBD.core.util.abstractions;

public interface Decrypter
{
	public void init(String serviceName, String encryptedCategory, String cipherAuthorizationIdName, String cipherAuthorizationId);
	public String decrypt(String encryptedValueName, String encryptedValue) throws Exception;
}
