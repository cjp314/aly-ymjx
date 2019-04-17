package com.hq.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Resource
    private AliSDKHelp aliSDKHelp;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("程序启动，更新。。。。");
        aliSDKHelp.updateDomain();
    }
}
