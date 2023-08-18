package com.costi.csw9.Model.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserLightRequest extends LightRequest {
    @NotNull
    private Long id;
}
