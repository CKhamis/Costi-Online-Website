package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.DTO.UserAccountRequest;
import com.costi.csw9.Model.DTO.WikiRequest;
import com.costi.csw9.Model.FlashMessage;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Controller
@AllArgsConstructor
@RequestMapping("/api/user")
public class AccountController {
    private final UserService userService;

    private AuthenticationManager authenticationManager;


    @GetMapping("/current")
    public ResponseEntity<?> getUser(Principal principal) {
        User user = getCurrentUser(principal);
        if(user != null){
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("No user logged in", ResponseMessage.Severity.LOW, "Cannot return user information if there is no logged in user"));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteUser(Principal principal) {
        try {
            // Make it such that users can't edit other users
            User current = getCurrentUser(principal);

            userService.delete(current.getId());
            return ResponseEntity.ok(new ResponseMessage("Account Deleted", ResponseMessage.Severity.INFORMATIONAL, "User of id " + current.getId() + ", is no longer accessible or recoverable. Wiki Posts are re-assigned to owner account."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error deleting user", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage> saveUser(@RequestBody @Valid UserAccountRequest request, Principal principal) {
        try {
            // Make it such that users can't edit other users
            User current = getCurrentUser(principal);
            request.setId(current.getId());

            userService.save(request);
            return ResponseEntity.ok(new ResponseMessage("User Saved", ResponseMessage.Severity.INFORMATIONAL, "Your account has been saved to Costi Online"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving User", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @PostMapping("/new")
    public String newUser(@Valid UserAccountRequest request, RedirectAttributes redirectAttributes) {
        try{
            request.setId(null);
            userService.save(request); //TODO: Somehow can still save new users with non-unique emails
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Congratulations!", "Your new Costi Online Account has been created.", FlashMessage.Status.SUCCESS));
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/Account";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("OOOOPS! Generic error (awwww man)", "Make sure to use a unique email", FlashMessage.Status.DANGER));
        }
        //TODO: does not show you errors
        return "redirect:/SignUp";
    }

    private User getCurrentUser(Principal principal){
        if (principal == null) {
            return null;
        }
        String username = principal.getName();
        User u = userService.loadUserByUsername(username);
        return u;
    }
}
