package utils;

import static org.hamcrest.MatcherAssert.assertThat;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class TU extends TestBase{
	
	WebDriverWait wew;
	static boolean isExecuted = false, ecieStatus, enveStatus, toeStatus, nseeStatus, sereStatus, displayStatus, enabledStatus, returnVal;
	long startTime;
	String str, returnStr, browserLoadStatus;
	Select sElement;
	Calendar cal;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    public List<String> browserTabs;
    private Actions actions;
    ChromeOptions chromeOptions;
    public static InputStream is;
    
	public static Properties prop=null;
	public static FileInputStream fis = null;
	public static String CurrentDir =System.getProperty("user.dir");
	public static String path = "/BDDFramework.properties";
	

	//*************************Docupace*************************//
	
	public static String docupacewind=null;
	public static String DocupaceWindow=null;
	
	public static String Pdfdoc="/src/test/resources/Externalfiles/TestUpload.pdf";

	public static String Pdfdoc1="src\\test\\resources\\Externalfiles\\TestUpload.pdf";
	
	public static String documentID=null;
	  public static String clientmanagewind=null;
	
	//******************************************************//
	
	
    public TU() {
    	try{
			
			prop= new Properties();
			fis = new FileInputStream(CurrentDir+path);
			prop.load(fis);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	public void waitForPageLoad() throws InterruptedException{
    	WebDriverWait wait = new WebDriverWait(driver, 60);
    	wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver wdriver) {
                return ((JavascriptExecutor) driver).executeScript(
                    "return document.readyState"
                ).equals("complete");
            }
        });
    	do{
    		Thread.sleep(1000);
    		browserLoadStatus = (String) ((JavascriptExecutor) driver).executeScript("return document.readyState");
    		System.out.println("browserLoadStatus: "+browserLoadStatus);
    	}while(!browserLoadStatus.equals("complete"));
    }
	
	public void killAllDriverServers(){
		
		try {
			Runtime.getRuntime().exec("taskkill /f /im chromedriver75.exe");
			Runtime.getRuntime().exec("taskkill /f /im chromedriver77.exe");
			TimeUnit.SECONDS.sleep(4);
        }catch(Exception e) {
        	System.out.println("No cmd.exe was present in the Task Manager. ");
        	e.printStackTrace();
        }
	}
	
	public static void phantomClick(WebDriver driver, WebElement element){
        final String script = "function ghostclick(el){var ev = document.createEvent(\"MouseEvent\");ev.initMouseEvent(\"click\",true ,true,window,null,0,0,0,0,false,false,false,false,0,null);el.dispatchEvent(ev);} return ghostclick(arguments[0])";
        ((JavascriptExecutor) driver).executeScript(script, element);
    }
	
	public String webAppObject(WebDriver driver, WebElement element, String fn, String value, int objWaitTime, String pageName, String elementName, int criticality) throws InterruptedException {
		waitForPageLoad();
		wew = new WebDriverWait(driver, 60);
		cal = Calendar.getInstance();
		str = "["+sdf.format(cal.getTime())+"]"+"[Page: "+pageName+"]: [WebElement: "+elementName+"]"; returnStr = "false";
		
		startTime = System.currentTimeMillis(); //fetch starting time
		displayStatus = false; enabledStatus = false;
		do{
	    	try{
	    		wew.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)));
	    		displayStatus = element.isDisplayed();
	    		enabledStatus = element.isEnabled();
	    		ecieStatus = false;
	    		returnStr = "";
	    	}catch(ElementClickInterceptedException ecie){
	    		ecieStatus = true;
	    		returnStr = "Element Click Intercepted Exception";
	    	}catch(ElementNotVisibleException enve){
	    		enveStatus = true;
	    		returnStr = "Element Not Visible Exception";
	    	}catch(TimeoutException toe){
	    		toeStatus = true;
	    		displayStatus = false;
	    		enabledStatus = false;
	    		returnStr = "TimeoutException for : "+str;
	    	}catch(NoSuchElementException nsee){
				nseeStatus = true;
	    		displayStatus = false;
				enabledStatus = false;
				returnStr = "NoSuchElementException for : "+str;
			}catch(StaleElementReferenceException sere){
				sereStatus = true;
				displayStatus = true;
				returnStr = "StaleElementReferenceException for : "+str;
			}
	    	Thread.sleep(1000);
	    }while((ecieStatus || enveStatus || toeStatus || nseeStatus || sereStatus || !displayStatus || !enabledStatus) && (((System.currentTimeMillis()-startTime) <= (objWaitTime*1000))));
		System.out.println("["+sdf.format(cal.getTime())+"]"+"["+elementName+": ecieStatus="+ecieStatus+", enveStatus="+enveStatus+", toeStatus="+toeStatus+", nseeStatus="+nseeStatus+", sereStatus="+sereStatus+", !displayStatus="+!displayStatus+", !enabledStatus="+!enabledStatus+"]");
		if(returnStr.contains("Exception") && criticality==1){
			returnStr = returnStr + ", displayStatus="+displayStatus+", Criticality="+criticality;
			return returnStr;
		}

		
		startTime = System.currentTimeMillis(); //fetch starting time
		
		if(displayStatus && enabledStatus){
//			System.out.println(str+": [Display Status: "+displayStatus+"], [Enabled Status: "+enabledStatus+"]. ");
			switch(fn){
			case "verify":
				returnStr = "true";
				System.out.println(str+": [Display Status: "+displayStatus+"], [Enabled Status: "+enabledStatus+"]. ");
				break;
			case "click": 
				element.click();
//				actions = new Actions(driver);
//				
//				do{
//			    	try{
//			    		if(prop.getProperty("webdriver.driver").equals("iexplorer")) {
//			    			System.out.println("Inside actions. ");
//			    			actions.click(element).build().perform();
//			    		}else {
//			    			element.click();
//			    		}
//			    		
//			    		ecieStatus = false;
//			    	}catch(ElementClickInterceptedException ecie){
//			    		ecieStatus = true;
//			    		Thread.sleep(1000);
//			    		System.out.println("["+sdf.format(cal.getTime())+"]"+elementName+": Element Click Intercepted Exception");
//			    	}
//		    	}while((ecieStatus) && (((System.currentTimeMillis()-startTime) <= (objWaitTime*1000))));
				System.out.println(str+": Element clicked. ");
				returnStr = "true";
				break;
			case "doubleClick": 
				actions = new Actions(driver);
				actions.doubleClick(element).perform();
				returnStr = "true";
				break;
			case "clear": 
				element.clear();
				System.out.println(str+": Element cleared. ");
				returnStr = "true";
				break;
			case "sendKeys": 
				element.sendKeys(value);
//				actions = new Actions(driver);
//				actions.sendKeys(element, value).build().perform();
				System.out.println("Entered value: "+value+" in "+str);
				returnStr = "true";
				break;
			case "sendKeysWithDelay":
				element.clear();
				for(int i=0;i<value.length();i++){
					Thread.sleep(objWaitTime*1000);
					element.sendKeys(Keys.HOME+Character.toString(value.charAt(i)));
				}
				System.out.println("Entered value: "+value+" in "+str);
				returnStr = "true";
				break;
			case "selectByVisibleText":
				sElement = new Select(element);
				sElement.selectByVisibleText(value);
				System.out.println("Selected visible text: "+value+" in "+str);
				returnStr = "true";
				break;
			case "selectByValue":
				sElement = new Select(element);
				sElement.selectByValue(value);
				System.out.println("Selected value: "+value+" in "+str);
				returnStr = "true";
				break;
			case "selectByIndex":
				sElement = new Select(element);
				sElement.selectByIndex(Integer.valueOf(value));
				System.out.println("Selected index: "+value+" in "+str);
				returnStr = "true";
				break;
			case "getFirstSelectedOption-GetText":
				sElement = new Select(element);
				returnStr = sElement.getFirstSelectedOption().getText();
				System.out.println("First Selected value Text: "+returnStr);
				break;
			case "getFirstSelectedOption-GetValue":
				sElement = new Select(element);
				returnStr = sElement.getFirstSelectedOption().getAttribute("value");
				System.out.println("First Selected value: "+returnStr);
				break;
			case "getText":
				returnStr = element.getText();
				System.out.println(str+": Element text: "+element.getText());
				break;
			case "check":
				System.out.println("element.isSelected(): "+element.isSelected());
				if(!rBtnSelected(element)){ //if(!element.isSelected()){
					element.click();
					System.out.println(str+": Checked element. ");
				}else if(rBtnSelected(element)){
					System.out.println(str+": Element is already checked. ");
				}
				returnStr = "true";
				break;
			case "uncheck":
				if(rBtnSelected(element)){ //if(element.isSelected()){
					element.click();
					System.out.println(str+": Unchecked element. ");
				}else if(!rBtnSelected(element)){
					System.out.println(str+": Element is already unchecked. ");
				}
				returnStr = "true";
				break;
			case "getAttribute":
				returnStr = element.getAttribute(value);
				System.out.println(str+": Element Attribute: "+value+" = "+returnStr);
				break;
			default:
				System.out.println(str+": [Display Status: "+displayStatus+"], [Enabled Status: "+enabledStatus+"]. Interaction to be performed on the object is not specified properly in the code. ");
				break;
			}
		}
		else{
			System.out.println(str+": [Display Status: "+displayStatus+"], [Enabled Status: "+enabledStatus+"]. ");
			returnStr = "false";
		}
		
		return returnStr;
	}
	
	public boolean rBtnSelected(WebElement element){
		if(element.getAttribute("class").contains("radioUnchecked") || (!element.isSelected())){
			returnVal = false;
		}else if(element.getAttribute("class").contains("radioChecked") || (element.isSelected())){
			returnVal = true;
		}
		return returnVal;
	}
    
    public String validateSite(String site, String title, String link, String tab) throws InterruptedException{
    	browserTabs = new ArrayList<String>();
    	browserTabs.addAll(driver.getWindowHandles());
    	System.out.println("browserTabs: "+browserTabs);
    	if(browserTabs.size()>=2){ // && tab.equals("new")
    		if(driver.getTitle().equals("Client Management Home")){
    			driver.switchTo().window(browserTabs.get(1));
    		}else if(driver.getTitle().equals("Login")){
    			return "Login";
//    			driver.switchTo().window(browserTabs.get(browserTabs.size()-1));
    		}else{
    			driver.switchTo().window(browserTabs.get(browserTabs.size()-1));
    		}
        	waitForPageLoad();
        	Thread.sleep(4000);
        	System.out.println("Title: "+driver.getTitle());
        	assertThat("Title does NOT match. Actual Title: "+driver.getTitle(), driver.getTitle().contains(title));
        	assertThat("website URL does NOT match. Expected URL: "+site+". Actual URL: "+driver.getCurrentUrl(), (driver.getCurrentUrl().contains(site) || site.contains(driver.getCurrentUrl())));
    	}
    	browserTabs = null;
    	return driver.getTitle();
    }
    
    
    public void tabAction(String action, String tab) {
    	if(action.equals("close")){
        	browserTabs = new ArrayList<String>();
        	browserTabs.addAll(driver.getWindowHandles());
        	driver.switchTo().window(browserTabs.get(1));
    		driver.close();
    		driver.switchTo().window(browserTabs.get(0));
    	}else if(action.equals("navigate") && tab.equals("back")){
    		driver.navigate().back();
    	}
    }
	

    public void switchToTab(int tabIndex) throws InterruptedException{
    	Thread.sleep(4000);
    	browserTabs = null;
    	browserTabs = new ArrayList<String>();
    	browserTabs.addAll(driver.getWindowHandles());
    	
    	driver.switchTo().window(browserTabs.get(tabIndex));
    	Thread.sleep(3000);
    }
    
    public void waitTillTitle(String tabTitle, int titleWaitTime) throws InterruptedException{
    	startTime = System.currentTimeMillis(); //fetch starting time
		do{
			if(driver.getTitle().equals(tabTitle)){
				break;
			}
    	}while((System.currentTimeMillis()-startTime) <= (titleWaitTime*1000));
    }
    
    public void navigateToURL(String url, String tab) throws InterruptedException {
    	if(tab.equals("new")){
    		((JavascriptExecutor) driver).executeScript("window.open()");
        	driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
        	switchToTab(1);
        	driver.get(url);
    	}else if(tab.equals("same")){
    		driver.get(url);
    	}
    	waitForPageLoad();
    	driver.manage().window().maximize();
    }
    
    // ****************************************************************
    // This Method creates an object for properties file, loads it
    // and reads the contents from properties file
    // ****************************************************************
 	public void readPropertyFile() throws IOException {
 		prop = new Properties();
 		is = new FileInputStream("serenity.properties");
 		prop.load(is);
 		System.out.println("Path loaded. ");
 	}
	
	// This Method handles switch between multiple windows
	public void window_navigate() {
		Set<String> allwin = driver.getWindowHandles();
		for (String testwin : allwin)
			driver.switchTo().window(testwin);
	}
	
	// This method implements implicit waits
	public void implicitwait(int timeout) {
		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
	}
	
	public void clearCache(WebDriver driver) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C://WebDrivers/chromedriver.exe");
		chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("disable-infobars");
		chromeOptions.addArguments("start-maximized");
		driver = new ChromeDriver(chromeOptions);
		driver.get("chrome://settings/clearBrowserData");
		Thread.sleep(5000);
		driver.switchTo().activeElement();
		driver.findElement(By.cssSelector("* /deep/ #clearBrowsingDataConfirm")).click();
		Thread.sleep(5000);
	}

	/* This will launch browser with cache disabled */
	public void launchWithoutCache() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C://WebDrivers/chromedriver.exe");
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability("applicationCacheEnabled", false);
//		driver = new ChromeDriver(cap);
	}
	
	public void ajaxWait(WebDriver driver, long seconds) {
	    try{
	        while(!waitForJSandJQueryToLoad(driver)) {
	            try {
	                Thread.sleep(1000);
	                seconds -= 1;
	                if (seconds <= 0) {
	                    return;
	                }
	            } catch (InterruptedException var4) {
	                var4.printStackTrace();
	            }
	        }
	    } catch(Exception e){
	        e.printStackTrace();
	    }
	}

	public boolean waitForJSandJQueryToLoad(WebDriver driver) {

	    WebDriverWait wait = new WebDriverWait(driver, 3);

	    // wait for jQuery to load
	    ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
	        @Override
	        public Boolean apply(WebDriver driver) {
	            try {
	                return ((Long)((JavascriptExecutor)driver).executeScript("return jQuery.active") == 0);
	            }
	            catch (Exception e) {
	                // no jQuery present
	                e.printStackTrace();
	                return true;
	            }
	        }
	    };

	    // wait for Javascript to load
	    ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
	        @Override
	        public Boolean apply(WebDriver driver) {
	            return ((JavascriptExecutor)driver).executeScript("return document.readyState")
	                    .toString().equals("complete");
	        }
	    };

	    return wait.until(jQueryLoad) && wait.until(jsLoad);
	}
	
	public void navigation_to_window() throws Throwable{
	   Set <String> st= driver.getWindowHandles();
       for(String windowhandle:st)
       driver.switchTo().window(windowhandle);	 
	}
	public void explicit_wait_elementvisibility(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver,20);
		wait.until(ExpectedConditions.visibilityOf(element));

	}
	public void explicit_wait_title(String title) {
		WebDriverWait wait = new WebDriverWait(driver,20);
		wait.until(ExpectedConditions.titleContains(title));
    }
	
	public void clickandhold(WebElement element){
	    Actions action = new Actions(driver);
	    action.clickAndHold(element).build().perform();	   
	}
	public String integertostring(String value) {
		int values= new BigDecimal(value).setScale(0,RoundingMode.HALF_UP).intValue();
		String svalue= Integer.toString(values);
		return svalue;
	}
	//****************************************************************
	 public void click_element(WebElement element) {
	 		element.click();
	 	}
	 public void fileupload(String filelocation) throws Throwable {
			try {
				Thread.sleep(3000);
				StringSelection stringSelection = new StringSelection(filelocation);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				Thread.sleep(3000);
				Robot robot = new Robot();
				Thread.sleep(3000);
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_CONTROL);
				Thread.sleep(3000);
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}

