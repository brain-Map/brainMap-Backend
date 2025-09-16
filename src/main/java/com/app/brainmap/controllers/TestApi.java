package com.app.brainmap.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ConcreteProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@AllArgsConstructor
public class TestApi {
    @RequestMapping("/test")
    public String testApi() {
        return "API is working!";
    }
}
