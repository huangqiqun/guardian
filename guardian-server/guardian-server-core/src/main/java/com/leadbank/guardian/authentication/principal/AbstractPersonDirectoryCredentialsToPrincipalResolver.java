package com.leadbank.guardian.authentication.principal;

import com.leadbank.guardian.constant.RedisKey;
import com.leadbank.guardian.util.ObjectSerializeUtil;
import com.leadbank.guardian.util.SpringContextUtil;
import com.leadbank.mcs.facade.bean.AuthenticateResp;
import com.leadbank.runtime.util.ObjectConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPersonDirectoryCredentialsToPrincipalResolver
    implements CredentialsToPrincipalResolver {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private boolean returnNullIfNoAttributes = false;

    public final Principal resolvePrincipal(final Credentials credentials) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to resolve a principal...");
        }

        final String principalId = extractPrincipalId(credentials);
        
        if (principalId == null) {
            return null;
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Creating SimplePrincipal for ["
                + principalId + "]");
        }

        JedisCluster jedisCluster = (JedisCluster) SpringContextUtil.getBean("jedisCluster");
        final String key = jedisCluster.hget(RedisKey.ACCESSTOKEN, principalId);
        jedisCluster.hdel(RedisKey.ACCESSTOKEN, principalId);
        AuthenticateResp accessTokenResp = null;
        try {
            accessTokenResp = (AuthenticateResp) ObjectSerializeUtil.deserialize(key);
        } catch (Exception e) {
            log.error("序列化错误{}", e);
        }

        String memo = accessTokenResp.getMemo();

        Map<String, Object> attributes = ObjectConvertUtil.json2MapG(memo);

        if (!StringUtils.hasText(memo) & !this.returnNullIfNoAttributes) {
            return new SimplePrincipal(principalId);
        }

        if (!StringUtils.hasText(memo)) {
            return null;
        }

        final Map<String, Object> convertedAttributes = new HashMap<String, Object>();
        convertedAttributes.putAll((Map<String, Object>) attributes.get("memberLogin"));

        return new SimplePrincipal(principalId, convertedAttributes);
    }
    
    protected abstract String extractPrincipalId(Credentials credentials);

    public void setReturnNullIfNoAttributes(final boolean returnNullIfNoAttributes) {
        this.returnNullIfNoAttributes = returnNullIfNoAttributes;
    }
}
