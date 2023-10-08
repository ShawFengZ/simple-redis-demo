package org.example.mq.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageListener {

    String value() default "";

    String topic() default "";

    String channel() default "";

    Mode mode() default Mode.TOPIC;

    enum Mode {
        /**
         * topic subscribe
         * */
        TOPIC(),

        /**
         * pub/sub
         * */
        PUBSUB(),

        /**
         * Stream
         * */
        STREAM
    }
}
