package com.ali.utils;

import redis.clients.jedis.JedisCluster;

import java.util.LinkedList;

public class JedisClusterUtil {
    private static LinkedList<JedisCluster> pool;
    static {
        pool =new LinkedList<>();
        for(int i=1;i<=10;i++){
            JedisCluster instance=createNewcluster();
            pool.add(instance);
        }
    }

    private static JedisCluster createNewcluster() {

        return null;
    }
    public static void resourceRelease(JedisCluster cluster) {
        if (cluster != null) {
            pool.push(cluster);
        }
    }
}
