package com.anglele.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by jeffeng on 2017/8/16.
 */
@SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
@Configuration
public class JedisConfig {
    /**
     * 创建Redis客户端连接工厂
     * 默认构造器会向localhost上的6379端口连接，并且没有密码
     *
     * @return
     */
    @Bean(destroyMethod = "destroy")
    public RedisConnectionFactory cf() {
        JedisConnectionFactory jcf = new JedisConnectionFactory();
        jcf.setHostName(Constant.REDIS_HOST);
        jcf.setDatabase(Constant.REDIS_DB);
        jcf.setUsePool(true);
        jcf.setTimeout(Constant.REDIS_TIMEOUT);
        jcf.setPoolConfig(jedisPoolConfig());
        jcf.setPassword(Constant.REDIS_PASSWORD);
        return jcf;
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(cf());
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setHashKeySerializer(stringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Constant.REDIS_MAX_ACTIVE);
        jedisPoolConfig.setMaxIdle(Constant.REDIS_MAX_IDLE);
        jedisPoolConfig.setMaxWaitMillis(Constant.REDIS_MAX_WAIT);
        return jedisPoolConfig;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        return stringRedisSerializer;
    }

}
