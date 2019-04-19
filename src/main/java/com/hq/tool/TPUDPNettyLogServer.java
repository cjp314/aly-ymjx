package com.hq.tool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;

@Component
@Slf4j
public class TPUDPNettyLogServer extends SimpleChannelInboundHandler<DatagramPacket> {

    private  final Charset charset = Charset.forName("GB2312");
    @Resource
    private TPLogHandle tpLogHandle;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        String tpLog = datagramPacket.content().toString(charset);
        log.info("接收日志：{}",tpLog);
        tpLogHandle.handleLog(tpLog);

    }
}
