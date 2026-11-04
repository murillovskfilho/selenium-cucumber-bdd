package com.murilofilho.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Runs every scenario as a TestNG test. parallel = true runs scenarios concurrently;
 * the thread count comes from the "threads" Maven property (dataproviderthreadcount).
 * Filter with -Dcucumber.filter.tags="@smoke".
 */
@CucumberOptions(features = "classpath:features", glue = "com.murilofilho.bdd")
public class RunCucumberTest extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
