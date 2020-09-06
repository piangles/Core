package com.TBD.core.util.abstractions;

public abstract class AbstractDecrypter implements Decrypter {
	String serviceName = null;
	String encryptedCategory = null;
	String cipherAuthorizationIdName = null;
	String cipherAuthorizationId = null;

	public AbstractDecrypter(String serviceName) {
		this.serviceName = serviceName;
		this.encryptedCategory = "Discovery"; 
	}

	@Override
	public final void init(String cipherAuthorizationIdName, String cipherAuthorizationId) {
		this.cipherAuthorizationIdName = cipherAuthorizationIdName;
		this.cipherAuthorizationId = cipherAuthorizationId;
	}

	public void setEncryptedCategory(String encryptedCategory){
		this.encryptedCategory = encryptedCategory;
	}
	
	protected final String getServiceName() {
		return serviceName;
	}

	protected final String getEncryptedCategory() {
		return encryptedCategory;
	}

	protected final String getCipherAuthorizationIdName() {
		return cipherAuthorizationIdName;
	}

	protected final String getCipherAuthorizationId() {
		return cipherAuthorizationId;
	}
}
