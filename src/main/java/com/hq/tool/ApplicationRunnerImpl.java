package com.hq.tool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.productivity.java.syslog4j.SyslogConstants;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ApplicationRunnerImpl implements ApplicationRunner {




    @Resource
    private IpHelp ipHelp;

    @Resource
    private UpdateDomainIp updateDomain;

    @Resource
    private  TPUDPNettyLogServer tpudpNettyLogServer;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("程序启动，更新。。。。");
        String ip = ipHelp.getCurrentIp();
        updateDomain.updateDomain(ip);


        EventLoopGroup group = new NioEventLoopGroup();
        log.info("netty 日志接收启动。。。。");
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(tpudpNettyLogServer);

            b.bind(SyslogConstants.SYSLOG_PORT_DEFAULT).sync().channel().closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }

    }



}
