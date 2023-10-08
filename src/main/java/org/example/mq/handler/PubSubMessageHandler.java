package org.example.mq.handler;

import cn.hutool.core.util.StrUtil;
import org.example.mq.annotations.MessageHandler;
import org.example.mq.annotations.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@MessageHandler(value = MessageListener.Mode.PUBSUB)
public class PubSubMessageHandler extends AbstractMessageHandler {
    public PubSubMessageHandler(RedisTemplate<Object, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public void invokeMessage(Method method) {
        Set<String> consumers = new HashSet<>();
        MessageListener listener = method.getAnnotation(MessageListener.class);
        String channel = getChannel(listener);
        RedisConnection connection = getConnection();
        connection.subscribe((message, pattern) -> {
            Class<?> declaringClass = method.getDeclaringClass();
            Object bean = applicationContext.getBean(declaringClass);
            byte[] body = message.getBody();
            consumer(method, consumers, bean, body);
        }, channel.getBytes());
    }

    private String getChannel(MessageListener listener) {
        String value = listener.value();
        String channel = listener.channel();

        return StrUtil.isBlank(channel) ? value : channel;
    }
}
