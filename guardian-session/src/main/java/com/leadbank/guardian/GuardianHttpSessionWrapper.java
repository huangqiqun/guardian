package com.leadbank.guardian;

import com.leadbank.guardian.util.ObjectSerializeUtil;
import com.leadbank.guardian.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class GuardianHttpSessionWrapper extends HttpSessionWrapper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String sessionName;
    private final String sessionId;
    private final JedisCluster jedisCluster;

    public GuardianHttpSessionWrapper(HttpSession session, String sessionName, String sessionId) {
        super(session);
        this.sessionName = sessionName;
        this.sessionId = sessionId;
        jedisCluster = (JedisCluster) SpringContextUtil.getBean("jedisCluster");

        if (!jedisCluster.hexists(sessionName, "sessionId")) {
            try {
                jedisCluster.hset(sessionName, "sessionId", ObjectSerializeUtil.serialize(sessionId));
                jedisCluster.expire(sessionName, this.getMaxInactiveInterval());
            } catch (IOException e) {
                logger.error("序列化错误!sessionId:{}-{}", sessionId, e);
            }
        }
    }

    public String getId() {
        final String val = jedisCluster.hget(sessionName, "sessionId");
        jedisCluster.expire(sessionName, this.getMaxInactiveInterval());
        String id = null;
        try {
            id = (String) ObjectSerializeUtil.deserialize(val);
        } catch (Exception e) {
            logger.error("反序列化错误!sessionId:{}-{}", sessionId, e);
        }
        return id;
    }

    public Object getAttribute(String key) {
        final String val = jedisCluster.hget(sessionName, key);
        jedisCluster.expire(sessionName, this.getMaxInactiveInterval());
        if (val == null) {
            return null;
        }

        Object obj = null;
        try {
            obj = ObjectSerializeUtil.deserialize(val);
        } catch (Exception e) {
            logger.error("反序列化错误!{}-{}", key, e);
        }
        return obj;
    }

    public Enumeration getAttributeNames() {
        final Set<String> keys = jedisCluster.hkeys(sessionName);
        jedisCluster.expire(sessionName, this.getMaxInactiveInterval());
        return new Enumerator(keys.iterator());
    }

    public void invalidate() {
        jedisCluster.del(sessionName);
    }

    public void removeAttribute(String key) {
        jedisCluster.hdel(sessionName, key);
        jedisCluster.expire(sessionName, this.getMaxInactiveInterval());
    }

    public void setAttribute(String key, Object value) {
        try {
            jedisCluster.hset(sessionName, key, ObjectSerializeUtil.serialize(value));
            jedisCluster.expire(sessionName, this.getMaxInactiveInterval());
        } catch (IOException e) {
            logger.error("序列化错误!{}:{}-{}", new Object[]{key, value, e});
        }
    }

    private TreeSet<String> keys(String pattern) {
        logger.debug("Start getting keys...");
        TreeSet<String> keys = new TreeSet<String>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for (String node : clusterNodes.keySet()) {
            logger.debug("Getting keys from: {}", node);
            JedisPool pool = clusterNodes.get(node);
            Jedis connection = pool.getResource();
            try {
                keys.addAll(connection.keys(pattern));
            } catch (Exception e) {
                logger.error("Getting keys error: {}", e);
            } finally {
                logger.debug("Connection closed.");
                connection.close();
            }
        }
        logger.debug("Keys gotten!");
        return keys;
    }

}
