package servlet.skier;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyLiftRider {


  private int time;
  private int liftID;
  private int waitTime;

  public int getTime() {
    return time;
  }

  public int getLiftID() {
    return liftID;
  }

  public int getWaitTime() {
    return waitTime;
  }
}
