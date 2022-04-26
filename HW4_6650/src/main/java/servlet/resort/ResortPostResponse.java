package servlet.resort;

public class ResortPostResponse {
  private int resortId;
  private int year;
  public ResortPostResponse(int resortId,int year) {
    this.resortId = resortId;
    this.year=year;
  }

  @Override
  public String toString() {
    return
        resortId +
            ","+ year

        ;
  }
}
