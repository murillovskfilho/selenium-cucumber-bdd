package com.murilofilho.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.murilofilho.bdd.api.ShipDeskApi;
import com.murilofilho.bdd.api.Shipment;
import com.murilofilho.bdd.context.ScenarioContext;
import com.murilofilho.bdd.data.Persona;
import com.murilofilho.bdd.data.ShipmentData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class CreateShipmentSteps {

    private final ScenarioContext ctx;
    private String submittedReference;

    public CreateShipmentSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @Given("I am on the new shipment page")
    public void iAmOnTheNewShipmentPage() {
        ctx.newShipmentPage().open();
    }

    /** Navigates only: what should happen next (form, refusal, redirect) is for the Then steps to check. */
    @When("I open the new shipment page")
    public void iOpenTheNewShipmentPage() {
        ctx.driver().get(com.murilofilho.bdd.config.Config.appUrl() + "/new");
    }

    @When("I create a shipment with:")
    public void iCreateAShipmentWith(Map<String, String> fields) {
        submit(fields);
    }

    @When("I create a shipment with {string} set to {string}")
    public void iCreateAShipmentWithFieldSetTo(String field, String value) {
        submit(Map.of(field, value));
    }

    @When("I create a shipment with {string} set to that shipment's reference")
    public void iCreateAShipmentWithExistingReference(String field) {
        submit(Map.of(field, ctx.shipment().reference()));
    }

    /** Starts from a valid form, applies the scenario's values on top, and submits. */
    private void submit(Map<String, String> overrides) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("Reference", ShipmentData.uniqueReference());
        form.put("Carrier", "UPS");
        form.put("Service level", "GROUND");
        form.put("Recipient name", "Grace Hopper");
        form.put("Destination ZIP", "94105");
        form.put("Weight (oz)", "24");
        form.putAll(overrides);
        submittedReference = form.get("Reference");
        ctx.newShipmentPage().fill(form);
        ctx.newShipmentPage().submit();
    }

    @Then("I see the new shipment form")
    public void iSeeTheNewShipmentForm() {
        assertThat(ctx.newShipmentPage().waitUntilReady().formAvailable()).isTrue();
    }

    @Then("the new shipment form is {availability}")
    public void theNewShipmentFormIs(boolean available) {
        boolean formShown = ctx.newShipmentPage().waitUntilReady().formAvailable();
        assertThat(formShown).isEqualTo(available);
        if (!available) {
            assertThat(ctx.newShipmentPage().forbiddenMessage()).isEqualTo("You do not have permission to create shipments.");
        }
    }

    @Then("the {string} field shows the error {string}")
    public void theFieldShowsTheError(String field, String message) {
        assertThat(ctx.newShipmentPage().hasFormError()).isTrue();
        assertThat(ctx.newShipmentPage().errorFor(field)).isEqualTo(message);
    }

    @Then("the shipment is not created")
    public void theShipmentIsNotCreated() {
        assertThat(ctx.newShipmentPage().currentPath()).isEqualTo("/new");
    }

    @Then("the API returns the same shipment")
    public void theApiReturnsTheSameShipment() {
        String id = queryParam(ctx.driver().getCurrentUrl(), "id");
        Response res = ShipDeskApi.getShipment(Persona.OPERATOR, id);
        assertThat(res.statusCode()).isEqualTo(200);
        Shipment shipment = res.as(Shipment.class);
        assertThat(shipment.reference()).isEqualTo(submittedReference);
        assertThat(shipment.status()).isEqualTo("LABEL_CREATED");
        assertThat(shipment.weightOz()).isEqualTo(12.5);
    }

    private static String queryParam(String url, String name) {
        for (String pair : URI.create(url).getQuery().split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv[0].equals(name)) return kv[1];
        }
        throw new IllegalStateException("No " + name + " in " + url);
    }
}
