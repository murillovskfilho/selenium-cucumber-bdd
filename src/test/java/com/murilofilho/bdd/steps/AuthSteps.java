package com.murilofilho.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.murilofilho.bdd.context.ScenarioContext;
import com.murilofilho.bdd.data.Persona;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AuthSteps {

    private final ScenarioContext ctx;

    public AuthSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @Given("I am on the sign-in page")
    public void iAmOnTheSignInPage() {
        ctx.loginPage().open();
    }

    @Given("I am not signed in")
    public void iAmNotSignedIn() {
        ctx.driver(); // a fresh browser per scenario: no session cookie exists yet
    }

    @Given("I am signed in as {persona}")
    public void iAmSignedInAs(Persona persona) {
        ctx.loginPage().signInViaApi(persona);
    }

    @When("I sign in as {persona}")
    public void iSignInAs(Persona persona) {
        ctx.loginPage().signIn(persona.email, Persona.PASSWORD);
    }

    @When("I sign in with email {string} and password {string}")
    public void iSignInWith(String email, String password) {
        ctx.loginPage().signIn(email, password);
    }

    @Then("I see the sign-in error {string}")
    public void iSeeTheSignInError(String message) {
        assertThat(ctx.loginPage().error()).isEqualTo(message);
    }

    @Then("I am still on the sign-in page")
    public void iAmStillOnTheSignInPage() {
        assertThat(ctx.loginPage().currentPath()).startsWith("/login");
    }

    @Then("I am asked to sign in")
    public void iAmAskedToSignIn() {
        ctx.loginPage().waitForPath("/login?next=");
    }

    @Then("I see the shipments list")
    public void iSeeTheShipmentsList() {
        ctx.shipmentsPage().waitUntilLoaded();
        assertThat(ctx.shipmentsPage().currentPath()).isEqualTo("/");
    }

    @Then("I am identified as {string} with the role {string}")
    public void iAmIdentifiedAs(String name, String role) {
        assertThat(ctx.shipmentsPage().currentUser()).isEqualTo(name);
        assertThat(ctx.shipmentsPage().currentRole()).isEqualTo(role);
    }
}
