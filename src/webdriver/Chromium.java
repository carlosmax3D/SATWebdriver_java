/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import webdriver.invoicesat.JSWaiter;

/**
 *
 * @author carlos
 */
public class Chromium {
    private static File file;
    private static WebDriver webDriver;
    private static JSWaiter waiter;
    private int retry = 5;
    private int milis = 1000;

    public Chromium() {
        defaultLoad();
    }
    
    public Chromium(String executable) {
        loadExecutable(executable);
    }    
    
    private void defaultLoad(){
        if (System.getProperty("os.name").toUpperCase().contains("LINUX"))
            loadExecutable("binary"+System.getProperty("file.separator")+"chromedriver");
        else if (System.getProperty("os.name").toUpperCase().contains("MAC"))
            loadExecutable("binary"+System.getProperty("file.separator")+"chromedriverM");
        else
            loadExecutable("binary"+System.getProperty("file.separator")+"chromedriver.exe");        
    }
    private void loadExecutable(String executable){
        file = new File(executable);
        if (!file.exists())
            return;
        System.setProperty("webdriver.chrome.driver",file.getAbsolutePath());
        System.setProperty("webdriver.chrome.args", "--disable-logging");
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--disable-extensions");
//        options.addArguments("test-type");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("no-sandbox");
        options.addArguments("window-size=1920,1080");
        options.addArguments("--headless");//hide browser
        webDriver = new ChromeDriver(options);
        waiter = new JSWaiter(webDriver);
        webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    }
    public static WebDriver getWebDriver(){
        if (webDriver == null){
            Chromium c;
        }
        return webDriver;
    }
    public static void setWebDriver(WebDriver webDriver){
        Chromium.webDriver = webDriver;
    }
    
        private WebElement waitUntilElementAppear(String id, int retries, int miliseconds) throws Exception{
            return waitUntilElementAppear(id, retries, miliseconds, false);
        }
        private WebElement waitUntilElementAppear(String id, int retries, int miliseconds, boolean click) throws Exception{
        WebElement element = null;
        Exception e = null;
        for (int i = 0; i < 2; i++){
            int count = 0;
            while (count < retries && element == null){
                try{
                    element = webDriver.findElement(By.id(id));
                    if(click){
                        WebDriverWait wait = new WebDriverWait(webDriver, 10);
                        WebElement elementW = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
                    }
                }catch(Exception ex){
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Element {0} didn't appear yet", id);
                    Thread.sleep(miliseconds);
                    element = null;
                    e = ex;
                }
                count++;
            }
        }
        if (element == null)
            throw e;
        return element;
    }
    private List<WebElement> waitUntilElementsAppear(String id, int retries, int miliseconds) throws Exception{
        List<WebElement> element = null;
        Exception e = null;
        int count = 0;
        while (count < retries && element == null){
            try{
                element = webDriver.findElements(By.id(id));
            }catch(Exception ex){
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Element {0} didn't appear yet", id);
                Thread.sleep(miliseconds);
                element = null;
                e = ex;
            }
            count++;
        }
        if (element == null)
            throw e;
        element = webDriver.findElements(By.id(id));
        return element;
    }
    private List<WebElement> waitClassNamesAppear(String id, int retries, int miliseconds) throws Exception{
        List<WebElement> element = null;
        Exception e = null;
        int count = 0;
        while (count < retries && element == null){
            try{
                element = webDriver.findElements(By.className(id));
            }catch(Exception ex){
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Element {0} didn't appear yet", id);
                Thread.sleep(miliseconds);
                element = null;
                e = ex;
            }
            count++;
        }
        if (element == null)
            throw e;
        element = webDriver.findElements(By.className(id));
        return element;
    }
    
