package com.murilofilho.bdd.pages;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class ShipmentsPage extends BasePage {

    private static final By HEADING = By.xpath("//h1[normalize-space()='Shipments']");
    private static final By LOADING = By.id("loading");
    private static final By ROWS = By.cssSelector("#shipments tbody tr");
    private static final By EMPTY = By.id("empty");
    private static final By PAGE_INFO = By.id("page-info");
    private static final By NEW_SHIPMENT = By.linkText("New shipment");
    private static final By CURRENT_USER = By.cssSelector("[data-testid='current-user']");
    private static final By CURRENT_ROLE = By.cssSelector("[data-testid='current-role']");

    public record Row(String id, String reference, String recipient, String carrier, String status, String created) {}

    public ShipmentsPage(WebDriver driver) {
        super(driver);
    }

    public ShipmentsPage open() {
        navigateTo("/");
        return waitUntilLoaded();
    }

    public ShipmentsPage waitUntilLoaded() {
        visible(HEADING);
        waitHidden(LOADING);
        return this;
    }

    /**
     * Searches and waits for *this* search to render: the page writes the filter to the URL when the
     * debounced request starts, and hides the loading indicator when its response is drawn.
     */
    public void searchFor(String text) {
        type(fieldByLabel("Search"), text);
        String expected = "q=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
        wait.until(d -> currentPath().contains(expected));
        waitUntilLoaded();
    }

    public void filterByStatus(String visibleStatus) {
        new Select(visible(fieldByLabel("Status"))).selectByVisibleText(visibleStatus);
        wait.until(d -> currentPath().contains("status="));
        waitUntilLoaded();
    }

    public List<Row> rows() {
        return driver.findElements(ROWS).stream().map(tr -> {
            List<String> cells = tr.findElements(By.tagName("td")).stream().map(WebElement::getText).toList();
            return new Row(cells.get(0), cells.get(1), cells.get(2), cells.get(3), cells.get(4), cells.get(5));
        }).toList();
    }

    public boolean emptyStateShown() {
        return isDisplayed(EMPTY);
    }

    public String emptyStateText() {
        return visible(EMPTY).getText();
    }

    public String pageInfo() {
        return visible(PAGE_INFO).getText();
    }

    public void nextPage() {
        String before = pageInfo();
        click(button("Next"));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(PAGE_INFO, before)));
        waitUntilLoaded();
    }

    public boolean canCreateShipments() {
        visible(CURRENT_ROLE);
        return isDisplayed(NEW_SHIPMENT);
    }

    public String currentUser() {
        return visible(CURRENT_USER).getText();
    }

    public String currentRole() {
        return visible(CURRENT_ROLE).getText();
    }
}
