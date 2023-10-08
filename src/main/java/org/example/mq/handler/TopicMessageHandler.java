package org.example.mq.handler;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.mq.annotations.MessageHandler;
import org.example.mq.annotations.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MessageHandler(value = MessageListener.Mode.TOPIC)
@Slf4j
public class TopicMessageHandler extends AbstractMessageHandler {

    public TopicMessageHandler(RedisTemplate<Object, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public void invokeMessage(Method method) {
        Set<String> consumers = new HashSet<>();
        MessageListener annotation = method.getAnnotation(MessageListener.class);
        String topic = getTopic(annotation);
        RedisConnection connection = getConnection();
        Class<?> declaringClass = method.getDeclaringClass();
        Object bean = applicationContext.getBean(declaringClass);
        while (true) {
            List<byte[]> bytes = connection.bRPop(1, topic.getBytes());
            if (CollectionUtil.isNotEmpty(bytes)) {
                log.info("bytes length {}", bytes.size());
                if (bytes.get(1) != null) {
                    consumer(method, consumers, bean, bytes.get(1));
                } else {
                    log.error("bytes don't have 1");
                }
            }
        }
    }

    private String getTopic(MessageListener annotation) {
        String value = annotation.value();
        String topic = annotation.topic();
        return StrUtil.isBlank(topic) ? value : topic;
    }
}
