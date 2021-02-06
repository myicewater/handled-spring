package com.jaxon.demo.service.impl;

import com.jaxon.demo.antation.JService;
import com.jaxon.demo.service.HelloService;

@JService("helloService")
public class HelloServiceImpl implements HelloService {
    @Override
    public void hello() {
        System.out.println("hello Jaxon");
    }
}
