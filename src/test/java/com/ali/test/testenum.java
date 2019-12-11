package com.ali.test;

import com.ali.common.EventType;

public class testenum {
    public static void main(String[] args) {
        String type="1";
        String e= EventType.REDISTER.getEventType();

        if(e.equals(type)){
            System.out.println("注册");
        }
    }
}
