package com.stabilize.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring.stabilize", ignoreUnknownFields = true)
@Component
@Validated
public class RpcConfig {
	private String proto = "stabilize";

	public String subPackge = null;
	
	@NotNull
	@NotEmpty
	private Integer port;

	private   int   sessionTimeout = 5000;
	@NotNull
	@NotEmpty
	private String address;
	
	public String getProto() {
		return proto;
	}

	public void setProto(String proto) {
		this.proto = proto;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSubPackge() {
		return subPackge;
	}

	public void setSubPackge(String subPackge) {
		this.subPackge = subPackge;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
