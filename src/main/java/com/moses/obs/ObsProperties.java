package com.moses.obs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * obs  配置文件
 * @Author HanKeQi
 * @Date 2020/3/9 11:58 上午
 * @Version 1.0
 **/
@ConfigurationProperties(prefix="moses.obs")
@EnableConfigurationProperties
public class ObsProperties {

    //开启 obs
    private boolean enabled;

    //终端节点 默认华北-北京四
    private String endPoint = "obs.cn-north-4.myhuaweicloud.com";
//    private String endPoint = "life-agent-download.djbx.com";

    //自定义域名
    private  String customUrl = "";

    //https://life-agent-download.djbx.com/test/videos/test111.mp4

    //accessKey
    //Access Key ID，接入键标识，用户在OBS系统中的接入键标识，一个接入键标识唯一对应一个用户，一个用户可以同时拥有多个接入键标识。OBS系统通过接入键标识识别访问系统的用户。
    private String ak;

    //secretKey
    //Secret Access Key，安全接入键，用户在OBS系统中的安全接入键，是用户访问OBS系统的密钥，用户根据安全接入键和请求头域生成鉴权信息。安全接入键和接入键标识一一对应。
    private String sk;

    //socket 超时 30s
    private Integer socketTimeout = 30000;

    //connection 超时 10s
    private Integer connectionTimeout = 10000;

    //区域 默认 华北-北京四
    private String bucketLoc = "cn-north-4";

    //// URL有效期，1天,单位s秒
    private Long expireSeconds = 60 * 60 * 24L  ;


    /**临时AK、SK 生成 参数 华为iam**/
    private String iamEndPoint = "https://iam.cn-north-4.myhuaweicloud.com";

    //用户名
    private String userName = "";

    //密码
    private String passWord = "";

    //账户名字
    private String domainName = "";

    private Long durationSeconds = 60 * 60 * 23L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public String getBucketLoc() {
        return bucketLoc;
    }

    public void setBucketLoc(String bucketLoc) {
        this.bucketLoc = bucketLoc;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(Long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public String getIamEndPoint() {
        return iamEndPoint;
    }

    public void setIamEndPoint(String iamEndPoint) {
        this.iamEndPoint = iamEndPoint;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
