package com.leadbank.guardian.web;

import com.leadbank.guardian.authentication.principal.UsernamePasswordCredentials;
import com.leadbank.guardian.util.UniqueTicketIdGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/generateLoginTicket")
public class GenerateLoginTicketController {

    private static final String PREFIX = "LT";

    private final Log logger = LogFactory.getLog(getClass());

    @Resource(name = "loginTicketUniqueIdGenerator")
    private UniqueTicketIdGenerator ticketIdGenerator;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView generate(HttpSession session) {
        final String loginTicket = this.ticketIdGenerator.getNewTicketId(PREFIX);
        this.logger.debug("Generated login ticket " + loginTicket);
        session.setAttribute("loginTicket", loginTicket);
        ModelAndView view = new ModelAndView("/default/ui/loginView");
        view.addObject("credentials", new UsernamePasswordCredentials());
        view.addObject("loginTicket", loginTicket);
        return view;
    }

}
