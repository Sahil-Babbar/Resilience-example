package com.example.service;

import com.example.dto.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HomeResilienceService {
   ResponseEntity<String> getUsersFromUserService();

   String getResultFromServiceB_withRetry();
   ResponseEntity<String> getMessageFromAnotherService();
   CompletableFuture<ResponseEntity<String>> getMessageFromUserService();
}
