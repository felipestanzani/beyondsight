package com.felipestanzani.beyondsight.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Location(@JsonProperty String file, @JsonProperty int line, @JsonProperty int column) {}