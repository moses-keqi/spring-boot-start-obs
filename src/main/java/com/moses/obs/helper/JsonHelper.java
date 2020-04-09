package com.moses.obs.helper;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
/**
 * @Author HanKeQi
 * @Date 2020/4/9 12:40 下午
 * @Version 1.0
 **/
public class JsonHelper {

    /**
     * JSON转对象
     * @param text
     * @param clazz
     * @return
     */
    public static <T> T parseObject(String text,Class<T> clazz){
        return JSON.parseObject(text, clazz);
    }

    /**
     * 对象转JSON
     * @param object
     * @return
     */
    public static String toJSONString(Object object){
        return JSON.toJSONString(object);
    }

    /**
     * 对象转JSON 支持泛型
     * @param text
     * @param type
     * @return
     */
    public static <T> T parseObject(String text,TypeReference<T> type){
        return JSON.parseObject(text, type);
    }

}
