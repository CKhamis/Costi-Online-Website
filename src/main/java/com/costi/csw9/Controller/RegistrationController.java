package com.costi.csw9.Controller;

import com.costi.csw9.Model.FlashMessage;
import com.costi.csw9.Service.RegistrationRequest;
import com.costi.csw9.Service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

    @PostMapping(path = "api/v1/registration/new")
    public String register(@RequestBody RegistrationRequest request){
        if(request.getRole().equals("ADMIN")){
            return registrationService.registerAdmin(request);
        }
        return registrationService.registerUser(request);
    }

    @GetMapping(path = "api/v1/registration/confirm")
    public RedirectView confirmAdmin(@RequestParam("token") String token, RedirectAttributes redirectAttributes){
        registrationService.confirmToken(token);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Success", "Your account was activated!", FlashMessage.Status.SUCCESS));
        return new RedirectView("/", true);
    }
}
