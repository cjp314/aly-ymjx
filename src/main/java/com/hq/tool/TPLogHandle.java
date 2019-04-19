package com.hq.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TPLogHandle {
    private  static  final String IPCP_FLAG = "IPCP协商成功，本地地址:";
    private  static  final int IPCP_FLAG_LENGTH = IPCP_FLAG.length();
    private  static  final  String IP_SPLIT_FLAG = ",";

    @Resource
    private AliSDKHelp aliSDKHelp;

    public  void handleLog(String tpLog){


            //解析  IPCP协商成功，本地地址:116.24.101.77, 对端地址:116.24.100.1, DNS1:202.96.134.33, DNS2:202.96.128.86
            int ipcpIndex = tpLog.indexOf(IPCP_FLAG);
            if(ipcpIndex > -1){
                log.info("日志中包含关键字:{}",IPCP_FLAG);
                String ip =analysisIp(ipcpIndex,tpLog);
                log.info("分析日志检测到ip发生变化，新ip{}，开始重新解析域名。。。",ip);
                aliSDKHelp.updateDomain(ip);
            }


    }

    protected   String analysisIp(int ipcpIndex,String tpLog){
        log.info("日志中解析ip。。。");
        String str = tpLog.substring(ipcpIndex+IPCP_FLAG_LENGTH);
        String ip = str.split(IP_SPLIT_FLAG,2)[0];
        return ip;
    }
}
