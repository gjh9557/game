package com.ali.utils;

import com.ali.common.CommonData;
import jodd.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

public class JedisUtils {
    private static JedisPool jedisPool ;

    public static Jedis getconn() throws IOException {
        if(jedisPool==null){//外层if来提高效率
            synchronized (JedisUtils.class) {

                if(jedisPool==null){//内层的if来保证jedispool实例的唯一性

                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMaxIdle(Integer.parseInt(CommonUtils.loadApolicationpropertie().getString(CommonData.MAX_IDLE)));
                    poolConfig.setMaxTotal(Integer.parseInt(CommonUtils.loadApolicationpropertie().getString(CommonData.MAX_TOTAL)));
                    poolConfig.setTestOnBorrow(Boolean.valueOf(CommonUtils.loadApolicationpropertie().getString(CommonData.TEST_ON_BORROW)));
                    jedisPool = new JedisPool(poolConfig, (CommonUtils.loadApolicationpropertie().getString(CommonData.REDIS_HOST_PORT)));
                }

            }
        }
        return jedisPool.getResource();
    }
    public static void releaseresour(Jedis jedis){
        if(jedisPool!=null){jedisPool.returnResourceObject(jedis);
        }
    }
}

