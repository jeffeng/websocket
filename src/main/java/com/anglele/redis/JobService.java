package com.anglele.redis;


import com.anglele.utils.QuartzManager;

/**
 * Created by jeffeng on 2018-6-15.
 */
public abstract class JobService {


    private RedisMessage redisMessage;

    private String object;

    private String key;

    private String rightKey = key + "_RIGHT_QUEUE";

    private Long millis = 3000L;

    private boolean flag = false;

    /**
     * 每隔多久执行一次
     */
    private String cron = "0/5 * * * * ? ";

    /**
     * 每次执行的条目
     */
    private int executeTimes = 10;


    /**
     * 普通的计划任务，key为计划任务的Jobname,默认5秒钟执行一次
     *
     * @param key key为redis定义的key
     */
    public JobService(String key) {
        this.key = key;
        this.run();
    }

    /**
     * 普通的计划任务，key为计划任务的Jobname，key为计划任务的Jobname,自定义设置计划执行时间
     *
     * @param key  key为redis定义的key
     * @param cron 定义job的计划执行时间
     */
    public JobService(String key, String cron) {
        this.cron = cron;
        this.key = key;
        this.run();
    }


    /**
     * 如果是一个redis队列可以调用该方法，key为redis定义的key,默认5秒钟执行一次，每次执行10条消息
     *
     * @param redisMessage
     * @param key          key为redis定义的key
     */
    public JobService(RedisMessage redisMessage, String key) {
        this.redisMessage = redisMessage;
        this.key = key;
        this.run();
    }

    /**
     * 如果是一个redis队列可以调用该方法，key为redis定义的key,自定义设置计划执行时间，每次执行10条消息
     *
     * @param redisMessage
     * @param key          key为redis定义的key
     * @param cron         定义job的计划执行时间
     */
    public JobService(RedisMessage redisMessage, String key, String cron) {
        this.cron = cron;
        this.key = key;
        this.redisMessage = redisMessage;
        this.run();
    }

    /**
     * 如果是一个redis队列可以调用该方法，key为redis定义的key,默认5秒钟执行一次，自定义每次执行的条数
     *
     * @param redisMessage
     * @param key          key为redis定义的key
     * @param executeTimes 每次计划执行的消息条数
     */
    public JobService(RedisMessage redisMessage, String key, int executeTimes) {
        this.executeTimes = executeTimes;
        this.key = key;
        this.redisMessage = redisMessage;
        this.run();
    }

    /**
     * 如果是一个redis队列可以调用该方法，key为redis定义的key,自定义设置计划执行时间，自定义每次执行的条数
     *
     * @param redisMessage
     * @param key          key为redis定义的key
     * @param cron         自定义设置计划执行时间
     * @param executeTimes 自定义每次执行的条数
     */
    public JobService(RedisMessage redisMessage, String key, String cron, int executeTimes) {
        this.key = key;
        this.redisMessage = redisMessage;
        this.cron = cron;
        this.executeTimes = executeTimes;
        this.run();
    }


    /**
     * 如果是redis队列 获的队列消息对象 返回一个String对象
     *
     * @return
     */
    public String getObject() {
        if (null != redisMessage) {
            return object;
        } else {
            return null;
        }
    }

    private void run() {
        QuartzManager.addJob(key, key, key, key, JobMethod.class, cron, this);
    }

    public void executeToQuartz() {
        if (null != redisMessage) {
            for (int i = 1; i <= executeTimes; i++) {
                object = redisMessage.pull(key, rightKey);
                execute();
                redisMessage.pull(rightKey);
                object = null;
            }
        } else {
            execute();
        }

    }

    /**
     * 用户自定义的计划任务执行方法
     */
    public abstract void execute();


}
