package com.murilofilho.bdd.pages;

import com.murilofilho.bdd.config.Config;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Shared page behaviour. Explicit waits only (no implicit waits, no Thread.sleep), and
 * form fields are located through their visible label, the way a user finds them.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.timeout());
    }

    protected void navigateTo(String path) {
        driver.get(Config.appUrl() + path);
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitHidden(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected boolean isDisplayed(By locator) {
        List<WebElement> found = driver.findElements(locator);
        return !found.isEmpty() && found.getFirst().isDisplayed();
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        field.sendKeys(text);
    }

    /** The control associated with a <label>, resolved through its "for" attribute. */
    protected By fieldByLabel(String label) {
        WebElement labelElement = visible(By.xpath("//label[normalize-space()=" + xpathLiteral(label) + "]"));
        return By.id(labelElement.getDomAttribute("for"));
    }

    protected By button(String name) {
        return By.xpath("//button[normalize-space()=" + xpathLiteral(name) + "]");
    }

    protected By alert(String containing) {
        return By.xpath("//*[@role='alert' and contains(normalize-space(), " + xpathLiteral(containing) + ")]");
    }

    public String currentPath() {
        String url = driver.getCurrentUrl();
        return url.substring(Config.appUrl().length());
    }

    public void waitForPath(String pathPrefix) {
        wait.until(d -> currentPath().startsWith(pathPrefix));
    }

    /** Quotes a string for XPath, including strings that contain both quote types. */
    protected static String xpathLiteral(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        return "concat('" + s.replace("'", "', \"'\", '") + "')";
    }
}
