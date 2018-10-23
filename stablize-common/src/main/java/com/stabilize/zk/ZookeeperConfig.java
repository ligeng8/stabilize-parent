package com.stabilize.zk;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stabilize.config.RpcConfig;


@ConfigurationProperties(prefix="spring.stabilize",ignoreUnknownFields=true)
@Configuration
public class ZookeeperConfig {
    @Autowired
	private RpcConfig rpcConfig;
	
	private static Logger logger =  LoggerFactory.getLogger(ZookeeperConfig.class);
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	
	@Bean
	public ZooKeeper zookeeper() {
		try {
			ZooKeeper zooKeeper = new ZooKeeper(rpcConfig.getAddress(), rpcConfig.getSessionTimeout(), new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					// TODO Auto-generated method stub
					if(event.getState().equals(KeeperState.SyncConnected)) {
						countDownLatch.countDown();
					}
				}
				
			});
			
			countDownLatch.await();
			logger.info("zookeeper Connection success");
			zooKeeper.create("/"+rpcConfig.getProto() , "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return zooKeeper;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} 
	}
	
}
