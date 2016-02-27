package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.TextReport;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;



import static com.codeborne.selenide.Selenide.*;

/**
 * Created by sherwin.angelo on 2/19/2016.
 */
@Listeners(TextReport.class)
public class ReportLogPrintTest {

    @Test
    public void testHotmail(){
        Configuration.browser = "chrome";
        open("https://www.hotmail.com");
    }

    @Test
    public void testGoogle(){
        Configuration.browser = "chrome";
        open("https://www.google.nl");
    }

    @Test
    public void testGmail(){
        Configuration.browser = "chrome";
        open("https://www.gmail.com");
    }

    @Test
    public void testAZ() {
        Configuration.browser = "chrome";
        open("https://www.az.nl");
    }

    @AfterMethod
    public void afterMethod(){
        System.out.println(TextReport.getReportLog());//Prints the report logs after each method

    }




}



