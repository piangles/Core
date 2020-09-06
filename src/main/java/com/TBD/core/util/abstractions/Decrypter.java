package com.TBD.core.util.abstractions;

public interface Decrypter
{
	public void init(String cipherAuthorizationIdName, String cipherAuthorizationId);
	public void setEncryptedCategory(String encryptedCategory);
	public String decrypt(String encryptedValueName, String encryptedValue) throws Exception;
}
