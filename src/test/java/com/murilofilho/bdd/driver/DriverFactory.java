package com.murilofilho.bdd.driver;

import com.murilofilho.bdd.config.Config;
import java.net.MalformedURLException;
import java.net.URI;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/** Creates one WebDriver per scenario: remote on a Selenium Grid, or local via Selenium Manager. */
public final class DriverFactory {

    private DriverFactory() {}

    public static WebDriver create() {
        MutableCapabilities options = switch (Config.browser()) {
            case "chrome" -> chromeOptions();
            case "firefox" -> firefoxOptions();
            default -> throw new IllegalArgumentException("Unsupported browser: " + Config.browser());
        };

        WebDriver driver = Config.gridUrl().map(url -> remote(url, options)).orElseGet(() -> local(options));
        driver.manage().window().setSize(new Dimension(1366, 900));
        return driver;
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) options.addArguments("--headless=new");
        options.addArguments("--disable-search-engine-choice-screen", "--no-first-run");
        // The app runs over plain HTTP on an internal hostname (http://app:3000 on the grid).
        // Recent Chrome upgrades such URLs to HTTPS and fails with ERR_SSL_PROTOCOL_ERROR.
        options.addArguments("--disable-features=HttpsUpgrades,HttpsFirstBalancedModeAutoEnable,HttpsFirstModeV2ForTypicallySecureUsers");
        // Native dialogs are handled explicitly by the steps; never auto-dismiss them.
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (Config.headless()) options.addArguments("-headless");
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
        return options;
    }

    private static WebDriver remote(String gridUrl, MutableCapabilities options) {
        try {
            return new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid grid URL: " + gridUrl, e);
        }
    }

    private static WebDriver local(MutableCapabilities options) {
        return options instanceof ChromeOptions chrome ? new ChromeDriver(chrome) : new FirefoxDriver((FirefoxOptions) options);
    }
}
