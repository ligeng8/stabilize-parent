package com.stabilize.server;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.stabilize.annotation.StabServer;
import com.stabilize.config.RpcConfig;
import com.stabilize.serial.MarShallingCodecFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import com.stabilize.zk.ZookeeperRegistry;

/**
 * 
 * @author ligeng3
 *
 */
@Component
public class StabServerStart implements ApplicationContextAware ,InitializingBean{

	private volatile ConcurrentHashMap< String, Object> handlerMap = new ConcurrentHashMap<>();
	
	@Autowired
	private   ZookeeperRegistry ZookeeperRegistry;
	@Autowired
	private  RpcConfig rpcConfig;
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workGroup = new NioEventLoopGroup();
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workGroup)
		               .channel(NioServerSocketChannel.class)
		               .option(ChannelOption.SO_BACKLOG, 1024)
		               .handler(new LoggingHandler(LogLevel.INFO))
		               .childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// TODO Auto-generated method stub
							ch.pipeline().addLast(MarShallingCodecFactory.buildMarshallingDecoder())
							             .addLast(MarShallingCodecFactory.buildMarshallingEncoder())
							             .addLast(new StabServerHandler(handlerMap));
						}
					});
		ChannelFuture channelFuture = serverBootstrap.bind(rpcConfig.getPort()).sync();
		channelFuture.channel().closeFuture().sync();
	}

	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		Map<String, Object> map = applicationContext.getBeansWithAnnotation(StabServer.class);
		if (map != null && !map.isEmpty()) {
			for (Object serviceBean : map.values()) {
				//从业务实现类上的自定义注解中获取到value，从来获取到业务接口的全名
				StabServer annotation = serviceBean.getClass().getAnnotation(StabServer.class);
				String intefaceClass = annotation.intefaceClass();
				if(intefaceClass.isEmpty()) {
					 Class<?>[] interfaces = serviceBean.getClass().getInterfaces();
					 if(interfaces != null && interfaces.length >1) {
						 intefaceClass = interfaces[0].getName();
					 }else {
						 intefaceClass = serviceBean.getClass().getName();
					 }
				}
				handlerMap.put(intefaceClass, serviceBean);
				String group = annotation.group();
				String module = annotation.module();
				String zNode = "/"+ rpcConfig.getProto() ;
				if(!group.isEmpty()) {
					zNode = zNode +  "/"+ group;
				}
				if(!module.isEmpty()) {
					zNode = zNode +  "/"+ module;
				}
				try {
					zNode = zNode+ "/"+ intefaceClass+ "/"+ "servers"+"/" +InetAddress.getLocalHost().getHostAddress()+annotation.export().substring(annotation.export().indexOf(":"));
					ZookeeperRegistry.createNode(zNode, "");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
	}

}
