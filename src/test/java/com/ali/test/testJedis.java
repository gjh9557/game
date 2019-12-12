package com.ali.test;

import com.ali.utils.JedisClusterUtil;
import com.ali.utils.JedisUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

public class testJedis {
    public static void main(String[] args) {
//        JedisCluster pool= JedisClusterUtil.getJedisfrompool();
//        String a=pool.set("bnm","3");
//        System.out.println(a);
        try {
            Jedis jedis= JedisUtils.getconn();
           String a= jedis.set("bnm","haha");
            System.out.println(a);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
