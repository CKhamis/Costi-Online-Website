package com.costi.csw9.Model.Temp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LightRequest {
    private String label, color, pattern;

    public LightRequest(String label, String color, String pattern) {
        this.label = label;
        this.color = color;
        this.pattern = pattern;
    }
}
