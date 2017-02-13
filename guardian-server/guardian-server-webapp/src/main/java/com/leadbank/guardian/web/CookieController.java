package com.leadbank.guardian.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.util.JSONPObject;

@Controller
@RequestMapping("/cookie")
public class CookieController {
	
	@RequestMapping("/getTGC")
    @ResponseBody
    public JSONPObject getTGC(@CookieValue(value = "GuardianTGC", required = false) String guardianTGC, String callback){
    	return new JSONPObject(callback, guardianTGC);
    }
	
}
