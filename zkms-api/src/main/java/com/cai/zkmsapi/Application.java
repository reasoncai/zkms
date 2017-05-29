package com.cai.zkmsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by reason on 17/5/29.
 */
@RestController
@SpringBootApplication
public class Application {
    @RequestMapping(name="HelloService",path = "/hello",method = RequestMethod.GET)
    public String hello(){
        return "Hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
