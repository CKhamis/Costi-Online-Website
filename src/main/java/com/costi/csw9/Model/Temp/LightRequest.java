package com.costi.csw9.Model.Temp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
/***
 * DTO for communication between Costi Online Light Nodes and Costi Online Services
 */
public class LightRequest {
    private String label, color, pattern;
    @NotNull
    private String address;

    public LightRequest(String address, String label, String color, String pattern) {
        this.label = label;
        this.color = color;
        this.pattern = pattern;
        this.address = address;
    }

    @Override
    public String toString() {
        return "{address: " + label +", label:" + label + ", color: " + color + ", pattern: " + pattern + "}";
    }
}
