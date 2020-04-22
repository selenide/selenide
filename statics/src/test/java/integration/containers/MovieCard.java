package integration.containers;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

@FindBy(css = ".mb-movie")
public class MovieCard extends ElementsContainer {
  @FindBy(css = ".poster")
  public SelenideElement poster;

  @FindBy(css = ".movieTitle")
  public SelenideElement title;

  @FindBy(css = ".tMeterScore")
  public SelenideElement score;
}
