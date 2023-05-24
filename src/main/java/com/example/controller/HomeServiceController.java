package com.example.controller;

import com.example.service.HomeResilienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class HomeServiceController {

    @Autowired
    HomeResilienceService homeResilienceService;
    @GetMapping("/users")
    public ResponseEntity<String> getUsers(){
        return homeResilienceService.getUsersFromUserService();
    }
    @GetMapping("/messages")
    public CompletableFuture<ResponseEntity<String>> messages(){
        return homeResilienceService.getMessageFromUserService();
    }
}
