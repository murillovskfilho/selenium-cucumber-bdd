package com.murilofilho.bdd.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Shipment(
        String id,
        String reference,
        String carrier,
        String serviceLevel,
        String recipientName,
        String destinationZip,
        double weightOz,
        String status,
        String createdAt,
        String createdBy) {}
