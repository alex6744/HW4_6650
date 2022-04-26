package part1;

public class Client {
  private int numThread;
  private int numSkiers;
  private int numLifts=40;
  private int numRuns=10;
  private String url;
  private Phase phase1;
  private Phase phase2;
  private Phase phase3;
  private long wallTime;

  public Client(int numThread, int numSkiers, String url) {
    this.numThread = Math.min(numThread,256);
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
  public void start(){
    System.out.println("starting threads: "+getNumThread());

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

    System.out.println("wall time :"+wallTime+" ms");
    int total=phase1.getNumRequest()+ phase2.getNumRequest()+ phase3.getNumRequest();
    System.out.println("total request: "+total);
    int fail= phase1.getTotalFail()+ phase2.getTotalFail()+ phase3.getTotalFail();
    System.out.println("failed: " +fail);
    double thoughtput=1.0*total/(wallTime/1000.0);

    System.out.println("throughtput: " +thoughtput+" request/second");

//    System.out.println("request1: "+phase1.getNumRequest());
//    System.out.println("request2: "+phase2.getNumRequest());
//    System.out.println("request3: "+phase3.getNumRequest());
    //System.out.println("end");
  //  System.out.println();
  }
}
