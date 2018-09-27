package com.anglele.redis;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by jeffeng on 2018-6-12.
 */
public class JobMethod extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobService service = (JobService) context.getJobDetail().getJobDataMap().get("JOB_SERVICE");
        service.executeToQuartz();
    }
}
