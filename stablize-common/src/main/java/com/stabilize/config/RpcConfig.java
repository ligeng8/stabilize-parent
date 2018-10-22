package com.stabilize.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix="spring.stabilize",ignoreUnknownFields=true)
@Configuration
public class RpcConfig {
	
    private   String  proto ;
    
   public  String subPackge;
    private int port;

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
	
	
    
    
}
