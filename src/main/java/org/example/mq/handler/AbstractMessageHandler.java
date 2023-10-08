package org.example.mq.handler;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.mq.pojo.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * handle interface
 * */
@Slf4j
public abstract class AbstractMessageHandler implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    protected RedisTemplate<Object, Object> redisTemplate;

    /**
     * invoke method with reflection.
     * @param method method
     * @param message message
     * @param bean RedisConsumer
     * */
    protected void invokeMethod(Method method, Message<?> message, Object bean) {
        try {
            method.invoke(bean, message);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public AbstractMessageHandler(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected RedisConnection getConnection() {
        return redisTemplate.getRequiredConnectionFactory().getConnection();
    }

    protected Message<?> getMessage(byte[] bytes) {
        String messageStr = new String(bytes, CharsetUtil.CHARSET_UTF_8);
        return JSONUtil.toBean(messageStr, Message.class);
    }

    /**
     * Execute message listening
     * */
    public abstract void invokeMessage(Method method);

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void consumer(Method method, Set<String> consumers, Object bean, byte[] message) {
        Message<?> msg = getMessage(message);
        if (consumers.add(msg.getId())) {
            invokeMethod(method, msg, bean);
        } else {
            log.error("Message have been consumed {}", msg);
        }
    }
}
