package com.moses.obs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 *  init obs
 *
 * @Author HanKeQi
 * @Date 2020/3/9 1:17 下午
 * @Version 1.0
 **/
@Configuration
@EnableConfigurationProperties({ObsProperties.class})
@ConditionalOnProperty(prefix="fairyland.huawei.obs",name="enabled",havingValue="true")
public class ObsServiceClientConfig {

    private static Logger logger = LoggerFactory.getLogger(ObsServiceClientConfig.class);

    @Bean
    @ConditionalOnClass({ObsServiceClient.class})
    public ObsServiceClient obsServiceClient(ObsProperties properties) throws Exception{
        if (properties == null || StringUtils.isEmpty(properties.getAk()) ||
                StringUtils.isEmpty(properties.getSk()) || StringUtils.isEmpty(properties.getEndPoint())){
            throw new Exception("please check obs configuration");
        }

        logger.info("obs initialization complete");
        return new ObsServiceClient(properties);
    }

}
