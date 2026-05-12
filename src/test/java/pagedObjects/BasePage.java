package pagedObjects;

import factory.DriverFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.WaitHelper;
import java.time.Duration;
public class BasePage {

    protected WebDriver          driver;
    protected WebDriverWait      wait;
    protected JavascriptExecutor js;

    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js     = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    protected void scrollTo(WebElement el)  { WaitHelper.scrollToElement(el); }
    protected void jsClick(WebElement el)   { WaitHelper.jsClick(el); }
    protected void sleep(long ms)           { WaitHelper.sleep(ms); }

    public String getPageTitle()   { return driver.getTitle(); }
    public String getCurrentUrl()  { return driver.getCurrentUrl(); }
}