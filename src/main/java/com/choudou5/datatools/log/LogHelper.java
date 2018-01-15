package com.choudou5.datatools.log;

import cn.hutool.json.JSONUtil;

/**
 * @Name: 日志助手
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class LogHelper {

    private static boolean switchLog = true;

    public static void closeLog(){
        switchLog = false;
    }

    public static void log(Object object){
        if(switchLog && object != null){
            System.out.println(JSONUtil.toJsonStr(object));
        }
    }

    public static void error(String message, Exception e){
        if(switchLog){
            System.out.println(message + e.getMessage());
        }
    }
}
