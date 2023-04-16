package com.costi.csw9.Service;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Model.Temp.ConfirmationToken;
import com.costi.csw9.Validation.EmailValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final EmailValidator emailValidator;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;

    public String registerAdmin(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }

        String token;
        System.out.println(userService.isEmpty());
        if(userService.isEmpty()){
            token = userService.signUpAdmin(
                    new User(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            UserRole.OWNER
                    )
            );
        }else{
            token = userService.signUpAdmin(
                    new User(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            UserRole.ADMIN

                    )
            );
        }

        String link = "http://localhost/api/v1/registration/confirm?token=" + token;
        System.out.println("Verification link for " + request.getFirstName() + " " + request.getLastName() + ":\t" + link);

        return token;
    }

    /**
     * Creation of a regular user, which does not need verification link
     * @param request the incoming request for user
     * @return status message
     */
    public String registerUser(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }

        //Create user
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                UserRole.USER
        );
        user.setEnabled(true);

        //Save user
        userService.signUpUser(user);

        System.out.println("User " + request.getFirstName() + " " + request.getLastName() + " was created and enabled!");
        return "User was added";
    }

    //GUI based creation
    public void registerUser(User user) {
        boolean isValidEmail = emailValidator.test(user.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }

        //Additional security measure
        user.setRole(UserRole.USER);

        userService.signUpUser(user);
        System.out.println("User " + user.getFirstName() + " " + user.getLastName() + " was created and enabled!");
    }

    public void registerAdmin(User user) {
        if(userService.isEmpty()){
            user.setRole(UserRole.OWNER);
        }


        boolean isValidEmail = emailValidator.test(user.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }

        String token = userService.signUpAdmin(user);

        String link = "http://localhost/api/v1/registration/confirm?token=" + token;
        System.out.println("Verification link for " + user.getFirstName() + " " + user.getLastName() + ":\t" + link);
    }

    @Transactional
    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enable(confirmationToken.getUser(), true);
    }
}
