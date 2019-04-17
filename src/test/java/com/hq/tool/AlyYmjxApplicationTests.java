package com.hq.tool;

import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordInfoResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlyYmjxApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Resource
    private AliSDKHelp aliSDKHelp;

    @Test
    public void testDom(){
       // DescribeDomainRecordsResponse.Record record = aliSDKHelp.describeDomainRecordsByA();
        //System.out.println(record);
        DescribeDomainRecordInfoResponse aa = aliSDKHelp.describeDomainRecordInfo("xxxx");
        System.out.println(aa);

    }

}
