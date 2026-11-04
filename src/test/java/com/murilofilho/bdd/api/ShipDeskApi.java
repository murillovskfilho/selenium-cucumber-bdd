package com.murilofilho.bdd.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.murilofilho.bdd.config.Config;
import com.murilofilho.bdd.data.Persona;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REST Assured client for ShipDesk. Used to arrange data quickly ("Given a shipment exists")
 * and to double-check what the UI shows against the backend.
 */
public final class ShipDeskApi {

    // Tokens are cached per persona for the whole run: one login per persona, shared by threads.
    private static final Map<Persona, String> TOKENS = new ConcurrentHashMap<>();

    private ShipDeskApi() {}

    private static RequestSpecification spec() {
        return new RequestSpecBuilder().setBaseUri(Config.apiUrl()).setContentType(ContentType.JSON).build();
    }

    public static Response login(String email, String password) {
        return given(spec()).body(Map.of("email", email, "password", password)).post("/api/auth/login");
    }

    public static String token(Persona persona) {
        return TOKENS.computeIfAbsent(persona, p -> {
            Response res = login(p.email, Persona.PASSWORD);
            assertThat(res.statusCode()).as("login as %s: %s", p, res.asString()).isEqualTo(200);
            return res.path("token");
        });
    }

    private static RequestSpecification as(Persona persona) {
        return given(spec()).header("Authorization", "Bearer " + token(persona));
    }

    public static Response createShipment(Persona persona, Map<String, Object> body) {
        return as(persona).body(body).post("/api/shipments");
    }

    public static Shipment createShipmentOk(Persona persona, Map<String, Object> body) {
        Response res = createShipment(persona, body);
        assertThat(res.statusCode()).as(res.asString()).isEqualTo(201);
        return res.as(Shipment.class);
    }

    public static Response getShipment(Persona persona, String id) {
        return as(persona).get("/api/shipments/{id}", id);
    }

    public static Response cancelShipment(Persona persona, String id) {
        return as(persona).post("/api/shipments/{id}/cancel", id);
    }
}
