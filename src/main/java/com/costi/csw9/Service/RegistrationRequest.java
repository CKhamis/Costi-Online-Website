package com.costi.csw9.Service;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
/**
 * Gets the necessary params to create a User object for account creation
 */
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}
