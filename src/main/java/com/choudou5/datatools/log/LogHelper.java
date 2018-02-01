package com.choudou5.datatools.log;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;

import java.io.File;

/**
 * @Name: 日志助手
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class LogHelper {


    private static File logFile = new File("D:\\data\\logs\\data-tools\\error.log");

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
            FileUtil.appendString(message+"\r\n", logFile, "utf-8");
            e.printStackTrace();
        }
    }
}
