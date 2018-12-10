package com.borris.smt.controller;

import com.borris.annotation.Autowired;
import com.borris.annotation.Controller;
import com.borris.annotation.RequestMapping;

@Controller
public class TestController {

    @Autowired(name="")
    private String test1;

    @RequestMapping("/helloWorld")
    public String helloWorld(){
        return "Hello World";
    }

}
