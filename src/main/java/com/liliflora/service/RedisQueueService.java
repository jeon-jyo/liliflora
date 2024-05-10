//package com.liliflora.service;
//
//import jakarta.annotation.Resource;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.ListOperations;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class RedisQueueService {
//
//    @Resource(name = "redisTemplate")
//    private ListOperations<String, String> listOps;
//
//    public void push(String queueName, String value) {
//        listOps.rightPush(queueName, value);
//    }
//
//    public String pop(String queueName) {
//        return listOps.leftPop(queueName);
//    }
//
//    public List<String> getAll(String queueName) {
//        return listOps.range(queueName, 0, -1);
//    }
//
//    public boolean isEmpty(String queueName) {
//        Long size = listOps.size(queueName);
//        return size != null && size == 0;
//    }
//}
