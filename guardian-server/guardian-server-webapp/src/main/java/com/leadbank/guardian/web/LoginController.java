package com.leadbank.guardian.web;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.leadbank.guardian.CentralAuthenticationService;
import com.leadbank.guardian.authentication.principal.UsernamePasswordCredentials;
import com.leadbank.guardian.authentication.principal.WebApplicationService;
import com.leadbank.guardian.constant.RedisKey;
import com.leadbank.guardian.services.support.ArgumentExtractor;
import com.leadbank.guardian.ticket.TicketException;
import com.leadbank.guardian.util.ObjectSerializeUtil;
import com.leadbank.guardian.web.support.CookieRetrievingCookieGenerator;
import com.leadbank.guardian.web.support.WebUtils;
import com.leadbank.mcs.facade.bean.AuthenticateResp;
import com.leadbank.runtime.util.ObjectConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/login")
public class LoginController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CentralAuthenticationService centralAuthenticationService;
    @Resource
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;
    @Resource
    private List<ArgumentExtractor> argumentExtractors;
    @Resource
    private JedisCluster jedisCluster;

    private boolean pathPopulated = false;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject login(String callback, UsernamePasswordCredentials credentials, String sessionName, HttpServletRequest request) {
        Assert.notNull(credentials.getUsername(), "username can not be null.");
        Assert.notNull(credentials.getPassword(), "password can not be null.");

        if (!this.pathPopulated) {
            final String contextPath = request.getContextPath();
            final String cookiePath = StringUtils.hasText(contextPath) ? contextPath + "/" : "/";
            logger.info("Setting path for cookies to: " + cookiePath);
            this.ticketGrantingTicketCookieGenerator.setCookiePath(cookiePath);
            this.pathPopulated = true;
        }

        WebApplicationService service = WebUtils.getService(this.argumentExtractors, request);
        if (service != null && logger.isDebugEnabled()) {
            logger.debug("Get service in Request: " + service.getId());
        }

        String ticketGrantingTicketId = null;
        try {
            ticketGrantingTicketId = this.centralAuthenticationService.createTicketGrantingTicket(credentials);
        } catch (final TicketException e) {
            return populateErrorsInstance(callback, credentials);
        }

        String serviceTicketId = null;
        try {
            serviceTicketId = this.centralAuthenticationService.grantServiceTicket(ticketGrantingTicketId, service, sessionName);
        } catch (final TicketException e) {
            logger.error("TicketException", e);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "S");
        result.put("ticketGrantingTicketId", ticketGrantingTicketId);
        result.put("serviceTicketId", serviceTicketId);
        final Set<String> clients = this.jedisCluster.smembers(RedisKey.CLIENT);
        result.put("clients", clients);

        return new JSONPObject(callback, result);
    }

    private JSONPObject populateErrorsInstance(final String callback, final UsernamePasswordCredentials credentials) {
        final String key = jedisCluster.hget(RedisKey.ACCESSTOKEN, credentials.getUsername());
        jedisCluster.hdel(RedisKey.ACCESSTOKEN, credentials.getUsername());
        AuthenticateResp accessTokenResp = null;
        try {
            accessTokenResp = (AuthenticateResp) ObjectSerializeUtil.deserialize(key);
        } catch (Exception e) {
            logger.error("序列化错误{}", e);
        }

        String memo = accessTokenResp.getMemo();

        Map<String, Object> attributes = ObjectConvertUtil.json2MapG(memo);

        return new JSONPObject(callback, attributes.get("memberLogin"));
    }

}
