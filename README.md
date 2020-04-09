## 基于华为云esdk-obs-java

> 1、快速接入华为云sdk

## 引入方式
  ```xml
  <dependency>
      <groupId>com.moses.obs</groupId>
      <artifactId>spring-boot-start-obs</artifactId>
      <version>1.0.0</version>
  </dependency>
  ```
  
## 配置文件
```yaml
moses:
    obs:
      enabled: true #开启 obs
      end-point:  obs.cn-north-4.myhuaweicloud.com #终端节点 默认华北-北京四
      custom-url:  #自定义域名
      ak: #永久accessKey
      sk: #永久secretKey
      socket-timeout:  30000 #socket 超时 默认30s
      connection-timeout: 10000 #connection 超时 默认10s
      bucket-loc: cn-north-4 # 默认 华北-北京四
      expire-seconds: 86400 #私有情况下上传成功后URL有效期，1天,单位s秒
      #以下是使用iam账户操作
      iam-end-point: https://iam.cn-north-4.myhuaweicloud.com #临时AK、SK 生成 参数 华为iam   默认华北-北京四
      user-name: #iam用户名
      pass-word: #iam密码
      domain-name: #iam账户名字
      duration-seconds: 82800 #临时token失效时间
```
## 开发环境
> JDK1.8、Maven、SpringBoot 2.2.4.RELEASE、fastjson 1.2.68 、华为云esdk-obs-java 3.19.7

#
```text
    ├─src
    │  ├─main
    │  │  ├─java
    │  │  │  └─com
    │  │  │      └─moses
    │  │  │          └─obs
    │  │  │             │  ObsProperties.java  
    │  │  │             │  ObsServiceClient.java  
    │  │  │             │  ObsServiceClientConfig.java  
    │  │  │             │  ObsTemporaryToken.java  
    │  │  │             └─enums
    │  │  │             │   ObsAccessType.java
    │  │  │             └─helper
    │  │  │             │  JsonHelper
    │  │  │             │  SingTrueHelper
    │  │  │             └─model
    │  │  │             │  TemporaryToken
         
    │  │  ├─resources
    │  │  │  │  banner.txt
    │  │  │  │  spring.factories
    │  │  │  │  spring.provides
    │  │  │  │  
    │  │              
    │  └─test
    │      └─java
```
