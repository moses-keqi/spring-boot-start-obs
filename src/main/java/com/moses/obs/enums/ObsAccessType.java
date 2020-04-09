package com.moses.obs.enums;

/**
 * @Author HanKeQi
 * @Date 2020/4/9 12:35 下午
 * @Version 1.0
 **/
public enum ObsAccessType {

    PUBLIC("公开"),

    EXPIRE("失效"),

    PRIVATE("归档");

    private String name;

    public String getName() {
        return name;
    }

    ObsAccessType(String name) {
        this.name = name;
    }
}
