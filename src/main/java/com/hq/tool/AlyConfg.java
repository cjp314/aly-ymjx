package com.hq.tool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: cjp
 * @description: 阿里云配置
 * @create: 2019-12-03 10:09
 */
@Component
@ConfigurationProperties(prefix = "aly")
@Data
public class AlyConfg {

    private List<AlyParams> params;
}
