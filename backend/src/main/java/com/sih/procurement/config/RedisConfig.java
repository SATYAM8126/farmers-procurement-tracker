package com.sih.procurement.config;

import com.sih.procurement.listener.QueueEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    // Every publish/subscribe in this app happens on this one channel name
    public static final String QUEUE_CHANNEL = "queue-events";

    @Bean
    public ChannelTopic queueTopic() {
        return new ChannelTopic(QUEUE_CHANNEL);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            QueueEventListener queueEventListener,
            ChannelTopic queueTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(queueEventListener, queueTopic);
        return container;
    }
}
