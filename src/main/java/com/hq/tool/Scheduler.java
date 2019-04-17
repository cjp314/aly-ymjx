package com.hq.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class Scheduler {


    @Resource
    private AliSDKHelp aliSDKHelp;

    @Scheduled(cron = "0 05 03 ? * *")
    public void testTasks() {
        log.info("开始执行任务。。。。。。。。。。。。。。。。。。");
        aliSDKHelp.updateDomain();
    }
}
