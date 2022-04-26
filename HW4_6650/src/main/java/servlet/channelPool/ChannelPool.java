package servlet.channelPool;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class ChannelPool {

  private static int CHANNEL=128;
  private static BlockingQueue<Channel> blockingDeque;
  private Connection connection;
  private ConnectionFactory factory;

  public void init(){
    try {
      this.blockingDeque=new LinkedBlockingDeque<>();
      this.factory=new ConnectionFactory();
      this.factory.setHost("18.236.180.88");
      this.factory.setPort(5672);
      this.factory.setUsername("admin");
      this.factory.setPassword("12345");
      this.connection=factory.newConnection();
      for(int i=0;i<CHANNEL;i++){
        blockingDeque.add(connection.createChannel());
      }


    } catch (TimeoutException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }


  }
  public Channel take() throws InterruptedException {
    return blockingDeque.take();
  }
  public boolean add(Channel channel){
    return blockingDeque.add(channel);
  }


}
