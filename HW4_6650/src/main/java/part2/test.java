package part2;


import java.io.FileNotFoundException;

public class test {
  public static void main(String[] args) throws InterruptedException, FileNotFoundException {
    String host=args[0];
    int numthread=Integer.parseInt(args[1]);
    String url="http://"+host+":8080/HW1_6650_war";
   // String url="http://localhost:8080/HW1_6650_war_exploded";
    Client_part2 c=new Client_part2(32,20000,url);
    //c.start();

    Client_part2 c1=new Client_part2(64,20000,url);
    //c1.start();

    Client_part2 c3=new Client_part2(512,20000,url);
   // c3.start();
   // Thread.sleep(1000);
    Client_part2 c2=new Client_part2(numthread,20000,url);
    c2.start();
  }
}
