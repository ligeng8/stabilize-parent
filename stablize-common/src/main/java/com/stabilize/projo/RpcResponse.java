package com.stabilize.projo;

import java.io.Serializable;

/**
 * 
 * @author ligeng3
 *
 */
public class RpcResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4838924633878215240L;

	private String requestId;
	
	private Throwable error;
	
	private Object reObject;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public Object getReObject() {
		return reObject;
	}

	public void setReObject(Object reObject) {
		this.reObject = reObject;
	}
	
	
}
