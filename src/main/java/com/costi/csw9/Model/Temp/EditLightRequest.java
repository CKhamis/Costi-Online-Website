package com.costi.csw9.Model.Temp;

import lombok.Getter;

@Getter
public class EditLightRequest extends LightRequest{
    private Long id;
    public EditLightRequest(Long id, String label, String color, String pattern) {
        super(label, color, pattern);
        this.id = id;
    }
}
