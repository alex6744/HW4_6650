package part1;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.ResortIDSeasonsBody;
import io.swagger.client.model.ResortSkiers;
import io.swagger.client.model.SeasonsList;
import io.swagger.client.model.SkierVertical;
import java.util.Arrays;
import java.util.List;

public class sample {
  public static void main(String[] args) {
    String url="http://54.184.180.83:8080/HW1_6650";
    String url1="http://localhost:8080/HW1_6650_war_exploded";
    ApiClient a=new ApiClient();
    a.setBasePath(url1);
    SkiersApi apiInstance = new SkiersApi(a);
    LiftRide body = new LiftRide(); // LiftRide | Specify new Season value
    Integer resortID = 56; // Integer | ID of the resort the skier is at
    String seasonID = "seasonID_example"; // String | ID of the ski season
    String dayID = "dayID_example"; // String | ID number of ski day in the ski season
    Integer skierID = 56; // Integer | ID of the skier riding the lift
    try {
      for(int i=0;i<10000;i++)
      apiInstance.writeNewLiftRide(body, resortID, seasonID, dayID, skierID);

    //  Integer result = apiInstance.getSkierDayVertical(resortID, seasonID, dayID, skierID);
     // System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkiersApi#writeNewLiftRide");
      e.printStackTrace();
    }
  }
}
