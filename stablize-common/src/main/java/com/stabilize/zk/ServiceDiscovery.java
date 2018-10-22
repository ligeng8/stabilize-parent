package com.stabilize.zk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 
 * 
 * @author ligeng3
 *
 */
public class ServiceDiscovery {

	private String serviceNode;
	
	private List<String> serviceList = new ArrayList<>();
	
	private ZooKeeper zk;

	public ServiceDiscovery(String serviceNode, ZooKeeper zk) {
		super();
		this.serviceNode = serviceNode;
		this.zk = zk;
		 watchNode();
	}
	
	public String disCover() {
		if(serviceList == null || serviceList.isEmpty()) {
			return null ;
		}
		Collections.sort(serviceList);
		if(serviceList.size() == 1) {
			return serviceList.get(0);
		}
		return serviceList.get(ThreadLocalRandom.current().nextInt(serviceList.size()));
	}
	
	
	public void watchNode() {
		try {
			serviceList = zk.getChildren(serviceNode, new Watcher() {
				
				@Override
				public void process(WatchedEvent event) {
					// TODO Auto-generated method stub
					if(event.getState().equals(Event.EventType.NodeChildrenChanged)) {
						watchNode();
					}
					
				}
			});
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
