package com.ali.test;

import com.ali.utils.CommonUtils;
import com.typesafe.config.Config;

/**
 * 对共同工具类的测试
 */

public class testCommonutils {
    public static void main(String[] args) {
        test();
    }
public static void test(){
 Config config= CommonUtils.loadApolicationpropertie();
String prot=config.getString("redis.host.port");
    System.out.println(prot);


}

}
