package com.hq.tool;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: cjp
 * @description: 更新指定域名ip
 * @create: 2019-12-03 11:03
 */
@Component
public class UpdateDomainIp {

    private List<AliSDKHelp> list;

    @Resource
    private AlyConfg alyConfg;
    @PostConstruct
    private void  init(){
        List<AlyParams> paramsList = alyConfg.getParams();
        list =new ArrayList<>(paramsList.size());
        for (AlyParams params: paramsList){
            AliSDKHelp aliSDKHelp = new AliSDKHelp(params);
            list.add(aliSDKHelp);
        }

    }

    public void updateDomain(String ip){
        for (AliSDKHelp aliSDKHelp: list){
            aliSDKHelp.updateDomain(ip);
        }
    }
}
