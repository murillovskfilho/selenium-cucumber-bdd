package com.murilofilho.bdd.data;

import java.util.Arrays;

/** The people scenarios act as. Accounts and password come from ShipDesk's demo seed. */
public enum Persona {
    ADMIN("admin", "admin@acme.test", "Ada Admin"),
    OPERATOR("operator", "operator@acme.test", "Otto Operator"),
    VIEWER("viewer", "viewer@acme.test", "Vera Viewer"),
    GLOBEX_OPERATOR("globex operator", "operator@globex.test", "Gus Globex"),
    LOCKED("locked user", "locked@acme.test", "Lou Locked");

    /** Demo-only password of the local ShipDesk app (see app/src/seed.ts in the app repo). */
    public static final String PASSWORD = "Demo-Passw0rd!";

    public final String label;
    public final String email;
    public final String displayName;

    Persona(String label, String email, String displayName) {
        this.label = label;
        this.email = email;
        this.displayName = displayName;
    }

    /** Resolves the human wording used in feature files, e.g. "globex operator". */
    public static Persona fromLabel(String label) {
        return Arrays.stream(values())
                .filter(p -> p.label.equalsIgnoreCase(label.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown persona: " + label));
    }
}
