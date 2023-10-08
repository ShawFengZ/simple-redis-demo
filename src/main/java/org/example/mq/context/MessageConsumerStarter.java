package org.example.mq.context;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.mq.annotations.MessageConsumer;
import org.example.mq.annotations.MessageHandler;
import org.example.mq.annotations.MessageListener;
import org.example.mq.handler.AbstractMessageHandler;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Start config, auto running when the project start.
 * For consumer register
 * */
@Component
@Slf4j
public class MessageConsumerStarter implements ApplicationRunner, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * Get all method have annotation MessageConsumer, Open message listening logic
     * */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<MessageListener.Mode, AbstractMessageHandler> invokers = getInvokers();
        applicationContext.getBeansWithAnnotation(MessageConsumer.class).values().parallelStream().forEach(
                consumer -> {
                    Method[] methods = consumer.getClass().getMethods();
                    if (ArrayUtil.isNotEmpty(methods)) {
                        Arrays.stream(methods).parallel().forEach(method -> startMessageListener(method, invokers));
                    }
                }
        );
    }

    private void startMessageListener(Method method, Map<MessageListener.Mode, AbstractMessageHandler> handlerMap) {
        MessageListener listener = method.getAnnotation(MessageListener.class);
        if (null == listener) {
            return;
        }
        MessageListener.Mode mode = listener.mode();
        AbstractMessageHandler handler = handlerMap.get(mode);
        if (null == handler) {
            log.error("invoker is null");
            return;
        }
        log.info("start consume message");
        handler.invokeMessage(method);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Map<MessageListener.Mode, AbstractMessageHandler> getInvokers() {
        Map<String, Object> beansWithAnnotions = applicationContext.getBeansWithAnnotation(MessageHandler.class);

        Map<MessageListener.Mode, AbstractMessageHandler> messageHandlers =
                beansWithAnnotions.values().stream().collect(Collectors.
                        toMap(k -> k.getClass().getAnnotation(MessageHandler.class).value(), k -> (AbstractMessageHandler) k));

        return messageHandlers;
    }
}
