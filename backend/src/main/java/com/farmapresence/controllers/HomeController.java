package com.farmapresence.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "FarmaPresence API - Backend is running");
        response.put("status", "active");
        response.put("version", "1.0.0");
        response.put("endpoints", new String[]{
            "/usuario/login - POST",
            "/actuator/health - GET",
            "/health - GET"
        });
        return response;
    }
}
