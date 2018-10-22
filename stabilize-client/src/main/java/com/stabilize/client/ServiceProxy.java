package com.stabilize.client;

import java.lang.reflect.Method;
import java.util.UUID;

import org.apache.zookeeper.ZooKeeper;

import com.stabilize.annotation.StabClient;
import com.stabilize.projo.RpcRequest;
import com.stabilize.projo.RpcResponse;
import com.stabilize.zk.ServiceDiscovery;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ServiceProxy implements MethodInterceptor{

	private Enhancer enhancer = new Enhancer();

	private ZooKeeper zooKeeper;
	private String proto;
	
	public ServiceProxy(ZooKeeper zooKeeper, String proto) {
		super();
		this.zooKeeper = zooKeeper;
	}
	public Object createProxy(Class<?> clazz) {
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		return enhancer.create();
	}
	@Override
	public Object intercept(Object arg0, Method method, Object[] args, MethodProxy arg3) throws Throwable {
		// TODO Auto-generated method stub
		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setRequestId(UUID.randomUUID().toString());
		rpcRequest.setClassName(method.getDeclaringClass().getName());
		rpcRequest.setMethodName(method.getName());
		rpcRequest.setParameterTypes(method.getParameterTypes());
		rpcRequest.setParameters(args);
		StabClient stabClient = method.getDeclaringClass().getAnnotation(StabClient.class);
		String zNode = "/"+ proto;
		if(!stabClient.group().isEmpty()) {
			zNode = zNode+"/"+stabClient.group();
		}
		if(stabClient.module().isEmpty()) {
			zNode = zNode+"/"+stabClient.module();
		}
		zNode = zNode + "/servers";
		ServiceDiscovery serviceDiscovery = new ServiceDiscovery(zNode, zooKeeper);
		String disCover = serviceDiscovery.disCover();
		String[] host = disCover.split(":");
		StablizeClient stablizeClient = new StablizeClient(host[0], Integer.valueOf(host[1]));
		RpcResponse rpcResponse = stablizeClient.send(rpcRequest);
		if(rpcResponse.getError() !=  null) {
			throw rpcResponse.getError();
		}
		return rpcResponse.getReObject();
	}
	
}
