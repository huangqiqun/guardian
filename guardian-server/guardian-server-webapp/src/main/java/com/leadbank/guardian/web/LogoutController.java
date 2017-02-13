package com.leadbank.guardian.web;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.leadbank.guardian.CentralAuthenticationService;
import com.leadbank.guardian.constant.RedisKey;
import com.leadbank.guardian.web.support.CookieRetrievingCookieGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/logout")
public final class LogoutController {

    @Resource
    private CentralAuthenticationService centralAuthenticationService;
    @Value("logoutView")
    private String logoutView;
    @Resource
    private JedisCluster jedisCluster;

    public LogoutController() {
        /*setCacheSeconds(0);*/
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject handleRequestInternal(String callback, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String ticketGrantingTicketId = request.getParameter("ticketGrantingTicketId");

        if (ticketGrantingTicketId != null) {
            this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("ticketGrantingTicketId", ticketGrantingTicketId);
            final Set<String> clients = this.jedisCluster.smembers(RedisKey.CLIENT);
            result.put("clients", clients);
            return new JSONPObject(callback, result);
        }

        return null;
    }

}