tu2----------------------------------------------------------------------------------------
	package utils;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class TU extends TestBase{
	
	WebDriverWait wew;
	static boolean isExecuted = false, ecieStatus, enveStatus, toeStatus, nseeStatus, sereStatus, displayStatus, enabledStatus, returnVal;
	long startTime;
	String str, returnStr, browserLoadStatus;
	Select sElement;
	Calendar cal;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    public List<String> browserTabs;
    private Actions actions;
    ChromeOptions chromeOptions;
    public static InputStream is;
    
	public static Properties prop=null;
	public static FileInputStream fis = null;
	public static String CurrentDir =System.getProperty("user.dir");
	public static String path = "/BDDFramework.properties";
	
    public TU() {
    	try{
			
			prop= new Properties();
			fis = new FileInputStream(CurrentDir+path);
			prop.load(fis);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	public void waitForPageLoad() throws InterruptedException{
    	WebDriverWait wait = new WebDriverWait(driver, 60);
    	wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver wdriver) {
                return ((JavascriptExecutor) driver).executeScript(
                    "return document.readyState"
                ).equals("complete");
            }
        });
    	do{
    		Thread.sleep(1000);
    		browserLoadStatus = (String) ((JavascriptExecutor) driver).executeScript("return document.readyState");
    		System.out.println("browserLoadStatus: "+browserLoadStatus);
    	}while(!browserLoadStatus.equals("complete"));
    }
	
	public void killAllDriverServers(){
		
		try {
			Runtime.getRuntime().exec("taskkill /f /im chromedriver75.exe");
			Runtime.getRuntime().exec("taskkill /f /im chromedriver77.exe");
			TimeUnit.SECONDS.sleep(4);
        }catch(Exception e) {
        	System.out.println("No cmd.exe was present in the Task Manager. ");
        	e.printStackTrace();
        }
	}
	
	public static void phantomClick(WebDriver driver, WebElement element){
        final String script = "function ghostclick(el){var ev = document.createEvent(\"MouseEvent\");ev.initMouseEvent(\"click\",true ,true,window,null,0,0,0,0,false,false,false,false,0,null);el.dispatchEvent(ev);} return ghostclick(arguments[0])";
        ((JavascriptExecutor) driver).executeScript(script, element);
    }
	
	public String webAppObject(WebDriver driver, WebElement element, String fn, String value, int objWaitTime, String pageName, String elementName, int criticality) throws InterruptedException {
		waitForPageLoad();
		wew = new WebDriverWait(driver, 60);
		cal = Calendar.getInstance();
		str = "["+sdf.format(cal.getTime())+"]"+"[Page: "+pageName+"]: [WebElement: "+elementName+"]"; returnStr = "false";
		
		startTime = System.currentTimeMillis(); //fetch starting time
		displayStatus = false; enabledStatus = false;
		do{
	    	try{
	    		wew.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)));
	    		displayStatus = element.isDisplayed();
	    		enabledStatus = element.isEnabled();
	    		ecieStatus = false;
	    		returnStr = "";
	    	}catch(ElementClickInterceptedException ecie){
	    		ecieStatus = true;
	    		returnStr = "Element Click Intercepted Exception";
	    	}catch(ElementNotVisibleException enve){
	    		enveStatus = true;
	    		returnStr = "Element Not Visible Exception";
	    	}catch(TimeoutException toe){
	    		toeStatus = true;
	    		displayStatus = false;
	    		enabledStatus = false;
	    		returnStr = "TimeoutException for : "+str;
	    	}catch(NoSuchElementException nsee){
				nseeStatus = true;
	    		displayStatus = false;
				enabledStatus = false;
				returnStr = "NoSuchElementException for : "+str;
			}catch(StaleElementReferenceException sere){
				sereStatus = true;
				displayStatus = true;
				returnStr = "StaleElementReferenceException for : "+str;
			}
	    	Thread.sleep(1000);
	    }while((ecieStatus || enveStatus || toeStatus || nseeStatus || sereStatus || !displayStatus || !enabledStatus) && (((System.currentTimeMillis()-startTime) <= (objWaitTime*1000))));
		System.out.println("["+sdf.format(cal.getTime())+"]"+"["+elementName+": ecieStatus="+ecieStatus+", enveStatus="+enveStatus+", toeStatus="+toeStatus+", nseeStatus="+nseeStatus+", sereStatus="+sereStatus+", !displayStatus="+!displayStatus+", !enabledStatus="+!enabledStatus+"]");
		if(returnStr.contains("Exception") && criticality==1){
			returnStr = returnStr + ", displayStatus="+displayStatus+", Criticality="+criticality;
			return returnStr;
		}

		
		startTime = System.currentTimeMillis(); //fetch starting time
		
		if(displayStatus && enabledStatus){
//			System.out.println(str+": [Display Status: "+displayStatus+"], [Enabled Status: "+enabledStatus+"]. ");
			switch(fn){
			case "verify":
				returnStr = "true";
				System.out.println(str+": [Display Status: "+displayStatus+"], [Enabled Status: "+enabledStatus+"]. ");
				break;
			case "click": 
				element.click();
//				actions = new Actions(driver);
//				
//				do{
//			    	try{
//			    		if(prop.getProperty("webdriver.driver").equals("iexplorer")) {
//			    			System.out.println("Inside actions. ");
//			    			actions.click(element).build().perform();
//			    		}else {
//			    			element.click();
//			    		}
//			    		
//			    		ecieStatus = false;
//			    	}catch(ElementClickInterceptedException ecie){
//			    		ecieStatus = true;
//			    		Thread.sleep(1000);
//			    		System.out.println("["+sdf.format(cal.getTime())+"]"+elementName+": Element Click Intercepted Exception");
//			    	}
//		    	}while((ecieStatus) && (((System.currentTimeMillis()-startTime) <= (objWaitTime*1000))));
				System.out.println(str+": Element clicked. ");
				returnStr = "true";
				break;
			case "doubleClick": 
				actions = new Actions(driver);
				actions.doubleClick(element).perform();
				returnStr = "true";
				break;
			case "clear": 
				element.clear();
				System.out.println(str+": Element cleared. ");
				returnStr = "true";
				break;
			case "sendKeys": 
				element.sendKeys(value);
//				actions = new Actions(driver);
//				actions.sendKeys(element, value).build().perform();
				System.out.println("Entered value: "+value+" in "+str);
				returnStr = "true";
				break;
			case "sendKeysWithDelay":
				element.clear();
				for(int i=0;i<value.length();i++){
					Thread.sleep(objWaitTime*1000);
					element.sendKeys(Keys.HOME+Character.toString(value.charAt(i)));
				}
				System.out.println("Entered value: "+value+" in "+str);
				returnStr = "true";
				break;
			case "selectByVisibleText":
				sElement = new Select(element);
				sElement.selectByVisibleText(value);
				System.out.println("Selected visible text: "+value+" in "+str);
				returnStr = "true";
				break;
			case "selectByValue":
				sElement = new Select(element);
				sElement.selectByValue(value);
				System.out.println("Selected value: "+value+" in "+str);
				returnStr = "true";
				break;
			case "selectByIndex":
				sElement = new Select(element);
				sElement.selectByIndex(Integer.valueOf(value));
				System.out.println("Selected index: "+value+" in "+str);
				returnStr = "true";
				break;
			case "getFirstSelectedOption-GetText":
				sElement = new Select(element);
				returnStr = sElement.getFirstSelectedOption().getText();
				System.out.println("First Selected value Text: "+returnStr);
				break;
			case "getFirstSelectedOption-GetValue":
				sElement = new Select(element);
				returnStr = sElement.getFirstSelectedOption().getAttribute("value");
				System.out.println("First Selected value: "+returnStr);
				break;
			case "getText":
				returnStr = element.getText();
				System.out.println(str+": Element text: "+element.getText());
				break;
			case "check":
				System.out.println("element.isSelected(): "+element.isSelected());
				if(!rBtnSelected(element)){ //if(!element.isSelected()){
					element.click();
					System.out.println(str+": Checked element. ");
				}else if(rBtnSelected(element)){
					System.out.println(str+": Element is already checked. ");
				}
				returnStr = "true";
				break;
			case "uncheck":
				if(rBtnSelected(element)){ //if(element.isSelected()){
					element.click();
					System.out.println(str+": Unchecked element. ");
				}else if(!rBtnSelected(element)){
					System.out.println(str+": Element is already unchecked. ");
				}
				returnStr = "true";
				break;
			case "getAttribute":
				returnStr = element.getAttribute(value);
				System.out.println(str+": Element Attribute: "+value+" = "+returnStr);
				break;
			default:
				System.out.println(str+": [Display Status: "+displayStatus+"], [Enabled Status: "+enabledStatus+"]. Interaction to be performed on the object is not specified properly in the code. ");
				break;
			}
		}
		else{
			System.out.println(str+": [Display Status: "+displayStatus+"], [Enabled Status: "+enabledStatus+"]. ");
			returnStr = "false";
		}
		
		return returnStr;
	}
	
	public boolean rBtnSelected(WebElement element){
		if(element.getAttribute("class").contains("radioUnchecked") || (!element.isSelected())){
			returnVal = false;
		}else if(element.getAttribute("class").contains("radioChecked") || (element.isSelected())){
			returnVal = true;
		}
		return returnVal;
	}
    
    public String validateSite(String site, String title, String link, String tab) throws InterruptedException{
    	browserTabs = new ArrayList<String>();
    	browserTabs.addAll(driver.getWindowHandles());
    	System.out.println("browserTabs: "+browserTabs);
    	if(browserTabs.size()>=2){ // && tab.equals("new")
    		if(driver.getTitle().equals("Client Management Home")){
    			driver.switchTo().window(browserTabs.get(1));
    		}else if(driver.getTitle().equals("Login")){
    			return "Login";
//    			driver.switchTo().window(browserTabs.get(browserTabs.size()-1));
    		}else{
    			driver.switchTo().window(browserTabs.get(browserTabs.size()-1));
    		}
        	waitForPageLoad();
        	Thread.sleep(4000);
        	System.out.println("Title: "+driver.getTitle());
        	assertThat("Title does NOT match. Actual Title: "+driver.getTitle(), driver.getTitle().contains(title));
        	assertThat("website URL does NOT match. Expected URL: "+site+". Actual URL: "+driver.getCurrentUrl(), (driver.getCurrentUrl().contains(site) || site.contains(driver.getCurrentUrl())));
    	}
    	browserTabs = null;
    	return driver.getTitle();
    }
    
    
    public void tabAction(String action, String tab) {
    	if(action.equals("close")){
        	browserTabs = new ArrayList<String>();
        	browserTabs.addAll(driver.getWindowHandles());
        	driver.switchTo().window(browserTabs.get(1));
    		driver.close();
    		driver.switchTo().window(browserTabs.get(0));
    	}else if(action.equals("navigate") && tab.equals("back")){
    		driver.navigate().back();
    	}
    }
	

    public void switchToTab(int tabIndex) throws InterruptedException{
    	Thread.sleep(4000);
    	browserTabs = null;
    	browserTabs = new ArrayList<String>();
    	browserTabs.addAll(driver.getWindowHandles());
    	
    	driver.switchTo().window(browserTabs.get(tabIndex));
    	Thread.sleep(3000);
    }
    
    public void waitTillTitle(String tabTitle, int titleWaitTime) throws InterruptedException{
    	startTime = System.currentTimeMillis(); //fetch starting time
		do{
			if(driver.getTitle().equals(tabTitle)){
				break;
			}
    	}while((System.currentTimeMillis()-startTime) <= (titleWaitTime*1000));
    }
    
    public void navigateToURL(String url, String tab) throws InterruptedException {
    	if(tab.equals("new")){
    		((JavascriptExecutor) driver).executeScript("window.open()");
        	driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
        	switchToTab(1);
        	driver.get(url);
    	}else if(tab.equals("same")){
    		driver.get(url);
    	}
    	waitForPageLoad();
    	driver.manage().window().maximize();
    }
    
    // ****************************************************************
    // This Method creates an object for properties file, loads it
    // and reads the contents from properties file
    // ****************************************************************
 	public void readPropertyFile() throws IOException {
 		prop = new Properties();
 		is = new FileInputStream("serenity.properties");
 		prop.load(is);
 		System.out.println("Path loaded. ");
 	}
	
	// This Method handles switch between multiple windows
	public void window_navigate() {
		Set<String> allwin = driver.getWindowHandles();
		for (String testwin : allwin)
			driver.switchTo().window(testwin);
	}
	
	// This method implements implicit waits
	public void implicitwait(int timeout) {
		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
	}
	
	public void clearCache(WebDriver driver) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C://WebDrivers/chromedriver.exe");
		chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("disable-infobars");
		chromeOptions.addArguments("start-maximized");
		driver = new ChromeDriver(chromeOptions);
		driver.get("chrome://settings/clearBrowserData");
		Thread.sleep(5000);
		driver.switchTo().activeElement();
		driver.findElement(By.cssSelector("* /deep/ #clearBrowsingDataConfirm")).click();
		Thread.sleep(5000);
	}

	/* This will launch browser with cache disabled */
	public void launchWithoutCache() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C://WebDrivers/chromedriver.exe");
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability("applicationCacheEnabled", false);
//		driver = new ChromeDriver(cap);
	}
	
	public void ajaxWait(WebDriver driver, long seconds) {
	    try{
	        while(!waitForJSandJQueryToLoad(driver)) {
	            try {
	                Thread.sleep(1000);
	                seconds -= 1;
	                if (seconds <= 0) {
	                    return;
	                }
	            } catch (InterruptedException var4) {
	                var4.printStackTrace();
	            }
	        }
	    } catch(Exception e){
	        e.printStackTrace();
	    }
	}

	public boolean waitForJSandJQueryToLoad(WebDriver driver) {

	    WebDriverWait wait = new WebDriverWait(driver, 3);

	    // wait for jQuery to load
	    ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
	        @Override
	        public Boolean apply(WebDriver driver) {
	            try {
	                return ((Long)((JavascriptExecutor)driver).executeScript("return jQuery.active") == 0);
	            }
	            catch (Exception e) {
	                // no jQuery present
	                e.printStackTrace();
	                return true;
	            }
	        }
	    };

	    // wait for Javascript to load
	    ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
	        @Override
	        public Boolean apply(WebDriver driver) {
	            return ((JavascriptExecutor)driver).executeScript("return document.readyState")
	                    .toString().equals("complete");
	        }
	    };

	    return wait.until(jQueryLoad) && wait.until(jsLoad);
	}
	
}
---------------Get email link--------------------------------
	package com.advisorgroup.WEBFEI.aut.utils;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;



