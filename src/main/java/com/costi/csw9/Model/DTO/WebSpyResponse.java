package com.costi.csw9.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class WebSpyResponse {
    boolean is_blocked;
    String message;
    String expire;
}
