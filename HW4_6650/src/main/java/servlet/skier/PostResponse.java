package servlet.skier;

public class PostResponse {
  private int resortId;
  private String season;
  private String day;
  private int skierId;
  private int time;
  private int liftID;
  private int waitTime;


  public PostResponse(int resortId, String season, String day, int skierId,MyLiftRider liftRider) {
    this.resortId = resortId;
    this.season = season;
    this.day = day;
    this.skierId = skierId;
    this.time=liftRider.getTime();
    this.liftID=liftRider.getLiftID();
    this.waitTime=liftRider.getWaitTime();
  }
  public PostResponse(int resortId, String day, int skierId,MyLiftRider liftRider) {
    this.resortId = resortId;
    this.day = day;
    this.skierId = skierId;
    this.time=liftRider.getTime();
    this.liftID=liftRider.getLiftID();
    this.waitTime=liftRider.getWaitTime();
  }
  public int getResortId() {
    return resortId;
  }

  public String getSeason() {
    return season;
  }

  public String getDay() {
    return day;
  }

  public int getSkierId() {
    return skierId;
  }

  public int getTime() {
    return time;
  }

  public int getLiftID() {
    return liftID;
  }

  public int getWaitTime() {
    return waitTime;
  }

  @Override
  public String toString() {
    return
         resortId +
         ","+ season +
        ","+ day +
        "," + skierId+
             "," + time+
             "," +liftID+
             "," + waitTime
        ;
  }

}
