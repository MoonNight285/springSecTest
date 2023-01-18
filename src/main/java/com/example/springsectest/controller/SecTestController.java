package com.example.springsectest.controller;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SecTestController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
    
    @RequestMapping(value = "/sec/all", method = RequestMethod.GET)
    public String all() {
        return "sec/all";
    }
    
    @RequestMapping("/sec/member")
    public String member() {
        return "sec/member";
    }
    
    @RequestMapping("/sec/admin")
    public String admin() {
        return "sec/admin";
    }
}
