package com.leadbank.guardian.client.listener;

import com.leadbank.guardian.constant.RedisKey;
import org.springframework.beans.factory.InitializingBean;

import com.leadbank.common.context.ApplicationContextConfig;

import redis.clients.jedis.JedisCluster;

public class InitialDataListener implements InitializingBean {

	private JedisCluster jedisCluster;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.jedisCluster.sadd(RedisKey.CLIENT, ApplicationContextConfig.get("serverName"));
	}
	
	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

}
