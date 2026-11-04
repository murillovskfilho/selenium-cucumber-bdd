package com.murilofilho.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.murilofilho.bdd.api.ShipDeskApi;
import com.murilofilho.bdd.context.ScenarioContext;
import com.murilofilho.bdd.data.Persona;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.Map;

/** The shipment detail page: details, cancellation, and what the API says about the same shipment. */
public class ShipmentDetailSteps {

    private final ScenarioContext ctx;

    public ShipmentDetailSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @When("I open that shipment")
    public void iOpenThatShipment() {
        ctx.shipmentPage().open(ctx.shipment().id());
    }

    @Given("another administrator cancels that shipment")
    public void anotherAdministratorCancelsThatShipment() {
        assertThat(ShipDeskApi.cancelShipment(Persona.ADMIN, ctx.shipment().id()).statusCode()).isEqualTo(200);
    }

    @When("I cancel the shipment and confirm")
    public void iCancelAndConfirm() {
        String dialog = ctx.shipmentPage().cancel(true);
        assertThat(dialog).isEqualTo("Cancel shipment " + ctx.shipment().id() + "? This cannot be undone.");
    }

    @When("I cancel the shipment but dismiss the confirmation")
    public void iCancelButDismiss() {
        ctx.shipmentPage().cancel(false);
    }

    @Then("I see the confirmation {string}")
    public void iSeeTheConfirmation(String message) {
        assertThat(ctx.shipmentPage().notice()).isEqualTo(message);
    }

    @Then("I see the error {string}")
    public void iSeeTheError(String message) {
        assertThat(ctx.shipmentPage().error()).isEqualTo(message);
    }

    @Then("I see the page title {string}")
    public void iSeeThePageTitle(String title) {
        assertThat(ctx.shipmentPage().title()).isEqualTo(title);
    }

    @Then("the shipment details show:")
    public void theShipmentDetailsShow(Map<String, String> expected) {
        expected.forEach((term, value) -> assertThat(ctx.shipmentPage().detail(term)).as(term).isEqualTo(value));
    }

    @Then("the shipment status is {string}")
    public void theShipmentStatusIs(String status) {
        ctx.shipmentPage().waitForStatus(status);
    }

    @Then("the cancel action is {availability}")
    public void theCancelActionIs(boolean available) {
        assertThat(ctx.shipmentPage().status()).isEqualTo("Label created");
        assertThat(ctx.shipmentPage().cancelAvailable()).isEqualTo(available);
    }

    @Then("the API reports the shipment as {string}")
    public void theApiReportsTheShipmentAs(String status) {
        Response res = ShipDeskApi.getShipment(Persona.ADMIN, ctx.shipment().id());
        assertThat(res.<String>path("status")).isEqualTo(status);
    }

    @Then("the API hides that shipment from {persona} with status {int}")
    public void theApiHidesThatShipment(Persona persona, int status) {
        assertThat(ShipDeskApi.getShipment(persona, ctx.shipment().id()).statusCode()).isEqualTo(status);
        assertThat(ShipDeskApi.cancelShipment(persona, ctx.shipment().id()).statusCode()).isEqualTo(status);
    }

    @When("{persona} calls the API to create a shipment")
    public void callsTheApiToCreate(Persona persona) {
        ctx.lastResponse(ShipDeskApi.createShipment(persona, com.murilofilho.bdd.data.ShipmentData.valid()));
    }

    @When("{persona} calls the API to cancel that shipment")
    public void callsTheApiToCancel(Persona persona) {
        ctx.lastResponse(ShipDeskApi.cancelShipment(persona, ctx.shipment().id()));
    }

    @Then("the API answers with status {int}")
    public void theApiAnswersWithStatus(int status) {
        assertThat(ctx.lastResponse().statusCode()).as(ctx.lastResponse().asString()).isEqualTo(status);
    }
}
