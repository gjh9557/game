package com.ali.utils;

import com.ali.common.CommonData;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

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
String hostandportinfos=CommonUtils.loadApolicationpropertie().getString(CommonData.REDIS_HOST_PORT);
        Set<HostAndPort> nodes=new LinkedHashSet<>();
        String [] arr=hostandportinfos.split(CommonData.SPLIT_FLG);
        for(int i=0;i<arr.length;i++){
            String[] hostAndport=arr[i].split(CommonData.SPLIT_FLG2);
            String host=hostAndport[0].trim();
            int port=Integer.parseInt(hostAndport[1].trim());
            nodes.add(new HostAndPort(host,port));
        }
        int timeout=Integer.parseInt(CommonUtils.loadApolicationpropertie().getString(CommonData.MAX_WAIT_MILLIS));
        GenericObjectPoolConfig poolConfig=new JedisPoolConfig();
        poolConfig.setMaxIdle(Integer.parseInt(CommonUtils.loadApolicationpropertie().getString(CommonData.MAX_IDLE)));
        poolConfig.setMaxTotal(Integer.parseInt(CommonUtils.loadApolicationpropertie().getString(CommonData.MAX_TOTAL)));
        poolConfig.setMaxWaitMillis(timeout);
        poolConfig.setTestOnBorrow(Boolean.valueOf(CommonUtils.loadApolicationpropertie().getString(CommonData.TEST_ON_BORROW)));

        int maxattempts=Integer.parseInt(CommonUtils.loadApolicationpropertie().getString(CommonData.MAX_ATTEMPTS));



        return new JedisCluster(nodes,timeout,maxattempts,poolConfig);
    }

    public static JedisCluster getJedisfrompool(){
        while(pool.size()==0){
            CommonUtils.mySleep(1);
        }
        return pool.pop();
    }

    public static void resourceRelease(JedisCluster cluster) {
        if (cluster != null) {
            pool.push(cluster);
        }
    }
}
