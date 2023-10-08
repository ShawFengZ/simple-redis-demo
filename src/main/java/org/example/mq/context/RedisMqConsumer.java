package org.example.mq.context;

import lombok.extern.slf4j.Slf4j;
import org.example.mq.annotations.MessageConsumer;
import org.example.mq.annotations.MessageListener;
import org.example.mq.pojo.Message;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@MessageConsumer
@Slf4j
public class RedisMqConsumer {

    @Resource
    RedisTemplate<Object, Object> redisTemplate;

    @MessageListener(topic = "topic1", mode = MessageListener.Mode.TOPIC)
    public void testTopic1(Message<?> message) {
        log.info("topic1===> " + message);
    }

    @MessageListener(topic = "topic1", mode = MessageListener.Mode.TOPIC)
    public void testTopic11(Message<?> message) {
        log.info("topic11===> " + message);
    }

    @MessageListener(topic = "topic2", mode = MessageListener.Mode.TOPIC)
    public void testTopic2(Message<?> message) {
        log.info("topic2===> " + message);
    }

    @MessageListener(topic = "topic3", mode = MessageListener.Mode.TOPIC)
    public void testTopic3(Message<?> message) {
        log.info("topic3===> " + message);
    }

    @MessageListener(channel = "pubsub", mode = MessageListener.Mode.PUBSUB)
    public void testPubsub1(Message<?> message) {
        log.info("pubsub1===> " + message);
    }

    @MessageListener(channel = "pubsub", mode = MessageListener.Mode.PUBSUB)
    public void testPubsub2(Message<?> message) {
        log.info("pubsub2===> " + message);
    }
}
