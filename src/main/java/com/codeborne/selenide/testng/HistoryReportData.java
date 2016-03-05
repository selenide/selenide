package com.codeborne.selenide.testng;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sherwin.angelo on 3/5/2016.
 */
public class HistoryReportData {
    static boolean SaveReportTDATA = false;
    static HashMap MAP = new HashMap<String, List<String>>();

    public static void setDataHistory(String keyValue, String data){
        if(SaveReportTDATA== true) {
            MAP.put(keyValue, data);
        }
    }

    public static void SaveHistoryData(){
        SaveReportTDATA = true;
    }
    public static String getReportData(String testName,String methodName){
        try {
            if (SaveReportTDATA == true) {
                String reportData = MAP.get(testName + " " + methodName).toString();
                //MAP.remove(testName+" "+methodName);
                return reportData;
            } else {
                return "Save report history data is set to false; Turn it on by calling method SaveHistoryData in beforeTest class";
            }
        }catch(Exception e){
            return "Exception "+e+" occurred.\n There is no data available by the requested testName: "+ testName + " and methodName: " + methodName;
        }
    }
}
