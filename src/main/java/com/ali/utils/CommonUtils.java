package com.ali.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * 共同的工具类
 */
public class CommonUtils {
public static Config loadApolicationpropertie(){
    return ConfigFactory.load();
}
    public static void mySleep(int second) {
        try {
            Thread.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
