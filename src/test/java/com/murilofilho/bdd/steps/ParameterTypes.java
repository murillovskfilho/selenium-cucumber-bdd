package com.murilofilho.bdd.steps;

import com.murilofilho.bdd.data.Persona;
import io.cucumber.java.ParameterType;

/** Custom Cucumber parameter types, so steps receive domain values instead of raw strings. */
public class ParameterTypes {

    /** "available" -> true, "not available" -> false. */
    @ParameterType("available|not available")
    public boolean availability(String text) {
        return text.equals("available");
    }

    /** A quoted persona name, e.g. "globex operator". */
    @ParameterType("\"(admin|operator|viewer|globex operator|locked user)\"")
    public Persona persona(String label) {
        return Persona.fromLabel(label);
    }
}
