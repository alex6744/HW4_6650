package part2;

import com.opencsv.CSVWriter;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.ResortIDSeasonsBody;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class Phase {
  private CountDownLatch full_latch;
  private CountDownLatch twenty_percent_latch;
  private int numThreads;
  private int numRuns;
  private int numSkiers;
  private int numLifts;
  private String url;
  private int numPost;
  private SkiersApi skiersApi;
  private int startTime;
  private int endTime;
  private int numRequest;
  private int fail;
  private Queue<Record> rec=new ConcurrentLinkedQueue<>();
  private int phase;
  private ResortsApi resortsApi;
  public Phase(int numThreads, int numRuns, int numSkiers, int numLifts, String url,int startTime,int endTime,int phase) {
    this.numThreads = numThreads;
    this.numRuns = numRuns;
    this.numSkiers = numSkiers;
    this.numLifts = numLifts;
    this.url = url;
    this.numPost=(int)Math.ceil(this.numRuns*(1.0*this.numSkiers/this.numThreads));
    if(phase==3){
     // this.numPost=(int) Math.ceil(this.numRuns);
    }
    ApiClient apiClient=new ApiClient();
    apiClient.setBasePath(this.url);
    this.resortsApi=new ResortsApi(apiClient);
    this.skiersApi=new SkiersApi(apiClient);
    this.startTime=startTime;
    this.endTime=endTime;
    this.full_latch=new CountDownLatch(this.numThreads);
    this.twenty_percent_latch=new CountDownLatch((int)(numThreads*0.2));
    this.numRequest=0;
    this.fail=0;
    this.phase=phase;
  }
  public void run(){
//    System.out.println("Thread :"+numThreads);
//    System.out.println("runs :"+numRuns);
//    System.out.println("posts :"+numPost);
//    System.out.println("s :"+1.0*this.numSkiers/numThreads);
    for(int i=0;i<this.numThreads;i++){
      int cur=i;
      Runnable thread=()->{

        for(int j=0;j<this.numPost;j++){
          LiftRide body=new LiftRide();
          int skierID = ThreadLocalRandom.current().nextInt(cur*numSkiers/numThreads,cur*numSkiers/numThreads+numSkiers/numThreads+1);
          int time = ThreadLocalRandom.current().nextInt(this.startTime,this.endTime+1);
          int liftID = ThreadLocalRandom.current().nextInt(this.numLifts+1);
          int waitTime = ThreadLocalRandom.current().nextInt(10+1);
          body.setTime(time);
          body.setLiftID(liftID);
          body.setWaitTime(waitTime);
          ResortIDSeasonsBody body1=new ResortIDSeasonsBody();
          int year=ThreadLocalRandom.current().nextInt(1950,2022+1);
          int resortId=ThreadLocalRandom.current().nextInt(cur*numSkiers/numThreads,cur*numSkiers/numThreads+numSkiers/numThreads+1);
          body1.setYear(year+"");
          long post_start=System.currentTimeMillis();
          boolean isFail=false;
          int responseCode=0;
          try {
          //  ApiResponse response=skiersApi.writeNewLiftRideWithHttpInfo(body,1,"2020","1",skierID);
            ApiResponse response0=resortsApi.addSeasonWithHttpInfo(body1,resortId);
            responseCode=response0.getStatusCode();
          } catch (ApiException e) {
            e.printStackTrace();
            responseCode=e.getCode();
            for(int k=0;k<5;k++){
              try {
               //ApiResponse response1=skiersApi.writeNewLiftRideWithHttpInfo(body,1,"2020","1",skierID);
                ApiResponse response11=resortsApi.addSeasonWithHttpInfo(body1,resortId);
                responseCode=response11.getStatusCode();
                if(responseCode==201){
                  break;
                }
              }catch (ApiException e1){
                e1.printStackTrace();
                responseCode=e.getCode();
              }
            }
            if(responseCode!=201){
              this.incFail();
            }

          }finally {
            add_new_record(post_start,"post",System.currentTimeMillis()-post_start,responseCode);
            incNumRequest();
          }

        }
        this.twenty_percent_latch.countDown();
        this.full_latch.countDown();
      };
      new Thread(thread).start();
    }
  }
  public void twenty_percent_wait(){
    try {
      this.twenty_percent_latch.await();
    }catch (InterruptedException e){
      e.printStackTrace();
    }
  }
  public void wait_complete(){
    try {
      this.full_latch.await();
    }catch (InterruptedException e){
      e.printStackTrace();
    }
  }
  public void add_new_record(long start,String type,long latency,int responseCode){
    rec.add(new Record(start,type,latency,responseCode));
  }

  public Queue<Record> getRecord() {
    return rec;
  }

  public synchronized void incNumRequest(){
    this.numRequest++;
  }
  public synchronized void incFail(){
    this.fail++;
  }
  public int getNumRequest(){
    return this.numRequest;
  }
  public int getTotalFail(){
    return this.fail;
  }
}
