package com.murilofilho.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.murilofilho.bdd.api.ShipDeskApi;
import com.murilofilho.bdd.context.ScenarioContext;
import com.murilofilho.bdd.data.Persona;
import com.murilofilho.bdd.data.ShipmentData;
import com.murilofilho.bdd.pages.ShipmentsPage.Row;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;

/** Arranging shipments through the API, and searching for them in the list. */
public class ShipmentSteps {

    private final ScenarioContext ctx;

    public ShipmentSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @Given("a shipment created by {persona}")
    public void aShipmentCreatedBy(Persona persona) {
        ctx.shipment(ShipDeskApi.createShipmentOk(persona, ShipmentData.valid()));
    }

    @Given("a shipment created by {persona} with:")
    public void aShipmentCreatedByWith(Persona persona, Map<String, String> fields) {
        Map<String, Object> body = ShipmentData.valid();
        fields.forEach((label, value) -> body.put(ShipmentData.fieldForLabel(label), value));
        ctx.shipment(ShipDeskApi.createShipmentOk(persona, body));
    }

    @When("I search for that shipment's reference")
    public void iSearchForThatShipmentsReference() {
        ctx.shipmentsPage().open().searchFor(ctx.shipment().reference());
    }

    @When("I search for {string}")
    public void iSearchFor(String text) {
        ctx.shipmentsPage().open().searchFor(text);
    }

    @When("I filter the list by status {string}")
    public void iFilterTheListByStatus(String status) {
        ctx.shipmentsPage().open().filterByStatus(status);
    }

    @When("I go to the next page")
    public void iGoToTheNextPage() {
        ctx.shipmentsPage().nextPage();
    }

    @Then("the list shows only that shipment with:")
    public void theListShowsOnlyThatShipmentWith(Map<String, String> expected) {
        List<Row> rows = ctx.shipmentsPage().rows();
        assertThat(rows).hasSize(1);
        Row row = rows.getFirst();
        assertThat(row.id()).isEqualTo(ctx.shipment().id());
        assertThat(row.reference()).isEqualTo(ctx.shipment().reference());
        expected.forEach((column, value) -> assertThat(column(row, column)).as(column).isEqualTo(value));
    }

    @Then("every shipment in the list has the status {string}")
    public void everyShipmentHasTheStatus(String status) {
        List<Row> rows = ctx.shipmentsPage().rows();
        assertThat(rows).isNotEmpty().allSatisfy(r -> assertThat(r.status()).isEqualTo(status));
    }

    @Then("I see the message {string}")
    public void iSeeTheMessage(String message) {
        assertThat(ctx.shipmentsPage().emptyStateText()).isEqualTo(message);
        assertThat(ctx.shipmentsPage().rows()).isEmpty();
    }

    @Then("I see {string} with {int} shipments")
    public void iSeePageWithShipments(String pageInfo, int count) {
        assertThat(ctx.shipmentsPage().pageInfo()).isEqualTo(pageInfo);
        assertThat(ctx.shipmentsPage().rows()).hasSize(count);
    }

    private static String column(Row row, String column) {
        return switch (column) {
            case "ID" -> row.id();
            case "Reference" -> row.reference();
            case "Recipient" -> row.recipient();
            case "Carrier" -> row.carrier();
            case "Status" -> row.status();
            case "Created" -> row.created();
            default -> throw new IllegalArgumentException("Unknown column: " + column);
        };
    }
}
