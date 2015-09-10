package demo.huge_form;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.*;

public class FastSetValueDemo {
  static URL page = FastSetValueDemo.class.getResource("huge_dynamic_form.html");

  /**
   *  without starting browser:
   *   firefox  -->  slow: 11309, fast: 4766 ms.
   *   chrome  -->  slow: 35070, fast: 5015 ms.
   *   htmlunit  -->  slow: 1935, fast: 1130 ms.
   *   
   * try 2:
   *  firefox  -->  slow: 11890, fast: 4734 ms.
   *  chrome  -->  slow: 42968, fast: 5898 ms.
   *  htmlunit  -->  slow: 1852, fast: 1237 ms.
   *   
   * with starting browser:
   * try 1:
   *  firefox  -->  slow: 14292, fast: 4793 ms.
   *  chrome  -->  slow: 37082, fast: 5340 ms.
   *  htmlunit  -->  slow: 3592, fast: 1354 ms.
   * try 2:
   *  firefox  -->  slow: 14812, fast: 5140 ms.
   *  chrome  -->  slow: 38307, fast: 5251 ms.
   *  htmlunit  -->  slow: 3615, fast: 1412 ms.
   */
  public static void main(String[] args) {
    runExperimentWith(FIREFOX);
    runExperimentWith(CHROME);
    runExperimentWith(HTMLUNIT);
//    runExperimentWith(PHANTOMJS);
//    runExperimentWith(SAFARI);
//    runExperimentWith(OPERA);
  }

  private static void runExperimentWith(String browser) {
    try {
      Configuration.browser = browser;
      Configuration.fastSetValue = false;
      long slow = fill(browser, "Slow");

      Configuration.fastSetValue = true;
      long fast = fill(browser, "Fast");

      System.out.println(browser + "  -->  slow: " + slow + ", fast: " + fast + " ms.");
    }
    finally {
      closeWebDriver();
    }
  }

  private static long fill(String browser, String text) {
    open(page);
    $(By.name("HEADER")).setValue(browser + ", " + text + " 'set value':");

    long start = System.currentTimeMillis();
    for (int i = 0; i < 100; i++) {
      $(By.name("field["+i+"]")).setValue("Переяславль-Приозёрский");
    }
    long end = System.currentTimeMillis();
    return end-start;
  }
}
