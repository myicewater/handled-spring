package com.jaxon.demo.service.impl;

import antation.JService;
import com.jaxon.demo.service.HelloService;

@JService("helloService")
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello() {
        return "hello Jaxon";
    }
}
