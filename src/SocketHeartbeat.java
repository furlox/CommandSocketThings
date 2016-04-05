import java.util.TimerTask;
import java.util.Timer;
import java.util.Timer.*;

public class SocketHeartbeat extends TimerTask{
	
   public SocketHeartbeat(Object o) {
	   
   }
   // this method performs the task
   public void run() {
   System.out.println("timer working");      
   }    
}