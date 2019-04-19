package com.hq.tool;

import lombok.extern.slf4j.Slf4j;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.impl.AbstractSyslogServer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

@Slf4j
@Component
public class TPUDPNetSyslogServer extends AbstractSyslogServer {





    protected DatagramSocket createDatagramSocket() throws SocketException, UnknownHostException {

        log.info("创建DatagramSocket端口:{}",SyslogConstants.SYSLOG_PORT_DEFAULT);
        DatagramSocket  datagramSocket = new DatagramSocket(SyslogConstants.SYSLOG_PORT_DEFAULT);

        return datagramSocket;
    }
    @Override
    protected void initialize() throws SyslogRuntimeException {

    }

    protected DatagramSocket ds = null;


    @Override
    public void run() {

        try {
            this.ds = createDatagramSocket();
            this.shutdown = false;

        } catch (SocketException se) {
            return;

        } catch (UnknownHostException uhe) {
            return;
        }

        log.info("启动tplink日志监听。。。");
        byte[] receiveData = new byte[SyslogConstants.SYSLOG_BUFFER_SIZE];

        while(!this.shutdown) {
            try {
                DatagramPacket dp = new DatagramPacket(receiveData,receiveData.length);

                this.ds.receive(dp);

                handleLog(receiveData);

            } catch (SocketException se) {
                log.error("udp soceket异常：",se);

            } catch (IOException ioe) {
                log.error("udp io异常：",ioe);
            }
        }
    }
    private static final String CHARST = "GB2312";
    private  static  final String IPCP_FLAG = "IPCP协商成功，本地地址:";
    private  static  final int IPCP_FLAG_LENGTH = IPCP_FLAG.length();
    private  static  final  String IP_SPLIT_FLAG = ",";

    @Resource
    private AliSDKHelp aliSDKHelp;
    protected void handleLog(byte[] receiveData){
        try {
            String tpLog = new String(receiveData, CHARST);
            log.info("接收日志：{}",tpLog);
            //解析  IPCP协商成功，本地地址:116.24.101.77, 对端地址:116.24.100.1, DNS1:202.96.134.33, DNS2:202.96.128.86
            int ipcpIndex = tpLog.indexOf(IPCP_FLAG);
            if(ipcpIndex > -1){
                String ip =analysisIp(ipcpIndex,tpLog);
                aliSDKHelp.updateDomain(ip);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected  static String analysisIp(int ipcpIndex,String tpLog){

        String str = tpLog.substring(ipcpIndex+IPCP_FLAG_LENGTH);
        String ip = str.split(IP_SPLIT_FLAG,2)[0];
        return ip;
    }
    public static void main(String[] args) {
        String tpLog=  "<13>Apr 19 10:48:57 TL-R476G 100: 2019-04-19 10:48:57 <5> :  192.168.0.148 IPCP协商成功，本地地址:116.24.101.77, 对端地址:116.24.100.1, DNS1:202.96.134.33, DNS2:202.96.128.86";
        int ipcpIndex = tpLog.indexOf(IPCP_FLAG);


        String ip = analysisIp(ipcpIndex,tpLog);
        System.out.println(ip);

    }
}

