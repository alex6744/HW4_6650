package part2;

public class Record {
    private long start;
    private String requestType;
    private long latency;
    private int responseCode;

  public Record(long start, String requestType, long latency, int responseCode) {
    this.start = start;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }
  @Override
  public String toString(){

    return start+" "+requestType+" "+latency+" "+responseCode;
  }

  public long getStart() {
    return start;
  }

  public String getRequestType() {
    return requestType;
  }

  public long getLatency() {
    return latency;
  }

  public int getResponseCode() {
    return responseCode;
  }
}