    public String[] downloadXMLSATFecha(String fecha) throws IOException, InterruptedException, Exception{
        Logger.getLogger(this.getClass().getName()).info("Start search with received invoices");
        List<String> processedFiles = new ArrayList<String>();
        WebElement element;
        try{
            element = waitUntilElementAppear("ctl00_MainContent_RdoTipoBusquedaReceptor",retry,milis,true);
            element.click();
            webDriver.get("https://portalcfdi.facturaelectronica.sat.gob.mx/ConsultaReceptor.aspx");
            waiter.waitJQueryAngular();//sleep(2*1000);
            element = waitUntilElementAppear("ctl00_MainContent_BtnBusqueda",retry,milis);
//            element.click();
            Logger.getLogger(this.getClass().getName()).info("Enter to page. . .");
            waiter.waitJQueryAngular();//sleep(2*1000);
            Logger.getLogger(this.getClass().getName()).info("Find by dates");
            element = waitUntilElementAppear("ctl00_MainContent_RdoFechas",retry,milis,true);
            element.click();
        }catch (Exception e){
            webDriver.get("https://portalcfdi.facturaelectronica.sat.gob.mx/ConsultaReceptor.aspx");
            Logger.getLogger(this.getClass().getName()).info("Enter to page. . .");
            waiter.waitJQueryAngular();//sleep(2*1000);
            Logger.getLogger(this.getClass().getName()).info("Find by dates");
            element = waitUntilElementAppear("ctl00_MainContent_RdoFechas",retry,milis,true);
            element.click();
        }
        waiter.waitJQueryAngular();//sleep(1*1000);
        waiter.waitJQueryAngular();//sleep(1*1000);
        Logger.getLogger(this.getClass().getName()).info("Format date");
        String[] date = fecha.split("-");
        String anio = date[0];
        String mes = date[1];
        for (int i = 0; i < 1; i++){
            waiter.waitJQueryAngular();//sleep(1*1000);
            waiter.waitJQueryAngular();//sleep(1*1000);
            Logger.getLogger(this.getClass().getName()).info("Setting year");
            element = waitUntilElementAppear("DdlAnio",retry,milis);
            Select dropdown = new Select(element);
            dropdown.selectByValue(anio);
            //element.click();
            waiter.waitJQueryAngular();//sleep(1*1000);
            waiter.waitJQueryAngular();//sleep(1*1000);
            waiter.waitJQueryAngular();//sleep(1*1000);
            Logger.getLogger(this.getClass().getName()).info("Setting month");
            element = waitUntilElementAppear("ctl00_MainContent_CldFecha_DdlMes",retry,milis);
            dropdown = new Select(element);
            //mes = Integer.parseInt(mes)+"";
            dropdown.selectByVisibleText(mes);
            //element.click();
            if (date.length > 2){
                String dia = date[2];
                //dia = (i == 0?Integer.parseInt(dia)+"":dia);
                Logger.getLogger(this.getClass().getName()).info("Setting day");
                element = waitUntilElementAppear("ctl00_MainContent_CldFecha_DdlDia",retry,milis);
                // make the input visible:
                waiter.waitJQueryAngular();//sleep(1*1000);
                dropdown = new Select(element);
                dropdown.selectByVisibleText(dia);
                //element.click();
                waiter.waitJQueryAngular();//sleep(1*1000);
                //((JavascriptExecutor)driver).executeScript("asignaDia()");
            }
            waiter.waitJQueryAngular();//sleep(1*1000);
            waiter.waitJQueryAngular();//sleep(1*1000);
            Logger.getLogger(this.getClass().getName()).info("Search now by that parameters");
            element = waitUntilElementAppear("ctl00_MainContent_BtnBusqueda",retry,milis,true);
            element.click();
            waiter.waitJQueryAngular();//sleep(5*1000);
            waiter.waitJQueryAngular();//sleep(5*1000);
            Logger.getLogger(this.getClass().getName()).info("Waiting for results");
            waitUntilElementAppear("ListaFolios",60,1000);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "First {0} results retrieved", waitUntilElementsAppear("BtnDescarga",60, 1000).size());
        }
        //element.submit();
        waiter.waitJQueryAngular();//sleep(3*1000);
        waiter.waitJQueryAngular();//sleep(3*1000);
        List<WebElement> pages = null;
        int pageCount = 1;
        boolean hasPages = false;
/*        try{
            pages = waitClassNamesAppear("pg-selected", retry, milis);
            pages.addAll(waitClassNamesAppear("pg-normal", retry, milis));
            pageCount = pages.size();
            hasPages = true;
        }catch (Exception ex){
            pageCount = pages != null && pages.size() > 0 ? pages.size() : 1;
        }*/
        for (int currentPage = 0; currentPage < pageCount; currentPage++){
            if (hasPages){
                try{
                    pages.get(currentPage).click();                    
                }catch (Exception ex){
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Current or unavailable page");
                }
            }
            waiter.waitJQueryAngular();//sleep(3*1000);
            waiter.waitJQueryAngular();//sleep(3*1000);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Getting all XMLs of invoices page {0}", (currentPage+1));
            List<WebElement> elements = waitUntilElementsAppear("BtnDescarga",retry, milis);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Quantity of XML {0}", elements.size());
            Logger.getLogger(this.getClass().getName()).info("Creating provitional names");
            List<String> names = new ArrayList<String>();
            for (WebElement elementt : elements){
                //RecuperaCfdi.aspx?Datos=
                Logger.getLogger(this.getClass().getName()).info("Getting and creating link of the file");
                String var = elementt.getAttribute("onclick");
                var = var.replace("return AccionCfdi('", "").replace("','Recuperacion');", "");
                var = "https://portalcfdi.facturaelectronica.sat.gob.mx/" + var;
                //driver.get(var);
                Date d = new Date();
                names.add(d.getTime()+"");
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Download file by link {0}", var);
                processedFiles.add(httpDownload(webDriver, var, d.getTime()+".xml","FACTURAS"+System.getProperty("file.separator")+fecha+System.getProperty("file.separator")));
                //elementt.click();
                //sleep(1*1000);
            }
            Logger.getLogger(this.getClass().getName()).info("Getting PDF's of invoices");
            waiter.waitJQueryAngular();//sleep(5*1000);
            elements = waitUntilElementsAppear("BtnRI",retry, milis);
            int i = 0;
            for (WebElement elementt : elements){
                //RepresentacionImpresa.aspx?Datos=
                Logger.getLogger(this.getClass().getName()).info("Getting and creating link of the file");
                String var = elementt.getAttribute("onclick");
                var = var.replace("recuperaRepresentacionImpresa('","").replace("');", "");
                var = "https://portalcfdi.facturaelectronica.sat.gob.mx/RepresentacionImpresa.aspx?Datos=" + var;
                //driver.get(var);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Download file by link {0}", var);
                processedFiles.add(httpDownload(webDriver, var, names.get(i)+".pdf","FACTURAS"+System.getProperty("file.separator")+fecha+System.getProperty("file.separator")));
                i++;
                //elementt.click();
                //sleep(1*1000);
            }
        }
        webDriver.get("https://portalcfdi.facturaelectronica.sat.gob.mx");
        waiter.waitJQueryAngular();//sleep(5*1000);
        String filesProcessed[] = new String[processedFiles.size()];
        processedFiles.toArray(filesProcessed);
        return filesProcessed;
    }
    
    public void loginSiteSAT(String cert, String pk, String secret) throws InterruptedException, Exception{
        WebElement element= null;
        boolean flag = false;
        int retries = 6;
        do{
            Logger.getLogger(this.getClass().getName()).info("Connecting with SAT site https://portalcfdi.facturaelectronica.sat.gob.mx");
            webDriver.get("https://portalcfdi.facturaelectronica.sat.gob.mx");
            // Find the text input element by its name
            Logger.getLogger(this.getClass().getName()).info("Waiting for page loading");
            waiter.waitJQueryAngular();//sleep(5*1000);
            Logger.getLogger(this.getClass().getName()).info("Getting element of FIEL's login");
            element = waitUntilElementAppear("buttonFiel",retry,milis);
            flag = false;
        }while (flag && retries > 0);
        if (flag)
            throw new Exception("Unable to connect to site");
        element.click();
        waiter.waitJQueryAngular();//sleep(5*1000);
        // Enter something to search for
        element = waitUntilElementAppear("fileCertificate",retry,milis);
        // make the input visible:
        ((JavascriptExecutor)webDriver).executeScript("arguments[0].style = \"\"; arguments[0].style.display = \"block\"; arguments[0].style.visibility = \"visible\";", element);
        // send file:
        element.sendKeys(cert);
        Logger.getLogger(this.getClass().getName()).info("Putting certificate");
        // Enter something to search for
        element = waitUntilElementAppear("filePrivateKey",retry,milis);
        // make the input visible:
        ((JavascriptExecutor)webDriver).executeScript("arguments[0].style = \"\"; arguments[0].style.display = \"block\"; arguments[0].style.visibility = \"visible\";", element);
        // send file:
        element.sendKeys(pk);
        Logger.getLogger(this.getClass().getName()).info("Putting key");
        element = webDriver.findElement(By.name("privateKeyPassword"));
        element.sendKeys(secret);
        Logger.getLogger(this.getClass().getName()).info("Putting private key");
        element = webDriver.findElement(By.name("submit"));
        // Now submit the form. WebDriver will find the form for us from the element
        element.click();
        Logger.getLogger(this.getClass().getName()).info("Login, wait");
        waiter.waitJQueryAngular();//sleep(2*1000);
    }
    
    public void logoutSiteSAT(){
        WebElement element= null;
        Logger.getLogger(this.getClass().getName()).info("Logging out");
        try{
            element = waitUntilElementAppear("anchorClose",retry,milis);
            element.click();
            waiter.waitJQueryAngular();//sleep(2*1000);
        }catch (Exception ex){
        }
        webDriver.get("https://portalcfdi.facturaelectronica.sat.gob.mx/logout.aspx?salir=y");
        waiter.waitJQueryAngular();//sleep(2*1000);
    }
    
    public String httpDownload(WebDriver driver,String var,String extension,String path) throws MalformedURLException, IOException{
        HttpURLConnection httpURLConnection = null;
        Logger.getLogger(this.getClass().getName()).info("Getting cookies from driver");
        Set<Cookie> cookies = driver.manage().getCookies();
        String cookieString = "";
        cookieString = cookies.stream().map((cookie) -> cookie.getName() + "=" + cookie.getValue() + ";").reduce(cookieString, String::concat);
        Logger.getLogger(this.getClass().getName()).info("Creating new connection");
        httpURLConnection = (HttpURLConnection) (new URL(var)).openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.addRequestProperty("Cookie", cookieString);
        Logger.getLogger(this.getClass().getName()).info("Setting cookies to conection");
        Logger.getLogger(this.getClass().getName()).info("Getting filename");
        String raw = httpURLConnection.getHeaderField("Content-Disposition");
        // raw = "attachment; filename=abc.jpg"
        if(raw != null && raw.contains("=")) {
            cookieString = raw.split("=")[1]; //getting value after '='
        } else {
            // fall back to random generated file name?
            cookieString = extension;
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating path and filename {0} {1}", new Object[]{path, cookieString});
        Files.createDirectories(Paths.get(path));
        Logger.getLogger(this.getClass().getName()).info("Saving file. . .");
        try (InputStream in = httpURLConnection.getInputStream()) {
            Files.copy(in, new File(path+cookieString).toPath(),
            StandardCopyOption.REPLACE_EXISTING);
        }
        return cookieString;
    }
    
    public static String[] downloadInvoicesDate(String cert, String privateKey, String pass, String date){
        Chromium chromium = new Chromium();
        String[] files = null;
        try{
            Logger.getLogger(Webdriver.class.getName()).info("Try to connect to SAT");
            chromium.loginSiteSAT(cert, privateKey, pass);
            Logger.getLogger(Webdriver.class.getName()).log(Level.INFO, "Entering to download site with date {0}", date);
            files = chromium.downloadXMLSATFecha(date);
            chromium.logoutSiteSAT();
        }catch(Exception ex){
            Logger.getLogger(Webdriver.class.getName()).log(Level.SEVERE,ex.toString());
            try{
                chromium.logoutSiteSAT();
            }catch(Exception e){}
        }
        Chromium.getWebDriver().close();
        Chromium.getWebDriver().quit();
        Chromium.setWebDriver(null);
        return files;
    }
    public static String[] downloadInvoicesYear(String cert, String privateKey, String pass, String year){
        Chromium chromium = new Chromium();
        List<String> files = new ArrayList<String>();
        try{
            Logger.getLogger(Webdriver.class.getName()).info("Try to connect to SAT");
            chromium.loginSiteSAT(cert, privateKey, pass);
            for (int i = 1; i < 13; i++){
                String dateMonth = year + "-" + String.format("%02d", i);
                Logger.getLogger(Webdriver.class.getName()).log(Level.INFO, "Entering to download site with date {0}", dateMonth);
                files.addAll(Arrays.asList(chromium.downloadXMLSATFecha(dateMonth)));
            }
            chromium.logoutSiteSAT();
        }catch(Exception ex){
            Logger.getLogger(Webdriver.class.getName()).log(Level.SEVERE,ex.toString());
            try{
                chromium.logoutSiteSAT();
            }catch(Exception e){}
        }
        Chromium.getWebDriver().close();
        Chromium.getWebDriver().quit();
        Chromium.setWebDriver(null);
        String response[] = new String[files.size()];
        files.toArray(response);
        return response;
    }
}