public class getlinkmail {
//	public static void main(String args[]) throws MessagingException, IOException{
//		String urlValue = getmaillink();
//		System.out.println(urlValue);
//	}
//	

public static String getmaillink() throws MessagingException, IOException {
          String passcodex = null;
          String host = "imap-mail.outlook.com";
          String mailStoreType = "imap";
          String username = "siva.sankar@advisorgroup.com";
          String password = "OnOa14546";
//          Srisai123$
          String urlLink="";
          Properties properties = new Properties();
          properties.put("mail.imap.host",host);
          properties.put("mail.imap.port", "993"); 
          properties.put("mail.imap.starttls.enable", "true");
          properties.setProperty("mail.imap.socketFactory.class","javax.net.ssl.SSLSocketFactory");
          properties.setProperty("mail.imap.socketFactory.fallback", "false");
          properties.setProperty("mail.imap.socketFactory.port",String.valueOf(993));
          Session emailSession = Session.getDefaultInstance(properties);
          //create the POP3 store object and connect with the pop server
          Store store = emailSession.getStore("imap");
          store.connect(host, username, password);
          Folder folder = store.getFolder("Inbox");
          if (!folder.exists()) {
              System.out.println("No INBOX...");
              System.exit(0);
              }
              folder.open(Folder.READ_WRITE);
              SearchTerm sender = new FromTerm(new InternetAddress("noreply@signixmail.com"));
              Message[] msg = folder.search(sender);
              int n=msg.length;
              System.out.println("SAI &&&&&&&&&&&&&&: "+ n);
              for (int i = n-1; i>n-2; i--) {
                	//String search= "Royal Alliance:";
                	String search="FSC Securities";
            	  	System.out.println("Step 1 "+ i +":" + msg[i].getSubject());
            	  	if(msg[i].getSubject().contains(search)){
                        Multipart multipart = (Multipart) msg[i].getContent();
         
                        for (int x = 0; x < multipart.getCount(); x++) {
                              BodyPart bodyPart = multipart.getBodyPart(x);
                              DataHandler handler = bodyPart.getDataHandler();
                              //System.out.println(bodyPart.getContent());
                              Object msgcon = bodyPart.getContent();
                              String messagecontent=msgcon.toString();
                              //System.out.println(messagecontent);
                              int intIndex = messagecontent.indexOf("https:");
                              //System.out.println(intIndex);
                              //System.out.println((messagecontent.substring(intIndex,intIndex+69)));
                              urlLink=messagecontent.substring(intIndex,intIndex+69);
                              }
                     } 
          }       
          folder.close(true);
          store.close();
          //return passcodex;       
		return urlLink;
        }         
}
