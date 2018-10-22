package com.stabilize.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZookeeperRegistry {
	@Autowired
	private ZooKeeper zooKeeper;

	/**
	 * 在zookeeper上创建znode节点
	 * 
	 * @param znode
	 * @param value
	 */
	public void createNode(String znode, String value) {
		String[] znodes = znode.split("/");
		String nodePath = "";
		try {
			for (String node : znodes) {
				nodePath = nodePath + "/" + node;
				if (zooKeeper.exists(nodePath, false) == null) {
					if(node.equals(znodes[znodes.length-1])) {
						zooKeeper.create(nodePath, value.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
					    break;
					}
					 zooKeeper.create(nodePath, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				}
			}
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
