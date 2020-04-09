package com.moses.obs;

import com.moses.obs.helper.JsonHelper;
import com.moses.obs.model.TemporaryToken;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @Author HanKeQi
 * @Date 2020/3/20 3:03 下午
 * @Version 1.0
 **/
public class ObsTemporaryToken {

    private static final Logger logger = LoggerFactory.getLogger(ObsTemporaryToken.class);

    private ObsProperties obsProperties;


    public ObsTemporaryToken(ObsProperties obsProperties){
        this.obsProperties = obsProperties;
    }

    /**
     * 临时ak sk
     * @return
     */
    public TemporaryToken getSecurityToken(){
        String token = getToken();
        TemporaryToken obsTemporaryTokenVo = new TemporaryToken();
        obsTemporaryTokenVo.setToken(token);
        Map<String, Object> credential = getSecurityToken(token);
        Map securityToken = JsonHelper.parseObject(String.valueOf(credential.get("credential")), Map.class);
        obsTemporaryTokenVo.setAk(String.valueOf(securityToken.get("access")));
        obsTemporaryTokenVo.setSk(String.valueOf(securityToken.get("secret")));
        obsTemporaryTokenVo.setSecurityToken(String.valueOf(securityToken.get("securitytoken")));
        obsTemporaryTokenVo.setExpires(obsProperties.getDurationSeconds());
        return obsTemporaryTokenVo;
    }

    private String getToken() {
        Request.Builder builder = new Request.Builder();
        builder.addHeader("Content-Type", "application/json;charset=utf8");
        builder.url(String.format("%s/v3/auth/tokens", obsProperties.getIamEndPoint()));
        String mimeType = "application/json";
        String content = "{\r\n" +
                "			\"auth\": {\r\n" +
                "				\"identity\": {\r\n" +
                "					\"methods\": [\"password\"],\r\n" +
                "					\"password\": {\r\n" +
                "						\"user\": {\r\n" +
                "							\"name\": \"" + obsProperties.getUserName() + "\",\r\n" +
                "							\"password\": \"" + obsProperties.getPassWord() + "\",\r\n" +
                "							\"domain\": {\r\n" +
                "								\"name\": \"" + obsProperties.getDomainName() + "\"\r\n" +
                "							}\r\n" +
                "						}\r\n" +
                "					}\r\n" +
                "				},\r\n" +
                "				\"scope\": {\r\n" +
                "					\"domain\": {\r\n" +
                "						\"name\": \"" + obsProperties.getDomainName() + "\"\r\n" +
                "					}\r\n" +
                "				}\r\n" +
                "			}\r\n" +
                "		  }";
        try {
            builder.post(createRequestBody(mimeType, content));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            return getTokeResponse(builder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getTokeResponse(Request request) throws IOException {
        String subjectToken = null;
        Call c = GetHttpClient().newCall(request);
        Response res = c.execute();
        if (res.headers() != null) {
            String header = res.headers().toString();
            if (header == null || header.trim().equals("")) {
                logger.info("\n");
            } else {
                subjectToken = res.header("X-Subject-Token").toString();
                logger.info("the Token : {}", subjectToken);
//                token = subjectToken;
            }
        }
        res.close();
        return subjectToken;
    }

    private Map<String, Object> getSecurityToken(String token) {
        if(token == null) {

        }
        Request.Builder builder = new Request.Builder();
        builder.addHeader("Content-Type", "application/json;charset=utf8");
        builder.url(String.format("%s/v3.0/OS-CREDENTIAL/securitytokens", obsProperties.getIamEndPoint()));
        String mimeType = "application/json";

		/* request body sample
			 {
			    "auth": {
			        "identity": {
			            "methods": [
			                "token"
			            ],
			            "token": {
			                "id": "***yourToken***",
			                "duration-seconds": "***your-duration-seconds***"
			            }
			        }
			    }
			}
		 */
        String content = "{\r\n" +
                "    \"auth\": {\r\n" +
                "        \"identity\": {\r\n" +
                "            \"methods\": [\r\n" +
                "                \"token\"\r\n" +
                "            ],\r\n" +
                "            \"token\": {\r\n" +
                "                \"id\": \""+ token +"\",\r\n" +
                "                \"duration-seconds\": \""+ obsProperties.getDurationSeconds() +"\"\r\n" +
                "\r\n" +
                "            }\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";

        // String content = "{\"auth\":{\"identity\":{\"methods\":[\"token\"],\"policy\":{\"Version\":\"1.1\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"obs:*\"],\"Resource\":[\"obs:*:*:bucket:*\"]}]},\"Condition\":{\"StringEndWithIfExsits\":{\"g:UserName\":[\"%s\"]},\"Bool\":{\"g:MFAPresent\":[\"true\"]}},\"token\":{\"id\":\"%s\",\"duration-seconds\":%d}}}}";

        //content = String.format(content, userName, token, durationSeconds);

        try {
            builder.post(createRequestBody(mimeType, content));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            String securityTokenResponse = getSecurityTokenResponse(builder.build());
            return JsonHelper.parseObject(securityTokenResponse, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSecurityTokenResponse(Request request) throws IOException {
        Call c = GetHttpClient().newCall(request);
        Response res = c.execute();
        if (res.body() != null) {
            String content = res.body().string();
            if (content == null || content.trim().equals("")) {
                logger.info("\n");
            } else {
                logger.info("Content: {}\n\n", content);
            }
            return  content;
        } else {
            logger.info("\n");
        }
        res.close();
        return  null;
    }

    private static OkHttpClient GetHttpClient() {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { xtm }, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HostnameVerifier DO_NOT_VERIFY = (arg0, arg1) -> true;

        OkHttpClient.Builder builder = new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(false)
                .sslSocketFactory(sslContext.getSocketFactory()).hostnameVerifier(DO_NOT_VERIFY).cache(null);

        //代理
//        if(proxyIsable) {
//            builder.proxy(new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostAddress, proxyPort)));
//
//            Authenticator proxyAuthenticator = new Authenticator() {
//                @Override public Request authenticate(Route route, Response response) throws IOException {
//                    String credential = Credentials.basic(proxyUser, proxyPassword);
//                    return response.request().newBuilder()
//                            .header(Constants.CommonHeaders.PROXY_AUTHORIZATION, credential)
//                            .build();
//                }
//            };
//            builder.proxyAuthenticator(proxyAuthenticator);
//        }

        return builder.build();
    }

    private static RequestBody createRequestBody(String mimeType, String content) throws UnsupportedEncodingException {
        return RequestBody.create(MediaType.parse(mimeType), content.getBytes("UTF-8"));
    }

}
