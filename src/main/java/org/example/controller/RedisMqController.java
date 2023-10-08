package org.example.controller;

import cn.hutool.json.JSONUtil;
import org.example.mq.pojo.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/mq")
public class RedisMqController {

    @Resource
    RedisTemplate<Object, Object> redisTemplate;

    @PostMapping("/topic/{key}")
    public long createMessageWithTopic(@PathVariable String key, @RequestBody Message<String> message) {
        return redisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(message));
    }

    @PostMapping("/pubsub/{key}")
    public void createMessageWithPubsub(@PathVariable String key, @RequestBody Message<String> message) {
        redisTemplate.convertAndSend(key, JSONUtil.toJsonStr(message));
    }
}
