package test;

import java.util.concurrent.ConcurrentHashMap;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class t1 {
  public static void main(String[] args){
//    Jedis jedis=new Jedis("35.88.84.246",6379);
//    System.out.println(jedis.get("key"));
//    ConcurrentHashMap<String,String> map=new ConcurrentHashMap<>();
//    map.put("123","hello");
//    map.put("456","world");
//    jedis.hmset("abc",map);
//    System.out.println(jedis.hget("abc","123"));
//    System.out.println(jedis.hlen("abc"));
//    System.out.println(jedis.hmget("abc","123","456"));
//    jedis.close();
    JedisPool pool=new JedisPool(new JedisPoolConfig(),"35.88.84.246",6379);
    Jedis jedis=pool.getResource();
    jedis.hset("skiers","123","sdsdsdsd");
    pool.destroy();
  }

}
