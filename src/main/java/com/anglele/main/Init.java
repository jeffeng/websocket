package com.anglele.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anglele.config.Constant;
import com.anglele.config.LocalConfig;
import com.anglele.main.Run;
import com.anglele.netty.BaseWebSocketServerHandler;
import com.anglele.netty.WebsocketServer;
import com.anglele.redis.JobService;
import com.anglele.redis.RedisMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by jeffeng on 2018-9-27.
 */
public class Init {
    public Init() {
        try {
            Constant.loadProperties();
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LocalConfig.class);
            RedisMessage redisMessage = context.getBean(RedisMessage.class);
            System.out.println("服务启动成功!");
            new JobService(redisMessage, Constant.WEB_SOCKET_KEY, Constant.WEB_SOCKET_EXECUTE_CRON, Constant.WEB_SOCKET_EXECUTE_TIMES) {
                @Override
                public void execute() {
                    String message = this.getObject();
                    if (StringUtils.isNotEmpty(message)) {
                        JSONObject jsonObject = JSON.parseObject(message);
                        BaseWebSocketServerHandler.push(jsonObject.getString("id"), jsonObject.getString("message"));
                        System.out.println("推送用户=>" + jsonObject.getString("id"));
                    }
                }
            };
            new WebsocketServer(Constant.WEB_SOCKET_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
