package com.moses.obs.model;

import java.io.Serializable;

/**
 * @Author HanKeQi
 * @Date 2020/4/9 12:37 下午
 * @Version 1.0
 **/
public class TemporaryToken implements Serializable {

    private String token;

    private String ak;

    private String expiresAt;

    private Long expires;

    private String sk;

    private String securityToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

}
