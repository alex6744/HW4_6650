package consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import servlet.skier.PostResponse;

public class Consumer {
  private static Map<Integer, List<String>> map;
  private static int threadNum;
  private static int basicQos;
  private static String queueName="post";
  private static int count=0;
  private static String host;
  private static String redisHost;
  public static void main(String[] args) throws IOException, TimeoutException {
    map=new ConcurrentHashMap<>();
    threadNum=Integer.parseInt(args[0]);
    basicQos=Integer.parseInt(args[1]);
    host=args[2];
    redisHost=args[3];
    System.out.println("threadNum:"+threadNum);
    System.out.println("basic:"+basicQos);
    ConnectionFactory factory=new ConnectionFactory();

    factory.setHost(host);
    factory.setPort(5672);
    factory.setUsername("admin");
    factory.setPassword("12345");
    JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();

//    jedisPoolConfig.setMaxTotal(threadNum);
//    jedisPoolConfig.setTestOnBorrow(true);
//    jedisPoolConfig.setTestWhileIdle(true);
//    jedisPoolConfig.setTestOnReturn(true);
//    jedisPoolConfig.setMaxWaitMillis(600000);
  JedisPool pool=new JedisPool(jedisPoolConfig,redisHost,6379);
   // final Jedis jedis1= pool.getResource();

    Connection connection=factory.newConnection();
    for(int i=0;i<threadNum;i++){
      int cur=0;

        Runnable thread=()->{
          Jedis jedis= pool.getResource();
          try {

            Channel channel=connection.createChannel();
            channel.basicQos(basicQos);

            channel.queueDeclare(queueName,false,false,false,null);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
              postVertical(delivery,jedis);
              addHash(delivery,jedis);


            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
          } catch (IOException e) {
            e.printStackTrace();
            if(jedis!=null){
              jedis.close();
            }
          }catch (JedisConnectionException e){
              if(jedis!=null){
                jedis.close();
              }
          }finally {
            if(jedis!=null){
              jedis.close();
            }
          }


        };
        new Thread(thread).start();
    }
   // jedis1.close();
  // pool.destroy();


  }
  public static void postVertical(Delivery delivery,Jedis jedis){
    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//              String[] parts=message.split(",");
//              int key=Integer.parseInt(parts[3]);
    Gson gson=new Gson();
    PostResponse postResponse=(PostResponse)  gson.fromJson(message,PostResponse.class);
    System.out.println(postResponse.toString());
    //incNumRequest();
    if(jedis.hexists(postResponse.getSkierId()+":"+postResponse.getResortId(),postResponse.getSeason())){
      String value=jedis.hget(postResponse.getSkierId()+":"+postResponse.getResortId(),postResponse.getSeason());
      int vert=Integer.parseInt(value)+postResponse.getLiftID()*10;
      jedis.hset(postResponse.getSkierId()+":"+postResponse.getResortId(),postResponse.getSeason(),vert+"");
    }else {
      jedis.hset(postResponse.getSkierId()+":"+postResponse.getResortId(),postResponse.getSeason(),postResponse.getLiftID()*10+"");
    }




//              if(map.containsKey(key)){
//                List<String> m=map.get(key);
//                m.add(message);
//              }else{
//                List<String> m=new ArrayList<>();
//                m.add(message);
//                map.put(key,m);
//              }
    System.out.println(" [x] Received message:" + message+ " skierID: "+postResponse.getSkierId());

  }

  public static void addHash(Delivery delivery,Jedis jedis) {
    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

    Gson gson=new Gson();
    PostResponse postResponse=(PostResponse)  gson.fromJson(message,PostResponse.class);
    System.out.println(postResponse.toString());
    String key = "sk" + postResponse.getSkierId() + "_r" + postResponse.getResortId() + "_s" + postResponse.getSeason() + "_d" + postResponse.getDay();
    String hashTime = postResponse.getTime()+"";
    String value = postResponse.getLiftID()*10+"";


      jedis.hset(key, hashTime, value);

  }


}
