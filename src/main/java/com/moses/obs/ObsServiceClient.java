package com.moses.obs;

import com.moses.obs.enums.ObsAccessType;
import com.moses.obs.helper.SingTrueHelper;
import com.moses.obs.model.TemporaryToken;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * obs 服务
 * @Author HanKeQi
 * @Date 2020/3/9 12:24 下午
 * @Version 1.0
 **/
public class ObsServiceClient {


    private Logger logger = LoggerFactory.getLogger(ObsServiceClient.class);

    private static OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false)
            .retryOnConnectionFailure(false).cache(null).build();

    private ObsClient obsClient;

    private ObsProperties properties;

    public ObsServiceClient(ObsProperties properties){
        this.properties = properties;
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(properties.getSocketTimeout());
        config.setConnectionTimeout(properties.getConnectionTimeout());
        config.setEndPoint(properties.getEndPoint());
        obsClient = new ObsClient(properties.getAk(),properties.getSk(),config);
    }

    /**
     * 临时token
     * @param properties
     * @param securityToken
     */
    public ObsServiceClient(ObsProperties properties, String securityToken){
        this.properties = properties;
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(properties.getSocketTimeout());
        config.setConnectionTimeout(properties.getConnectionTimeout());
        config.setEndPoint(properties.getEndPoint());
        obsClient = new ObsClient(properties.getAk(),properties.getSk(), securityToken, config);
    }

    /**
     * 临时AK\SK
     * @return
     */
    public TemporaryToken getObsTemporaryToken(){
        ObsTemporaryToken obsTemporaryToken = new ObsTemporaryToken(properties);
        return obsTemporaryToken.getSecurityToken();
    }

    /**
     * 创建一个桶
     * @param bucketName
     * @return
     * @throws ObsException
     */
    public ObsBucket createBucket(String bucketName) throws ObsException {
        return createBucket(bucketName, properties.getBucketLoc());
    }

    /**
     * 创建一个桶
     * @param bucketName
     * @param bucketLoc
     * @return
     * @throws ObsException
     */
    public ObsBucket createBucket(String bucketName, String bucketLoc) throws ObsException {
        ObsBucket obsBucket = new ObsBucket(bucketName, bucketLoc);
        try {
            obsClient.createBucket(obsBucket);
            logger.info("Create bucket: {} successfully!", bucketName);
        }catch (Exception e){
            logger.info("Create bucket: {} error!", bucketName);
        }

        return obsBucket;
    }

    /**
     * 查看桶是否存在
     * @param bucketName
     */
    public boolean getBucketExists(String bucketName){
        boolean exists = obsClient.headBucket(bucketName);
        logger.info("Getting bucket exists {}" , exists);
        return exists;
    }

    /**
     * 获取桶信息
     * @param bucketName
     * @throws ObsException
     */
    public void getBucketStorageInfo(String bucketName) throws ObsException {
        BucketStorageInfo storageInfo = obsClient.getBucketStorageInfo(bucketName);
        logger.info("Getting bucket storageInfo {}", storageInfo);
    }


    /**
     * 删除一个桶
     * @param bucketName
     * @throws ObsException
     */
    public void delBucket(String bucketName) throws ObsException {
        //删除桶下所有碎片
        List<ObsObject> obsObjects = getObsObject(bucketName);
        if (!CollectionUtils.isEmpty(obsObjects)){
            obsObjects.forEach((obsObject)->{
                obsClient.deleteObject(bucketName, obsObject.getObjectKey());
            });
        }
        //删除桶
        obsClient.deleteBucket(bucketName);
    }

    /**
     * bytes上传
     * @param bucketName
     * @param objectKey
     * @param bytes
     * @return
     */
    public PutObjectResult uploadFile(String bucketName, String objectKey, byte[] bytes, ObsAccessType obsAccessType){
        PutObjectResult putObjectResult = obsClient.putObject(bucketName, objectKey, new ByteArrayInputStream(bytes));
        objectAclOperations(bucketName, objectKey, obsAccessType);
        return putObjectResult;
    }

    /**
     * 文件路径上传
     * @param bucketName
     * @param objectKey
     * @param path
     * @return
     */
    public PutObjectResult  uploadFile(String bucketName, String objectKey, String path, ObsAccessType obsAccessType){
        PutObjectResult putObjectResult = obsClient.putObject(bucketName, objectKey, new File(path));
        objectAclOperations(bucketName, objectKey, obsAccessType);
        return putObjectResult;
    }

    /**
     * file上传
     * @param bucketName
     * @param objectKey
     * @param file
     * @return
     */
    public PutObjectResult  uploadFile(String bucketName,String objectKey, File file, ObsAccessType obsAccessType){
        PutObjectResult putObjectResult = obsClient.putObject(bucketName, objectKey, file);
        objectAclOperations(bucketName, objectKey, obsAccessType);
        return putObjectResult;
    }

    /**
     * 删除
     * @param bucketName
     * @param objectKey
     * @param versionId
     * @return
     */
    public boolean deleteFile(String bucketName,String objectKey, String versionId){
        DeleteObjectResult deleteObjectResult = obsClient.deleteObject(bucketName, objectKey, versionId);
        boolean deleteMarker = deleteObjectResult.isDeleteMarker();
        return deleteMarker;
    }


    /**
     * 断点上传
     * @param bucketName
     * @param objectKey
     * @param localFile 指定文件上传的位置
     * @return
     */
    public CompleteMultipartUploadResult uploadFileRenewal(String bucketName,String objectKey, String localFile,  ObsAccessType obsAccessType){
        UploadFileRequest request = new UploadFileRequest(bucketName, objectKey);
        request.setUploadFile(localFile);
        // 设置分段上传时的最大并发数
        request.setTaskNum(5);
        request.setPartSize(10 * 1024 * 1024);
        // 开启断点续传模式
        request.setEnableCheckpoint(true);
        try{
            // 进行断点续传上传
            CompleteMultipartUploadResult result = obsClient.uploadFile(request);
            objectAclOperations(bucketName, objectKey, obsAccessType);
            return result;
        }catch (ObsException e) {
            // 发生异常时可再次调用断点续传上传接口进行重新上传
            logger.error("uploadFile error:{}", e.getErrorMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 断点下载
     * @param bucketName
     * @param objectKey
     * @param versionId
     * @param localFile 制定下载的位置
     * @return
     */
    public DownloadFileResult downloadFileRenewal(String bucketName,String objectKey, String localFile, String versionId){
        DownloadFileRequest request = new DownloadFileRequest(bucketName, objectKey, versionId);
        // 设置下载对象的本地文件路径
        request.setDownloadFile(localFile);
        // 设置分段下载时的最大并发数
        request.setTaskNum(5);
        // 设置分段大小为10MB
        request.setPartSize(10 * 1024 * 1024);
        // 开启断点续传模式
        request.setEnableCheckpoint(true);
        try{
            // 进行断点续传下载
            DownloadFileResult result = obsClient.downloadFile(request);
            return result;
        }catch (ObsException e) {
            // 发生异常时可再次调用断点续传下载接口进行重新下载
            logger.error("download error:{}", e.getErrorMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看
     * @param bucketName
     * @param objectKey
     * @param versionId
     * @return
     */
    public ObsObject getObject(String bucketName,String objectKey, String versionId){
        ObsObject object = obsClient.getObject(bucketName, objectKey, versionId);
        return object;
    }

    /**
     * 文件流查看
     * @param bucketName
     * @param objectKey
     * @return
     */
    public byte[] getObject(String bucketName,String objectKey, ObsAccessType obsAccessType) throws IOException {
        objectAclOperations(bucketName, objectKey, obsAccessType);
        ObsObject object = obsClient.getObject(bucketName, objectKey);
        if (object != null){
            InputStream objectContent = object.getObjectContent();
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = objectContent.read(buff, 0, 1024)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            return in2b;
        }
        return null;
    }

    /**
     * URL 签名访问
     * @param bucketName
     * @param objectKey
     * @param obsAccessType
     * @return
     * @throws Exception
     */
    public String getUrl(String bucketName,String objectKey, ObsAccessType obsAccessType) throws Exception {
        String expireUrl = "https://%s.%s/%s?AccessKeyId=%s&Expires=%s&Signature=%s";
        String publicUrl = "https://%s.%s/%s";
        //自定义域名
        boolean empty = !StringUtils.isEmpty(properties.getCustomUrl());
        if (empty){
            expireUrl = "https://%s/%s?AccessKeyId=%s&Expires=%s&Signature=%s";
            publicUrl = "https://%s/%s";
        }
        //私有文件临时对外开放
        if (obsAccessType.EXPIRE == obsAccessType){
            long expire = System.currentTimeMillis() / 1000 + properties.getExpireSeconds();
            if (empty){
                return String.format(expireUrl, properties.getCustomUrl(), objectKey
                        , properties.getAk(), expire, SingTrueHelper.createUrlSignature(bucketName, objectKey, properties.getSk(), expire));
            }
            return String.format(expireUrl, bucketName, properties.getEndPoint(), objectKey
                    , properties.getAk(), expire, SingTrueHelper.createUrlSignature(bucketName, objectKey, properties.getSk(), expire));
        }
        if (ObsAccessType.PRIVATE == obsAccessType){
            throw new Exception("归档文件无权访问文件");
        }
        //自定域名 直接对外开放
        if (empty){
            return String.format(publicUrl, properties.getCustomUrl(), objectKey);
        }
        return String.format(publicUrl, bucketName, properties.getEndPoint(), objectKey);
    }



    /**
     * ObjectMetadata
     * @param bucketName
     * @param objectKey
     * @param versionId
     * @return
     */
    public ObjectMetadata getObjectMetadata(String bucketName,String objectKey, String versionId){
        ObjectMetadata result = obsClient.getObjectMetadata(bucketName, objectKey);
        return result;
    }

    /**
     * 授权  objectKey 对外开放
     * @param bucketName
     * @param objectKey
     */
    public void objectAclOperations(String bucketName, String objectKey, ObsAccessType obsAccessType) {
        if (obsAccessType != null && ObsAccessType.PUBLIC == obsAccessType){
            try {
                TemporarySignatureRequest req = new TemporarySignatureRequest(HttpMethodEnum.PUT, 0l);
                req.setBucketName(bucketName);
                req.setObjectKey(objectKey);
                Map<String, String> headers = new HashMap<>();
                headers.put("x-obs-acl", "public-read");
                req.setHeaders(headers);
                req.setSpecialParam(SpecialParamEnum.ACL);
                TemporarySignatureResponse res = obsClient.createTemporarySignature(req);
                logger.info("Setting object ACL to public-read using temporary signature url: \t{}", res.getSignedUrl());
                Request.Builder builder = new Request.Builder();
                for (Map.Entry<String, String> entry : res.getActualSignedRequestHeaders().entrySet()) {
                    builder.header(entry.getKey(), entry.getValue());
                }
                builder.url(res.getSignedUrl()).put(RequestBody.create(null, "".getBytes("UTF-8")));
                Call c = httpClient.newCall(builder.build());
                Response execute = c.execute();
                logger.info("\tStatus: {}", execute.code());
                //打印日志
                if (execute.body() != null) {
                    String content = execute.body().string();
                    if (!StringUtils.isEmpty(content)) {
                        logger.info("\tContent:{} \n\n", content);
                    }
                }
            }catch (Exception e){
                logger.error("acl is error bucketName = {}, objectKey = {}, e = {}", bucketName, objectKey, e);
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取当前桶下所有的上传数据
     * @param bucketName
     * @return
     */
    public List<ObsObject> getObsObject(String bucketName){
        ObjectListing objectListing = obsClient.listObjects(bucketName);
        if (objectListing != null && !CollectionUtils.isEmpty(objectListing.getObjects())){
            return objectListing.getObjects();
        }
        return null;
    }

    /**
     * 关闭 设置null
     */
    public void close(){
        try {
            obsClient.close();
            if (obsClient != null){
                obsClient = null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
