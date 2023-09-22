package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.DTO.UserAccountRequest;
import com.costi.csw9.Model.User;
import com.costi.csw9.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/api/user")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService){
        this.userService = userService;
    }

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


    private User getCurrentUser(Principal principal){
        if (principal == null) {
            return null;
        }
        String username = principal.getName();
        User u = userService.loadUserByUsername(username);
        return u;
    }
}
