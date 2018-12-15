package com.borris.smt.controller;

import com.borris.annotation.Autowired;
import com.borris.annotation.Component;
import com.borris.annotation.Controller;
import com.borris.annotation.RequestMapping;

@Component("testController")
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private String test1;

    @RequestMapping("/helloWorld")
    public String helloWorld(){
        return "Hello World";
    }

}
