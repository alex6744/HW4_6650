package servlet.skier;

import java.util.ArrayList;
import java.util.List;

public class Verticals {
  private List<Vertical> resorts;

  public Verticals() {
    resorts=new ArrayList<>();
  }
  public void add(Vertical vertical){
    resorts.add(vertical);
  }
}
