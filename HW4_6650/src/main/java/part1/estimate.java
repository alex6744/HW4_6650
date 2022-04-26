package part1;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class estimate {
  public static void main(String[] args) throws InterruptedException {
    String url="http://54.184.180.83:8080/HW1_6650";
    String url1="http://localhost:8080/HW1_6650_war_exploded";
    ApiClient a=new ApiClient();
    a.setBasePath(url);
    SkiersApi apiInstance = new SkiersApi(a);
    // LiftRide | Specify new Season value
    Integer resortID = 56; // Integer | ID of the resort the skier is at
    String seasonID = "seasonID_example"; // String | ID of the ski season
    String dayID = "dayID_example"; // String | ID number of ski day in the ski season
   // Integer | ID of the skier riding the lift
    CountDownLatch latch=new CountDownLatch(1);
      long start=System.currentTimeMillis();
    for(int i=0;i<1;i++){
      int cur=i;
      Runnable thread=()->{

        for(int j=0;j<10000;j++){
          LiftRide body=new LiftRide();
          int skierID = ThreadLocalRandom
              .current().nextInt(0,64);
          int time = ThreadLocalRandom.current().nextInt(0,91);
          int liftID = ThreadLocalRandom.current().nextInt(200000);
          int waitTime = ThreadLocalRandom.current().nextInt(10+1);
          body.setTime(time);
          body.setLiftID(liftID);
          body.setWaitTime(waitTime);
          long post_start=System.currentTimeMillis();
          boolean isFail=false;
          int responseCode=0;
          try {
            ApiResponse response=apiInstance.writeNewLiftRideWithHttpInfo(body,1,"2020","1",skierID);
            responseCode=response.getStatusCode();
          } catch (ApiException e) {
            e.printStackTrace();
            responseCode=e.getCode();
            for(int k=0;k<5;k++){
              try {
                ApiResponse response1=apiInstance.writeNewLiftRideWithHttpInfo(body,1,"2020","1",skierID);
                responseCode=response1.getStatusCode();
                if(responseCode==201){
                  break;
                }
              }catch (ApiException e1){
                e1.printStackTrace();
                responseCode=e.getCode();
              }
            }
            if(responseCode!=201){
             // this.incFail();
            }

          }

        }

       latch.countDown();
      };
      new Thread(thread).start();
    }
    latch.await();
      long end=System.currentTimeMillis();

      System.out.println("10000 request by 1 thread: "+(end-start)+"ms");
      //  Integer result = apiInstance.getSkierDayVertical(resortID, seasonID, dayID, skierID);
      // System.out.println(result);

    }


}
