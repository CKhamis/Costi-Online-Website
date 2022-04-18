package com.costi.csw9.Service;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Validation.EmailValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {
    private EmailValidator emailValidator;
    private final UserService userService;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if(isValidEmail){
            return userService.signUpUser(new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), UserRole.USER));
        }else {
            throw new IllegalStateException("email invalid");
        }
    }
}
