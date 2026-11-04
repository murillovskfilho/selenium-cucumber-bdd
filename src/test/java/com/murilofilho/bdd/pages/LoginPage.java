package com.murilofilho.bdd.pages;

import com.murilofilho.bdd.api.ShipDeskApi;
import com.murilofilho.bdd.config.Config;
import com.murilofilho.bdd.data.Persona;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By ERROR = By.id("login-error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        navigateTo("/login");
        visible(By.tagName("h1"));
        return this;
    }

    public void signIn(String email, String password) {
        type(fieldByLabel("Email"), email);
        type(fieldByLabel("Password"), password);
        click(button("Sign in"));
    }

    public String error() {
        return visible(ERROR).getText();
    }

    /**
     * Signs in without the UI: gets a token from the API and plants the session cookie.
     * Scenarios that are not about logging in start authenticated in milliseconds.
     */
    public void signInViaApi(Persona persona) {
        navigateTo("/login");
        driver.manage().addCookie(new Cookie.Builder("session", ShipDeskApi.token(persona)).path("/").isHttpOnly(true).build());
        driver.get(Config.appUrl() + "/");
    }
}
