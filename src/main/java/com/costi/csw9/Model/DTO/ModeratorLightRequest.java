package com.costi.csw9.Model.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModeratorLightRequest extends LightRequest{
    private boolean isFavorite, isPublic, isEnabled;
    private Long Id;
}
