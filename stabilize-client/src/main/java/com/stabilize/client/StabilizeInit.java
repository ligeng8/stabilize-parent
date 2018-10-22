package com.stabilize.client;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.stabilize.annotation.StabClient;

public class StabilizeInit implements ApplicationContextAware {
	@Value("#{rpcConfig.subPackge}")
	private String subPackge;
	@Autowired
	private ZooKeeper zooKeeper;
	@Value("#{rpcConfig.proto}")
	private String proto;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

//		Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Component.class);
		String[] names = applicationContext.getBeanDefinitionNames();
		for (String name : names) {
			Object bean = applicationContext.getBean(name);
			String clazz = bean.getClass().getName();
			if (!clazz.startsWith(subPackge)) {
				continue;
			}
			Field[] fields = bean.getClass().getDeclaredFields();
			for (Field field : fields) {
				StabClient stabClient = field.getAnnotation(StabClient.class);
				if (stabClient == null) {
					continue;
				}
				Class<?> type = field.getType();
				ServiceProxy serviceProxy = new ServiceProxy(zooKeeper, proto);
				Object proxy = serviceProxy.createProxy(type);
				try {
					field.set(bean, proxy);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
