package integration;

import com.codeborne.selenide.impl.SelenideLogger;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LogEventTest extends IntegrationTest {
  
  
  static List<LogEvent> logEvents = new ArrayList<LogEvent>();
  static LogEventListener logEventListener = new LogEventListener() {
    @Override
    public void onEvent(LogEvent currentLog) {
      logEvents.add(currentLog);
//      System.out.println( "{" + currentLog.getElement() + "} " +
//          currentLog.getSubject() + ": " + currentLog.getStatus()
//      );
    }
  };
  
  class PrettyReportCreator extends TestWatcher {
    @Override
    protected void starting(Description description) {
      super.starting(description);
    }
    
    @Override
    protected void finished(Description description) {
      super.finished(description);
      
      System.out.println();
      System.out.println();
      System.out.println();
      System.out.println("Pretty report for " + description.getDisplayName());
      
      String hLine = "+--------------------+----------------------------------------------------------------------+----------+";
      
      System.out.println(hLine);
      
      System.out.format("|%-20s|%-70s|%-10s|\n", "Element", "Subject", "Status");
      System.out.println(hLine);
      for (LogEvent e : logEvents) {
        
        System.out.format("|%-20s|%-70s|%-10s|\n", e.getElement(),
              e.getSubject(), e.getStatus());
      }
      System.out.println(hLine);
      System.out.println();
      System.out.println();
      System.out.println();
    }
  }
  
  @Rule
  public TestRule prettyReportCreator = new PrettyReportCreator();
  
  @BeforeClass
  public static void setUp() throws Exception {
    runLocalHttpServer();
    SelenideLogger.addListener(logEventListener);
  }
  
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }
  
  
  @Test
  public void shouldDoSomeChecksAndActions() {
    $(By.name("username")).shouldBe(readonly);
    $(By.name("password")).shouldNotBe(readonly).should(be(empty));
    $(By.name("password")).setValue("123");
    $("#login").click();
  }
  

}
