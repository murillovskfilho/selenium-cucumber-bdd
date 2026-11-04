package com.murilofilho.bdd.data;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shipment input as the API expects it. Every builder call gets a unique reference, so parallel
 * scenarios never collide and no cleanup is needed; every other field is fixed and readable.
 */
public final class ShipmentData {

    private static final SecureRandom RANDOM = new SecureRandom();

    private ShipmentData() {}

    public static String uniqueReference() {
        byte[] bytes = new byte[4];
        RANDOM.nextBytes(bytes);
        return ("BDD-" + Long.toString(System.currentTimeMillis(), 36) + "-" + HexFormat.of().formatHex(bytes)).toUpperCase();
    }

    public static Map<String, Object> valid() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("reference", uniqueReference());
        data.put("carrier", "UPS");
        data.put("serviceLevel", "GROUND");
        data.put("recipientName", "Grace Hopper");
        data.put("destinationZip", "94105");
        data.put("weightOz", 24);
        return data;
    }

    /** Maps the form labels used in feature files to API field names. */
    public static String fieldForLabel(String label) {
        return switch (label.trim()) {
            case "Reference" -> "reference";
            case "Carrier" -> "carrier";
            case "Service level" -> "serviceLevel";
            case "Recipient name" -> "recipientName";
            case "Destination ZIP" -> "destinationZip";
            case "Weight (oz)" -> "weightOz";
            default -> throw new IllegalArgumentException("Unknown shipment field: " + label);
        };
    }
}
