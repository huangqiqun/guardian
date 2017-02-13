package com.leadbank.guardian.metaq.listener;

import java.util.Set;

import com.leadbank.guardian.constant.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisCluster;

import com.leadbank.guardian.util.HttpClient;
import com.taobao.metamorphosis.client.extension.spring.DefaultMessageListener;
import com.taobao.metamorphosis.client.extension.spring.MetaqMessage;

public class ClientCleanerListener<T> extends DefaultMessageListener<T> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private JedisCluster jedisCluster;
	
	private HttpClient httpClient;

	@Override
	public void onReceiveMessages(MetaqMessage<T> msg) {
		try {
			Set<String> clients = this.jedisCluster.smembers(RedisKey.CLIENT);
			for (String client : clients) {
				boolean validEndPoint = this.httpClient.isValidEndPoint(client);
				if(!validEndPoint){
					this.jedisCluster.srem(RedisKey.CLIENT, client);
				}
			}
		} catch (Exception e) {
			logger.error("MQ执行http连接失败{}", e);
		}
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

}
