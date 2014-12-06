package com.codeborne.selenide.logevents;

/**
 * Events, created on Selenide actions 
 * like "navigate to url", "click on element", "check a condition" <br/><br/>
 * 
 * An event contains a string representation of the element, the subject and its status.<br/><br/>
 *  
 * Depends on implementation, status can be something like "PASSED", "FAILED" or something else.
 * 
 */
public interface LogEvent {

  
  String getElement();
  String getSubject();
  String getStatus();
}
