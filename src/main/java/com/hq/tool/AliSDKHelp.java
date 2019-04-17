package com.hq.tool;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 阿里云sdk
 * @Author  cjp <chenjm314@163.com>
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019/04/16
 */
@Component
@Slf4j
public class AliSDKHelp {

    @Value("${domain.name}")
    private String DomainName;

    @Value("${region.id}")
    private String regionId;

    @Value("${access.key.id}")
    private String accessKeyId;

    @Value("${secret}")
    private  String secret;

    private String currentRecordId;

    private String currentIp;

    private DescribeDomainRecordInfoResponse describeDomainRecordInfoResponse;

    @Resource
    private IpHelp ipHelp;

    /**
     * @Description 检查域名解析ip是否已经是外网的ip
     * @Author  cjp <chenjm314@163.com>
     * @Date 17:50 2019/4/16
     * @Param
     * @return
     */
    public boolean checkDomain(){
        currentIp = ipHelp.getCurrentIp();
        if(currentIp == null){
            return  false;
        }

        if(checkCurrentRecordId() == false){
            return  false;
        }

        describeDomainRecordInfoResponse = describeDomainRecordInfo(currentRecordId);
        if(describeDomainRecordInfoResponse == null){
            return  false;
        }
        //存在特殊情况服务端把记录删除了，需要重新获取id
        if(describeDomainRecordInfoResponse.getRecordId() == null){
            currentRecordId = null;
            log.warn("服务端把记录删除了，重新获取id");
            return  checkDomain();
        }else {
            if(currentIp.equals(describeDomainRecordInfoResponse.getValue())){
                log.warn("ip相同不需要更新");
                return false;
            }
        }
        return  true;

    }
    private  boolean  checkCurrentRecordId(){
        if(currentRecordId == null){
            DescribeDomainRecordsResponse.Record record = describeDomainRecordsByA();
            if(record != null ){
                currentRecordId = record.getRecordId();
                return true;
            }
        }else {
            return true;
        }
        return false;
    }


    public DescribeDomainRecordInfoResponse  describeDomainRecordInfo(String recordId){
        IAcsClient client = getClient();
        DescribeDomainRecordInfoRequest request = new DescribeDomainRecordInfoRequest();
        request.setRecordId(recordId);

        try {
            DescribeDomainRecordInfoResponse response = client.getAcsResponse(request);
            return response;

        } catch (ServerException e) {
            log.error("获取解析记录列表异常",e);
        } catch (ClientException e) {
            log.error("获取解析记录列表异常",e);
        }
        return null;
    }

    /**
     * @Description 获取域名对应的A记录，设定有且只有一条
     * @Author  cjp <chenjm314@163.com>
     * @Date 19:34 2019/4/16
     * @Param
     * @return
     */
    public DescribeDomainRecordsResponse.Record  describeDomainRecordsByA(){
        IAcsClient client = getClient();
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
        request.setTypeKeyWord("A");
        request.setDomainName(DomainName);

        try {
            DescribeDomainRecordsResponse response = client.getAcsResponse(request);
            List<DescribeDomainRecordsResponse.Record> domainRecords = response.getDomainRecords();
            log.info(domainRecords.toString());
            if(domainRecords.size() == 1){
                return domainRecords.get(0);
            }else {
                log.error("域名:{0}只能有一条A解析记录，当前条数：{1}",DomainName,domainRecords.size());
                return null;
            }

        } catch (ServerException e) {
            log.error("获取解析记录列表异常",e);
        } catch (ClientException e) {
            log.error("获取解析记录列表异常",e);
        }
        return null;
    }

    public IAcsClient getClient(){
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        return  client;
    }

    public void updateDomain(){
        if (checkDomain()) {

            UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
            request.setTTL(describeDomainRecordInfoResponse.getTTL());
            request.setValue(currentIp);
            request.setType(describeDomainRecordInfoResponse.getType());
            request.setRR(describeDomainRecordInfoResponse.getRR());
            request.setRecordId(describeDomainRecordInfoResponse.getRecordId());

            IAcsClient client = getClient();
            try {
                UpdateDomainRecordResponse response = client.getAcsResponse(request);
                log.info("修改解析id为{0}记录成功，新的映射ip：{1}",response.getRecordId(),currentIp);
            } catch (ServerException e) {
                log.error("修改解析记录异常",e);
            } catch (ClientException e) {
                log.error("修改解析记录异常",e);
            }
        }
    }



}
