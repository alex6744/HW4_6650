package servlet.skier;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import servlet.channelPool.ChannelPool;

@WebServlet(name = "servlet.skier.SkierServlet", value = "/skiers/*")
public class SkierServlet extends HttpServlet {
  public  ChannelPool channelPool;
  public JedisPool jedisPool;
  @Override
  public void init(ServletConfig config){
    channelPool=new ChannelPool();
    channelPool.init();
    jedisPool=new JedisPool(new JedisPoolConfig(),"54.213.48.86",6379);

  }
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String urlPath = request.getPathInfo();


//    if (urlPath == null || urlPath.isEmpty()) {
//      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      response.getWriter().write("missing parameters");
//      return;
//    }
    String[] urlParts = urlPath.split("/");
//    if(!isUrlValid(urlParts)){
//      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      response.getWriter().write("url is not valid");
//      return;
//    }
    PrintWriter out = response.getWriter();
    if(urlParts.length==3){
      if(!urlParts[2].contains("vertical")){
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      }else {
        response.setStatus(HttpServletResponse.SC_OK);
        Jedis jedis = jedisPool.getResource();
        try {

          response.setContentType("application/json");


          Verticals verticals = new Verticals();
          for (String key : jedis.hkeys(urlParts[1] + ":" + request.getParameter("resortId"))) {
            int vert = Integer
                .parseInt(jedis.hget(urlParts[1] + ":" + request.getParameter("resortId"), key));
            Vertical v = new Vertical(key, vert);

            verticals.add(v);


          }

          out.write(new Gson().toJson(verticals));
        } catch (JedisConnectionException e) {
          if (jedis != null) {
            jedis.close();
          }
        } finally {
          if (jedis != null) {
            jedis.close();
          }
        }

      }


    }else if (urlParts.length==8){
      Jedis jedis = jedisPool.getResource();
      response.setStatus(HttpServletResponse.SC_OK);
     try {
       int cnt = getVerticals(jedis,urlParts);
       out.write(new Gson().toJson(cnt));
       out.flush();
     }catch (JedisConnectionException e) {
       if (jedis != null) {
         jedis.close();
       }
     } finally {
       if (jedis != null) {
         jedis.close();
       }
     }

    }




   //out.println("<h1>" + "msg" + "</h1>");

//    out.println("1");
//    out.println(request.getParameter("seasons"));
//    out.println(request.getParameter("resort"));
   // response.getWriter().write(request.getPathInfo());

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
      String urlPath = request.getPathInfo();
      Gson gson=new Gson();
      if (urlPath == null || urlPath.isEmpty()) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("missing parameters");
        return;
      }
      String[] urlParts = urlPath.split("/");
      if(!isUrlValid(urlParts)){
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("url is not valid");
        return;
      }
    System.out.println(urlPath);

      try {
        int resortID=Integer.parseInt(urlParts[1]);
        String seasonID=urlParts[3];
        String dayID=urlParts[5];
        int skierID=Integer.parseInt(urlParts[7]);


        Channel channel=channelPool.take();

        MyLiftRider myLiftRider =(MyLiftRider) gson.fromJson(bodyContentToString(request),
            MyLiftRider.class);
        PostResponse postRespone=new PostResponse(resortID,seasonID,dayID,skierID,myLiftRider);
        channel.queueDeclare("post",false,false,false,null);
        channel.basicPublish("","post",null,new Gson().toJson(postRespone).getBytes(StandardCharsets.UTF_8));
        channelPool.add(channel);


        channel=channelPool.take();
        channel.queueDeclare("resort_post",false,false,false,null);
        channel.basicPublish("","resort_post",null,new Gson().toJson(postRespone).getBytes(StandardCharsets.UTF_8));
        channelPool.add(channel);

       // System.out.println("bbbbbbbb");
        response.setStatus(HttpServletResponse.SC_CREATED);


        //response.setContentType("application/json");
       // response.getWriter().write(new Gson().toJson(myLiftRider));

      }catch (IllegalArgumentException | InterruptedException e){
        e.printStackTrace();
        response.getWriter().write("missing parameters");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

      }



  }
  public String bodyContentToString(HttpServletRequest request) throws IOException {
    BufferedReader bufferedReader=request.getReader();
    StringBuilder sb=new StringBuilder();
    String line;
    while((line=bufferedReader.readLine())!=null){
      sb.append(line);
    }
    return sb.toString();
  }

  private  boolean isUrlValid(String[] urlPath){
    if(urlPath.length<3){
      return false;
    }
    if(!urlPath[2].equals("seasons")){
      return false;
    }
    if(urlPath[2].equals("seasons")){
      if(urlPath[4].equals("days")){
        if(!urlPath[6].equals("skiers")){
          return false;
        }
      }else{
        return false;
      }
    }
    return true;
  }
  private int getVerticals(Jedis jedis,String[] urlParts) {

    Map<String, String> map = jedis.hgetAll("sk" + urlParts[7] + "_r" + urlParts[1] + "_s" + urlParts[3] + "_d" + urlParts[5]);
    int cnt = 0;
    for(String s : map.values()) {
      cnt += Integer.parseInt(s);
    }
    return cnt;
  }




}
