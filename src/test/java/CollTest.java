import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class CollTest {


    @Test
    public void ts() {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.timeout = 5000;
        open("https://google.com");
        $("#lst-ib").setValue("docker").submit();
        ElementsCollection coll = $$(".g h3>a");
        coll.forEach(n -> System.out.println(n.getText()));
        System.out.println("----------------------------");
        coll.last(3).forEach(n -> System.out.println(n.getText()));




    }
}
