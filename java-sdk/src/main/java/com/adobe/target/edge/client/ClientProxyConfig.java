package com.adobe.target.edge.client;

import javax.annotation.Nullable;

public class ClientProxyConfig {
	private String host;
	private int port;

	@Nullable
	private String username;

	@Nullable
	private String password;
	private boolean authProxy = false;
	
	public ClientProxyConfig(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public ClientProxyConfig(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		authProxy = true;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Nullable
	public String getUsername() {
		return username;
	}

	@Nullable
	public String getPassword() {
		return password;
	}

	public boolean isAuthProxy() {
		return authProxy;
	}
}
