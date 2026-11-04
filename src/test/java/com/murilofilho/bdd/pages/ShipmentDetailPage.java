package com.murilofilho.bdd.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ShipmentDetailPage extends BasePage {

    private static final By TITLE = By.id("title");
    private static final By NOTICE = By.id("notice");
    private static final By ERROR = By.id("error");
    private static final By DETAILS = By.id("details");
    private static final By CANCEL = By.id("cancel");

    public ShipmentDetailPage(WebDriver driver) {
        super(driver);
    }

    public ShipmentDetailPage open(String id) {
        navigateTo("/shipment?id=" + id);
        return waitUntilLoaded();
    }

    /** Loaded means either the details or the not-found message rendered. */
    public ShipmentDetailPage waitUntilLoaded() {
        wait.until(d -> isDisplayed(DETAILS) || isDisplayed(ERROR));
        return this;
    }

    public String title() {
        return visible(TITLE).getText();
    }

    public String detail(String term) {
        return visible(By.xpath("//dl[@id='details']/dt[normalize-space()=" + xpathLiteral(term) + "]/following-sibling::dd[1]")).getText();
    }

    public String status() {
        return detail("Status");
    }

    public void waitForStatus(String status) {
        wait.until(ExpectedConditions.textToBe(
                By.xpath("//dl[@id='details']/dt[normalize-space()='Status']/following-sibling::dd[1]"), status));
    }

    public boolean cancelAvailable() {
        return isDisplayed(CANCEL);
    }

    /** Clicks "Cancel shipment" and answers the browser's native confirm dialog. Returns its text. */
    public String cancel(boolean confirm) {
        click(CANCEL);
        Alert dialog = wait.until(ExpectedConditions.alertIsPresent());
        String message = dialog.getText();
        if (confirm) dialog.accept();
        else dialog.dismiss();
        return message;
    }

    public String notice() {
        return visible(NOTICE).getText();
    }

    public String error() {
        return visible(ERROR).getText();
    }
}
