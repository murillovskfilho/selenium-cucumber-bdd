package com.murilofilho.bdd.hooks;

import com.murilofilho.bdd.config.Config;
import com.murilofilho.bdd.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import java.nio.charset.StandardCharsets;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    private final ScenarioContext ctx;

    public Hooks(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @Before
    public void labelRun() {
        // Lets one Allure report hold the Chrome and the Firefox run side by side.
        Allure.parameter("browser", Config.browser());
    }

    /** On failure: screenshot, URL and page source attached to the report. Always: close the browser. */
    @After
    public void tearDown(Scenario scenario) {
        if (!ctx.hasDriver()) return;
        try {
            if (scenario.isFailed()) {
                scenario.attach(((TakesScreenshot) ctx.driver()).getScreenshotAs(OutputType.BYTES), "image/png", "screenshot");
                scenario.attach(ctx.driver().getCurrentUrl(), "text/plain", "url");
                scenario.attach(ctx.driver().getPageSource().getBytes(StandardCharsets.UTF_8), "text/html", "page source");
            }
        } catch (RuntimeException e) {
            scenario.log("Could not capture failure evidence: " + e.getMessage());
        } finally {
            ctx.quitDriver();
        }
    }
}
