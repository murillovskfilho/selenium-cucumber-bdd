package com.murilofilho.bdd.context;

import com.murilofilho.bdd.api.Shipment;
import com.murilofilho.bdd.driver.DriverFactory;
import com.murilofilho.bdd.pages.LoginPage;
import com.murilofilho.bdd.pages.NewShipmentPage;
import com.murilofilho.bdd.pages.ShipmentDetailPage;
import com.murilofilho.bdd.pages.ShipmentsPage;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;

/**
 * Per-scenario state, injected by PicoContainer into every step class and hook.
 * A new instance per scenario keeps parallel scenarios fully isolated.
 * The browser is created lazily, so API-only scenarios never start one.
 */
public class ScenarioContext {

    private WebDriver driver;
    private Shipment shipment;
    private Response lastResponse;

    public WebDriver driver() {
        if (driver == null) driver = DriverFactory.create();
        return driver;
    }

    public boolean hasDriver() {
        return driver != null;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public LoginPage loginPage() {
        return new LoginPage(driver());
    }

    public ShipmentsPage shipmentsPage() {
        return new ShipmentsPage(driver());
    }

    public NewShipmentPage newShipmentPage() {
        return new NewShipmentPage(driver());
    }

    public ShipmentDetailPage shipmentPage() {
        return new ShipmentDetailPage(driver());
    }

    /** The shipment the scenario is about ("that shipment"). */
    public Shipment shipment() {
        if (shipment == null) throw new IllegalStateException("No shipment in this scenario yet");
        return shipment;
    }

    public void shipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Response lastResponse() {
        return lastResponse;
    }

    public void lastResponse(Response response) {
        this.lastResponse = response;
    }
}
