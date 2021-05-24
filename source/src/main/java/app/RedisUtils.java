
package app;

import com.google.gson.Gson;

import app.central.usernode.UserNode;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisExhaustedPoolException;

public class RedisUtils {

    private JedisPool redisPool;

    public RedisUtils() throws JedisExhaustedPoolException {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10000);

        this.redisPool = new JedisPool(poolConfig, "localhost", 6379, Protocol.DEFAULT_TIMEOUT);
    }

    public void setNode(String nodeID, UserNode nodeContent) {

        Jedis connection = this.redisPool.getResource();

        connection.set(nodeID, (new Gson()).toJson(nodeContent));

        connection.close();
    }

    public UserNode getNode(String nodeID) {

        Jedis connection = this.redisPool.getResource();

        String json = connection.get(nodeID);

        connection.close();
        
        return (new Gson()).fromJson(json, UserNode.class);
    }

    public void closePool() {

        this.redisPool.close();
    }
}