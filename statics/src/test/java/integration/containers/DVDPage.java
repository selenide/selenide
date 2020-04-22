package integration.containers;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class DVDPage {
    @FindBy(css = "#show-more-btn > button")
    public SelenideElement showMoreBtn;

    @FindBy(css = ".mb-movie")
    List<MovieCard> movieCards;

    public DVDPage clickShowMore() {
        showMoreBtn.click();
        System.out.println("What I clicked: " + showMoreBtn);
        return this;
    }
}
