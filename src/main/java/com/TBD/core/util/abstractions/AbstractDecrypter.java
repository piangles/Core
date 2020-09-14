package com.TBD.core.util.abstractions;

public abstract class AbstractDecrypter implements Decrypter {
	String serviceName = null;
	String encryptedCategory = null;
	String cipherAuthorizationIdName = null;
	String cipherAuthorizationId = null;

	@Override
	public final void init(String serviceName, String encryptedCategory, String cipherAuthorizationIdName, String cipherAuthorizationId) {
		this.serviceName = serviceName;
		this.encryptedCategory = encryptedCategory;
		this.cipherAuthorizationIdName = cipherAuthorizationIdName;
		this.cipherAuthorizationId = cipherAuthorizationId;
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
