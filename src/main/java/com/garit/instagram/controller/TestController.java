package com.garit.instagram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/role-guest/api/test")
    public String test(){
        return "wow~ test success!";
    }
}
