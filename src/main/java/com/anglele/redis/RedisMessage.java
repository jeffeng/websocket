package com.anglele.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by jeffeng on 2018-6-4.
 */
@Component
public class RedisMessage {

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * redis发布消息
     *
     * @param channel 订阅频道
     * @param message 订阅频道收到的消息
     */
    public void sendMessage(String channel, String message) {
        stringRedisTemplate.convertAndSend(channel, message);
    }

    /**
     * 向指定的列表左边插入数据
     *
     * @param key
     * @param value
     */
    public void push(String key, String object) {
        stringRedisTemplate.opsForList().leftPush(key, object);
    }

    /**
     * 弹出指定列表右边的数据（如果没有数据，在指定的时间内等待）
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public String rightPop(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 弹出指定列表右边的数据
     *
     * @param key
     * @return
     */
    public String pull(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    /**
     * 弹出指定列表右边,并向指定列表的左边插入(弹出列表如果没有元素,等待指定的时间)
     *
     * @param sourceKey
     * @param destinationKey
     * @param timeout
     * @param unit
     * @return
     */
    public String rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
    }

    /**
     * 弹出指定列表右边,并向指定列表的左边插入
     *
     * @param sourceKey
     * @param destinationKey
     * @return
     */
    public String pull(String sourceKey, String destinationKey) {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
    }


}
