package com.felipestanzani.beyondsight.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Symbol(@JsonProperty String name, @JsonProperty String kind,
                     @JsonProperty Location location, @JsonProperty String signature,
                     @JsonProperty String doc) {
}