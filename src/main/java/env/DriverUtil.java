package env;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by tom on 24/02/17.
 */
public class DriverUtil {
    public static long DEFAULT_WAIT = 20;
    protected static WebDriver driver;

    public static WebDriver getDefaultDriver() {
		if (driver != null) {
			return driver;
		}
		System.out.println("*******************************************************************************************************");
        //System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver");
        DesiredCapabilities capabilities = null;
		capabilities = DesiredCapabilities.firefox();
        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability("takesScreenshot", true);
        driver = chooseDriver(capabilities);
        driver.manage().timeouts().setScriptTimeout(DEFAULT_WAIT,
                TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }

    /**
     * By default to web driver will be PhantomJS
     *
     * Override it by passing -DWebDriver=Chrome to the command line arguments
     * @param capabilities
     * @return
     */
    private static WebDriver chooseDriver(DesiredCapabilities capabilities) {
		String preferredDriver = System.getProperty("browser", "Firefox");
		boolean headless = System.getProperty("Headless", "true").equals("true");
		System.out.println("preferredDriver "+ preferredDriver.toLowerCase());
		switch (preferredDriver.toLowerCase()) {
			case "chrome":
				final ChromeOptions chromeOptions = new ChromeOptions();
				if (headless) {
					chromeOptions.addArguments("--headless");
				}
				capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
				System.out.println("********************* before ChromeDriver created");
				ChromeDriver driver = new ChromeDriver();
				System.out.println("********************* after ChromeDriver created");
				ErrorHandler handler = new ErrorHandler();
				handler.setIncludeServerErrors(false);
				driver.setErrorHandler(handler);
				return driver;
			case "phantomjs":

				System.out.println("********************* before phantomJSDriver created");
				PhantomJSDriver phantomJSDriver = new PhantomJSDriver(capabilities);
				System.out.println("********************* after phantomJSDriver created");
				return phantomJSDriver;
				
			default:
				//return new PhantomJSDriver(capabilities);
				FirefoxOptions options = new FirefoxOptions();
				//capabilities.s
				if (headless) {
					options.addArguments("-headless", "-safe-mode");
				}
				capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
				System.out.println("********************* before FirefoxDriver created");
				final FirefoxDriver firefox = new FirefoxDriver();
				System.out.println("********************* after FirefoxDriver created");
				return firefox;
		}
    }

    public static WebElement waitAndGetElementByCssSelector(WebDriver driver, String selector,
                                                            int seconds) {
        By selection = By.cssSelector(selector);
        return (new WebDriverWait(driver, seconds)).until( // ensure element is visible!
                ExpectedConditions.visibilityOfElementLocated(selection));
    }

	public static void closeDriver() {
		if (driver != null) {
			try {

				System.out.println("********************* close driver "+ driver);
				 driver.manage().deleteAllCookies();
				//driver.close();
				//driver.quit(); // fails in current geckodriver! TODO: Fixme
			} catch (NoSuchMethodError nsme) { // in case quit fails
			} catch (NoSuchSessionException nsse) { // in case close fails
			} catch (SessionNotCreatedException snce) {} // in case close fails
			driver = null;
		}
	}
}
