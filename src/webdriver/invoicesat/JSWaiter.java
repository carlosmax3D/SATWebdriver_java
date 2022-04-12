/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdriver.invoicesat;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 *
 * @author Carlos Israel
 */
public class JSWaiter {
 
    private static WebDriver jsWaitDriver;
    private static WebDriverWait jsWait;
    private static JavascriptExecutor jsExec;
 
    public JSWaiter(){
    }

    public JSWaiter(WebDriver driver){
        setDriver(driver);
    }
    
    //Get the driver 
    public static void setDriver (WebDriver driver) {
        jsWaitDriver = driver;
        jsWait = new WebDriverWait(jsWaitDriver, 10);
        jsExec = (JavascriptExecutor) jsWaitDriver;
    }
 
    //Wait for JQuery Load
    public String waitForJQueryLoad() {
        String status = "";
        //Wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = jsWaitDriver -> ((Long) ((JavascriptExecutor) jsWaitDriver)
                .executeScript("return jQuery.active") == 0);
 
        //Get JQuery is Ready
        boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");
 
        //Wait JQuery until it is Ready!
        if(!jqueryReady) {
            //Wait for jQuery to load
            jsWait.until(jQueryLoad);
            status = "JQuery is NOT Ready! | ";
        } else {
            status = "JQuery is Ready!";
        }
        return status;
    }
 
 
    //Wait for Angular Load
    public String waitForAngularLoad() {
        String status = "";
        WebDriverWait wait = new WebDriverWait(jsWaitDriver,15);
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;
 
        String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length === 0";
 
        //Wait for ANGULAR to load
        ExpectedCondition<Boolean> angularLoad = driver -> Boolean.valueOf(((JavascriptExecutor) driver)
                .executeScript(angularReadyScript).toString());
 
        //Get Angular is Ready
        boolean angularReady = Boolean.valueOf(jsExec.executeScript(angularReadyScript).toString());
 
        //Wait ANGULAR until it is Ready!
        if(!angularReady) {
            status = "ANGULAR is NOT Ready!";
            //Wait for Angular to load
            wait.until(angularLoad);
        } else {
            status = "ANGULAR is Ready!";
        }
        return status;
    }
 
    //Wait Until JS Ready
    public String waitUntilJSReady() {
        String status = "";
        WebDriverWait wait = new WebDriverWait(jsWaitDriver,15);
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;
 
        //Wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) jsWaitDriver)
                .executeScript("return document.readyState").toString().equals("complete");
 
        //Get JS is Ready
        boolean jsReady =  (Boolean) jsExec.executeScript("return document.readyState").toString().equals("complete");
 
        //Wait Javascript until it is Ready!
        if(!jsReady) {
            status = "JS in NOT Ready!";
            //Wait for Javascript to load
            wait.until(jsLoad);
        } else {
            status = "JS is Ready!";
        }
        return status;
    }
 
    //Wait Until JQuery and JS Ready
    public String waitUntilJQueryReady() {
        String status = "";
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;
 
        //First check that JQuery is defined on the page. If it is, then wait AJAX
        Boolean jQueryDefined = (Boolean) jsExec.executeScript("return typeof jQuery != 'undefined'");
        if (jQueryDefined == true) {
            //Pre Wait for stability (Optional)
            sleep(20);
 
            //Wait JQuery Load
            status += waitForJQueryLoad();
 
            //Wait JS Load
            status += "|" + waitUntilJSReady();
 
            //Post Wait for stability (Optional)
            sleep(20);
        }  else {
            status = "jQuery is not defined on this site!";
        }
        return status;
    }
 
    //Wait Until Angular and JS Ready
    public String waitUntilAngularReady() {
        String status = "";
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;
 
        //First check that ANGULAR is defined on the page. If it is, then wait ANGULAR
        Boolean angularUnDefined = (Boolean) jsExec.executeScript("return window.angular === undefined");
        if (!angularUnDefined) {
            Boolean angularInjectorUnDefined = (Boolean) jsExec.executeScript("return angular.element(document).injector() === undefined");
            if(!angularInjectorUnDefined) {
                //Pre Wait for stability (Optional)
                sleep(20);
 
                //Wait Angular Load
                status += waitForAngularLoad();
 
                //Wait JS Load
                status += "|" + waitUntilJSReady();
 
                //Post Wait for stability (Optional)
                sleep(20);
            } else {
                status = "Angular injector is not defined on this site!";
            }
        }  else {
            status = "Angular is not defined on this site!";
        }
        return status;
    }
 
    //Wait Until JQuery Angular and JS is ready
    public String waitJQueryAngular() {
        String status = waitUntilJQueryReady();
        status += "|" + waitUntilAngularReady();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, status);
        return status;
    }
 
    public static void sleep (Integer seconds) {
        long secondsLong = (long) seconds;
        try {
            Thread.sleep(secondsLong);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}