package com.moses.obs.helper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * @Author HanKeQi
 * @Date 2020/3/12 7:23 下午
 * @Version 1.0
 **/
public class SingTrueHelper {


    /**
     * 加密
     * @param bucketName 桶
     * @param objectKey 路径
     * @param secretKey sk
     * @param expire 过去时间
     * @return
     * @throws Exception
     */
    public static String createUrlSignature(String bucketName, String objectKey, String secretKey, Long expire) throws Exception {
        String httpMethod = "GET";
        String contentType = "";
        //时间加30分钟

        String expireTime = String.valueOf(expire);
//        String canonicalizedResource = "/djbx-dev-images//sharefile/dajiabxlife/attchment/01111/202003/e15020d1324d4384949d9f019efe2943.gif";
        String canonicalizedResource = String.format("/%s/%s", bucketName, objectKey);
        String canonicalizedHeaders = "";
        //String canonicalString = httpMethod + "\n" + "\n" + contentType + "\n" + expireTime + "\n" + canonicalizedHeaders + canonicalizedResource;
        String canonicalString = String.format("%s\n\n%s\n%s\n%s%s", httpMethod, contentType, expireTime, canonicalizedHeaders, canonicalizedResource);
        return signWithHmacSha1(secretKey, canonicalString);
    }

    /**
     * 签名秘钥
     * @param secretKey
     * @param canonicalString
     * @return
     * @throws Exception
     */
    private static String signWithHmacSha1(String secretKey, String canonicalString) throws Exception{
        // TODO Auto-generated method stub
        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        String str = Base64.getEncoder().encodeToString(mac.doFinal(canonicalString.getBytes("UTF-8")));
        return URLEncoder.encode(str, "UTF-8");
    }
}
