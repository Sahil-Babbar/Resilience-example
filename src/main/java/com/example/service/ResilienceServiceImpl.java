package com.example.service;

import com.example.model.ResilienceConstants;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ResilienceServiceImpl implements HomeResilienceService {

    @Autowired
    RestTemplate restTemplate;
    private static final String BASE_URL="http://localhost:8081/";

    @Override
//    @CircuitBreaker(name= ResilienceConstants.CIRCUIT_BREAKER,fallbackMethod = ResilienceConstants.CIRCUIT_BREAKER_FALLBACK)
//    @RateLimiter(name=ResilienceConstants.RATE_LIMITER,fallbackMethod = ResilienceConstants.RATE_LIMITER_FALLBACK)
    @Retry(name=ResilienceConstants.RETRY,fallbackMethod = ResilienceConstants.RETRY_FALLBACK)

    public ResponseEntity<String> getUsersFromUserService(){
        String url = BASE_URL + "getAllUsers";
        String response= restTemplate.getForObject(url, String.class);
        return new ResponseEntity<String>(response,HttpStatus.OK);
    }

    @Override
    @TimeLimiter(name=ResilienceConstants.TIME_LIMITER,fallbackMethod = ResilienceConstants.TIME_LIMIER_FALLBACK)
//    @Bulkhead(name = ResilienceConstants.BULK_HEAD,fallbackMethod = ResilienceConstants.BULK_HEAD_FALLBACK)
    public CompletableFuture<ResponseEntity<String>> getMessageFromUserService() {
        String url= BASE_URL+"showMessage";
        return CompletableFuture.supplyAsync(() -> {
            String result = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(result);
        });
//         String result= restTemplate.getForObject(url,String.class);
//         new ResponseEntity<String>(result,HttpStatus.OK);
//         return CompletableFuture.completedFuture(ResponseEntity<String>(result,HttpStatus.OK));
    }

    @Bulkhead(name = ResilienceConstants.BULK_HEAD,fallbackMethod = ResilienceConstants.BULK_HEAD_FALLBACK)
    public ResponseEntity<String> getMessageFromAnotherService() {
        String url= BASE_URL+"showMessage";
            String result = restTemplate.getForObject(url, String.class);
            return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @Override
    @Retry(name=ResilienceConstants.RETRY,fallbackMethod = ResilienceConstants.RETRY_FALLBACK)
    public String getResultFromServiceB_withRetry(){
        String url = BASE_URL + "userDetails";
        return restTemplate.getForEntity(url,String.class).getBody();

    }
    public ResponseEntity<String> circuitBreakerFallback(Exception e){
        log.info("circuit breaker Fallback");
        return new ResponseEntity<>("Service B is down\ncircuitBreakerFallback method is called...",HttpStatus.NOT_FOUND);
    }
    public CompletableFuture<ResponseEntity<String>> timeLimiterFallback(){
        log.info("timeLimiterFallback");
        return CompletableFuture.supplyAsync(() -> {
            String result = "Timeout for request";
            return new ResponseEntity<String>(result, HttpStatus.REQUEST_TIMEOUT);
        });
    }
    public ResponseEntity<String> rateLimiterFallback(Exception e){
        log.info("rate Limiter Fallback");
        String response= "Too Many request";
        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }
    public ResponseEntity<String> retryFallback(Exception e){
        log.info("retry Fallback");
        String response= "service is down";
        return new ResponseEntity<>(response, HttpStatus.REQUEST_TIMEOUT);
    }
    public ResponseEntity<String> bulkHeadFallBack(Exception e){
        log.info("Bulk head Fallback");
        return new ResponseEntity<>("User Service is Down ",HttpStatus.BAD_GATEWAY);
    }
}
