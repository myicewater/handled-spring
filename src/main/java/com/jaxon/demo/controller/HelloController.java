package com.jaxon.demo.controller;

import antation.JAutowired;
import antation.JController;
import antation.JRequestMapping;
import com.jaxon.demo.service.HelloService;

@JController
@JRequestMapping("hello")
public class HelloController {

    @JAutowired
    private HelloService helloService;


    @JRequestMapping("hi")
    public String hello(){
        return helloService.hello();
    }


}
