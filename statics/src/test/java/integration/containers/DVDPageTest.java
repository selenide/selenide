package integration.containers;

import com.codeborne.selenide.junit.TextReport;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.sleep;
import static org.junit.Assert.assertEquals;

/**
 * Inspired by https://github.com/ansonliao/Selenium-RottenTomatoes
 */
public class DVDPageTest {
  @Rule
  public TextReport report = new TextReport();

  @Before
  public void setUp() {
    open("https://www.rottentomatoes.com/browse/top-dvd-streaming/");
  }

  @Test
  public void f1() throws InterruptedException {
    sleep(1000);
    DVDPage page = page(DVDPage.class);
    assertEquals(32, page.movieCards.size());

    page.clickShowMore();
    TimeUnit.SECONDS.sleep(1);
    assertEquals(64, page.movieCards.size());

    page.clickShowMore();
    TimeUnit.SECONDS.sleep(1);
    assertEquals(65, page.movieCards.size());
  }
}
