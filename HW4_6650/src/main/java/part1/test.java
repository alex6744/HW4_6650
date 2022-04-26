package part1;

public class test {
  public static void main(String[] args) throws InterruptedException {
    String url="http://54.184.180.83:8080/HW1_6650";
    String url1="http://localhost:8080/HW1_6650_war_exploded";
    Client c=new Client(32,20000,url);
    //c.start();
   // Thread.sleep(1000);
    Client c1=new Client(64,20000,url);
   // c1.start();
   // Thread.sleep(1000);
    Client c3=new Client(128,20000,url);
   // c3.start();
   // Thread.sleep(1000);
    Client c2=new Client(256,20000,url);
    c2.start();
  }
}
