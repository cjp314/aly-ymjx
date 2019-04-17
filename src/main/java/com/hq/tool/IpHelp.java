package com.hq.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 获取外网ip
 * @Author  cjp <chenjm314@163.com>
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019/04/16
 */
@Component
@Slf4j
public class IpHelp {

    @Value("${query.ip.url}")
    private  String queryIpUrl;

    public  String getCurrentIp(){

        URL url = null;
        HttpsURLConnection urlconn = null;
        BufferedReader br = null;
        try {
            url = new URL(queryIpUrl);
            SslUtils.ignoreSsl();
            urlconn = (HttpsURLConnection)url.openConnection();
            InputStreamReader insr = new InputStreamReader(urlconn.getInputStream());
            br = new BufferedReader(insr);
            String buf;
            String get= "";
            while ((buf = br.readLine()) != null) {
                get+=buf;
            }
            //测试发现获取的ip存在特殊字符，截取掉

            String ip = clearIp(get);
            return ip;


        } catch (Exception e) {
            log.error("获取外网ip异常：",e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private  String clearIp(String str)
    {
        String ip = null;
        final String number = ".0123456789";
        for(int i = 0;i < str.length(); i ++)
        {
            char s = str.charAt(i);


            if(ip != null){
                if(number.indexOf(s) > -1)
                {
                    ip += String.valueOf(s);
                }else{
                    return ip;
                }
            }

            if(ip == null){
                if(number.indexOf(s) > 0)
                {
                    ip = String.valueOf(s);
                }
            }
        }

        if(isIP(ip)){
            return ip;
        }else {

            log.error("解析外网ip异常，原始str："+str);
            return  null;
        }

    }
    private  boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 )
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }


}
