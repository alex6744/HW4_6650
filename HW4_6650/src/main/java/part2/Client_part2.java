package part2;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Client_part2 {
  private int numThread;
  private int numSkiers;
  private int numLifts=40;
  private int numRuns=10;
  private String url;
  private Phase phase1;
  private Phase phase2;
  private Phase phase3;
  private long wallTime;
  private List<Record> recordList=new LinkedList<>();
  private int total=0;
  private int max=Integer.MIN_VALUE;
  private int min=Integer.MAX_VALUE;
  public Client_part2(int numThread, int numSkiers, String url) {
    this.numThread = Math.min(numThread,512);
    this.numSkiers = Math.min(numSkiers,100000);

    this.url = url;

    this.phase1=new Phase(this.numThread/4,(int)Math.ceil(1.0*numRuns*2/10),this.numSkiers,numLifts,url,0,90,1);
    this.phase2=new Phase(this.numThread,(int)Math.ceil(1.0*numRuns*6/10),this.numSkiers,numLifts,url,91,360,2);
    this.phase3=new Phase((int)Math.ceil(this.numThread*1.0/4),(int)Math.ceil(numRuns*1/10),this.numSkiers,numLifts,url,361,420,3);
  }

  public Phase getPhase1() {
    return phase1;
  }

  public Phase getPhase2() {
    return phase2;
  }

  public Phase getPhase3() {
    return phase3;
  }

  public int getNumThread() {
    return numThread;
  }
  public void start() throws FileNotFoundException {
    System.out.println("start threads: "+getNumThread());

    long beign=System.currentTimeMillis();
    phase1.run();
    phase1.twenty_percent_wait();
    phase2.run();
    phase2.twenty_percent_wait();
    phase3.run();
    phase1.wait_complete();
    phase2.wait_complete();
    phase3.wait_complete();
    long end=System.currentTimeMillis();
    this.wallTime=end-beign;



    System.out.println("------stat------");
    Queue<Record> q1=getPhase1().getRecord();
    Queue<Record> q2=getPhase2().getRecord();
    Queue<Record> q3=getPhase3().getRecord();
   // System.out.println(q1.size());
    //System.out.println(q2.size());
   // System.out.println(q3.size());
    try {
      FileWriter out=new FileWriter(new File("./test1.csv"));
      CSVWriter w=new CSVWriter(out);
      int count=0;
      while (!q1.isEmpty()){
        Record r= q1.poll();
        String[] s=new String[]{r.getStart()+"",r.getRequestType(),r.getLatency()+"",r.getResponseCode()+""};
        w.writeNext(s);
        count++;
      }
      while (!q2.isEmpty()){
        Record r= q2.poll();
        String[] s=new String[]{r.getStart()+"",r.getRequestType(),r.getLatency()+"",r.getResponseCode()+""};
        w.writeNext(s);
        count++;
      }
      while (!q3.isEmpty()){
        Record r= q3.poll();
        String[] s=new String[]{r.getStart()+"",r.getRequestType(),r.getLatency()+"",r.getResponseCode()+""};
        w.writeNext(s);
        count++;
      }

      //System.out.println( count);
      w.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    total=phase1.getNumRequest()+ phase2.getNumRequest()+ phase3.getNumRequest();
    System.out.println("Mean response time(ms): "+avg());
    System.out.println("Median response time(ms): "+median());

    double thoughtput=1.0*total/(wallTime/1000.0);
    System.out.println("Throughput(request/s): "+thoughtput);
    System.out.println("p99 response time(ms): "+p99());
    findMaxAndMin();
    System.out.println("Max response time(ms): "+this.max);
    System.out.println("Min response time(ms): "+this.min);
    System.out.println("Wall time: "+wallTime);
  }
  public void findMaxAndMin(){

    try(CSVReader r=new CSVReader(new FileReader("./test1.csv"))) {
      String[] line;
      while ((line=r.readNext())!=null){
        int time=Integer.parseInt(line[2]);
        this.max=Math.max(max,time);
        if(time!=0) {
          this.min = Math.min(min, time);
        }

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public int p99(){
    int count=0;
    int per99=(int)(1.0*total*99/100);
    try(CSVReader r=new CSVReader(new FileReader("./test1.csv"))) {
      String[] line;
      while ((line=r.readNext())!=null){

        count++;
        if(count==per99){
          return Integer.parseInt(line[2]);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }
  public double avg() throws FileNotFoundException {
    int count=0;
    int sum=0;
    try(CSVReader r=new CSVReader(new FileReader("./test1.csv"))) {
      String[] line;
      while ((line=r.readNext())!=null){
        sum+=Integer.parseInt(line[2]);
        count++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 1.0*sum/count;
  }
  public int median() throws FileNotFoundException {
    if(this.total%2==0){
      int left=total/2; int right=total/2+1;
      int leftN=0; int rightN=0;
      int count=0;
      try(CSVReader r=new CSVReader(new FileReader("./test1.csv"))) {
        String[] line;
        while ((line=r.readNext())!=null){

          count++;
          if(count==left){
              leftN=Integer.parseInt(line[2]);
          }
          if(count==right){
            rightN=Integer.parseInt(line[2]);
            break;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return (leftN+rightN)/2;
    }else{
      int count=0;
      try(CSVReader r=new CSVReader(new FileReader("./test1.csv"))) {
        String[] line;
        while ((line=r.readNext())!=null){

          count++;
          if(count==(total/2+1)){
              return Integer.parseInt(line[2]);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return 0;
    }


  }

}
