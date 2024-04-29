package integration;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Serhii Bryt
 * 04.04.2024 18:57
 **/
public class MyTask extends TimerTask {
  @Override
  public void run() {
    System.out.println("Smth");
  }

  @Override
  public boolean cancel() {
    System.out.println("Cancel");
    return super.cancel();
  }

  public static void main(String[] args) throws InterruptedException {
    Timer timer = new Timer();

    MyTask task = new MyTask();
    timer.schedule(task, 0, 1000);
    Thread.sleep(6000);
    task.cancel();
    timer.cancel();
  }
}
