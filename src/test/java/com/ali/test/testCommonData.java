package com.ali.test;

import com.ali.common.CommonData;
import com.ali.utils.CommonUtils;

public class testCommonData {

    public static void main(String[] args) {
    String port=    CommonUtils.loadApolicationpropertie().getString(CommonData.REDIS_HOST_PORT);
        System.out.println(port);

    }
}
