package integration.testng;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.HistoryReportData;
import com.codeborne.selenide.testng.TextReport;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;


import static com.codeborne.selenide.Selenide.*;
/**
 * Created by sherwin.angelo on 2/19/2016.
 */
@Listeners(TextReport.class)
public class ReportLogPrintTest {

    @BeforeTest
    public void setValue(){
        HistoryReportData.SaveHistoryData();
    }
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
    public void afterMethod(ITestResult _result,ITestContext context){
        System.out.println("report from hashmap: "+HistoryReportData.getReportData("x",_result.getMethod().getMethodName()));//Prints the report logs after each method

       System.out.println("report from hashmap: "+HistoryReportData.getReportData(context.getCurrentXmlTest().getName(),_result.getMethod().getMethodName()));//Prints the report logs after each method


    }




}



