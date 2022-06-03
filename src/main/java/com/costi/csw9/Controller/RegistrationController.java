package com.costi.csw9.Controller;

import com.costi.csw9.Model.FlashMessage;
import com.costi.csw9.Service.RegistrationRequest;
import com.costi.csw9.Service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request){
        return registrationService.registerAdmin(request);
    }

    @GetMapping(path = "confirm")
    public RedirectView confirm(@RequestParam("token") String token, RedirectAttributes redirectAttributes){
        registrationService.confirmToken(token);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Success", "Your account was activated!", FlashMessage.Status.SUCCESS));
        return new RedirectView("/", true);
    }
}
