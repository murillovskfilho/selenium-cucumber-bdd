package com.murilofilho.bdd.pages;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class NewShipmentPage extends BasePage {

    private static final By HEADING = By.xpath("//h1[normalize-space()='New shipment']");
    private static final By FORM = By.id("shipment-form");
    private static final By FORBIDDEN = By.id("forbidden");

    public NewShipmentPage(WebDriver driver) {
        super(driver);
    }

    public NewShipmentPage open() {
        navigateTo("/new");
        return waitUntilReady();
    }

    /** Ready means either the form or the permission message has rendered. */
    public NewShipmentPage waitUntilReady() {
        visible(HEADING);
        wait.until(d -> isDisplayed(FORM) || isDisplayed(FORBIDDEN));
        return this;
    }

    public boolean formAvailable() {
        return isDisplayed(FORM) && !isDisplayed(FORBIDDEN);
    }

    public String forbiddenMessage() {
        return visible(FORBIDDEN).getText();
    }

    /** Fills fields by their visible label: {"Carrier": "FEDEX", "Weight (oz)": "12.5"}. */
    public void fill(Map<String, String> byLabel) {
        byLabel.forEach((label, value) -> {
            By field = fieldByLabel(label);
            WebElement element = visible(field);
            if ("select".equals(element.getTagName())) {
                Select select = new Select(element);
                if (value.isEmpty()) select.selectByIndex(0);
                else select.selectByVisibleText(value);
            } else {
                element.clear();
                element.sendKeys(value);
            }
        });
    }

    public void submit() {
        click(button("Create shipment"));
    }

    /** The error message announced for a field (linked through aria-describedby). */
    public String errorFor(String label) {
        WebElement field = visible(fieldByLabel(label));
        wait.until(d -> "true".equals(field.getDomAttribute("aria-invalid")));
        return driver.findElement(By.id(field.getDomAttribute("aria-describedby"))).getText();
    }

    public boolean hasFormError() {
        return visible(alert("Please fix the errors below.")).isDisplayed();
    }
}
