package com.costi.csw9.Controller;

import com.costi.csw9.Model.Light;
import com.costi.csw9.Model.Post;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Service.LightService;
import com.costi.csw9.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class LightController {
    private LightService lightService;
    private UserService userService;

    public LightController(LightService lightService, UserService userService){
        this.lightService = lightService;
        this.userService = userService;
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            return new User("NULL", "NULL", "Not Signed In", "error", UserRole.USER);
        }
        String username = principal.getName();
        User u = userService.findByEmail(username);
        return u;
    }
    @PostMapping("/update-light")
    @ResponseBody
    public String handlePostRequest(@RequestBody String requestData, Principal principal) {
        // Process the received data
        System.out.println("Received data: " + requestData);

        // Perform necessary operations and return a response
        String response = "Data received successfully";
        return response;
    }

    @GetMapping("/api/v1/LED/all-lights")
    public ResponseEntity<List<Light>> getAllLights(Principal principal) {
        User user = getCurrentUser(principal);
        if (user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)) {
            return ResponseEntity.ok(lightService.getAllLights());
        }
        return ResponseEntity.ok(lightService.getEnabledLights(true));
    }

    @GetMapping("/api/v1/LED/{id}")
    public ResponseEntity<Light> getLight(@PathVariable Long id, Principal principal) {
        try{
            Light light = lightService.getLightById(id);
            if(light.isPublic()){
                return ResponseEntity.ok(light);
            }else{
                if(getCurrentUser(principal).isOwner() || getCurrentUser(principal).isAdmin()){
                    return ResponseEntity.ok(light);
                }else{
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/v1/LED/new")
    public ResponseEntity<String> addOrUpdateLight(@RequestBody Light light) {
        try {
            Light savedLight = lightService.saveLight(light);
            return ResponseEntity.ok("Light added/updated successfully. Light ID: " + savedLight.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding/updating light: " + e.getMessage());
        }
    }
}
