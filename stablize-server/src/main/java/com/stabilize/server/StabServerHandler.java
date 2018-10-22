package com.stabilize.server;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stabilize.projo.RpcRequest;
import com.stabilize.projo.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class StabServerHandler extends SimpleChannelInboundHandler<RpcRequest>{
	private static final Logger logger = LoggerFactory
			.getLogger(StabServerHandler.class);
	private Map<String ,Object> map ;

	
	public StabServerHandler(Map<String, Object> map) {
		super();
		this.map = map;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
		// TODO Auto-generated method stub
		RpcResponse rpcResponse = new RpcResponse();
		rpcResponse.setRequestId(msg.getRequestId());
		try {
			Object handler = handler(msg);
			rpcResponse.setReObject(handler);
		} catch (Exception e) {
			rpcResponse.setError(e);
		}
		ctx.writeAndFlush(rpcResponse);
	}
	
	public Object handler(RpcRequest req) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String className = req.getClassName();
		String methodName = req.getMethodName();
		Class<?>[] parameterTypes = req.getParameterTypes();
		Object[] parameters = req.getParameters();
		Object object = map.get(className);
		Class<?> class1 = Class.forName(className);
		Object result = class1.getMethod(methodName, parameterTypes).invoke(object, parameters);
		return result;
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		logger.error("server caught exception", cause);
		ctx.close();
	}
}
