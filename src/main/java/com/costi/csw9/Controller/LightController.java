package com.costi.csw9.Controller;

import com.costi.csw9.Model.Light;
import com.costi.csw9.Model.LightLog;
import com.costi.csw9.Model.Temp.EditLightRequest;
import com.costi.csw9.Model.Temp.LightRequest;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Service.LightService;
import com.costi.csw9.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/api/v1/LED/all-lights")
    public ResponseEntity<List<Light>> getAllLights(Principal principal) {
        User user = getCurrentUser(principal);
        if (user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)) {
            return ResponseEntity.ok(lightService.getAllLights());
        }
        return ResponseEntity.ok(lightService.getPublicLights(true));
    }

    @GetMapping("/api/v1/LED/analytics")
    @ResponseBody
    public Map<String, Object> getLEDAnalytics() {
        // Construct the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        List<Light> lightList = lightService.getAllLights();
        jsonResponse.put("totalLights", lightList.size());


        int numFavorite = 0, numPublic = 0, numPrivate = 0, numEnabled = 0, numDisabled = 0, numUsedToday = 0;
        for(Light light : lightList){
            LocalDate currentDate = LocalDate.now();
            LocalDate lightDate = (light.getLastConnected() != null) ? light.getLastConnected().toLocalDate() : LocalDate.MIN;
            if(currentDate.equals(lightDate)){
                numUsedToday++;
            }

            if(light.isFavorite()){
                numFavorite++;
            }

            if(light.isEnabled()){
                numEnabled++;
            }else{
                numDisabled++;
            }

            if(light.isPublic()){
                numPublic++;
            }else{
                numPrivate++;
            }
        }

        jsonResponse.put("favoriteCount", numFavorite);
        jsonResponse.put("publicCount", numPublic);
        jsonResponse.put("privateCount", numPrivate);
        jsonResponse.put("enabledCount", numEnabled);
        jsonResponse.put("disabledCount", numDisabled);
        jsonResponse.put("usedTodayCount", numUsedToday);

        return jsonResponse;
    }

    @GetMapping("/api/v1/LED/{id}/Logs")
    public ResponseEntity<List<LightLog>> getLightStatus(@PathVariable Long id, Principal principal) {
        try{
            Light light = lightService.getLightById(id);
            if(light.isPublic() || getCurrentUser(principal).isOwner() || getCurrentUser(principal).isAdmin()){
                return ResponseEntity.ok(light.getLogs());
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    /*
        Light Connection
     */
    @GetMapping("/api/v1/LED/{id}/Sync-Up")
    public ResponseEntity<?> syncUp(@PathVariable Long id){
        try {
            Light light = lightService.getLightById(id);
            String status = lightService.syncUp(light);

            if(status.charAt(0) == 'C'){
                return ResponseEntity.ok(status);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error sending data: " + status);
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/v1/LED/{id}/Sync-Down")
    public ResponseEntity<?> syncDown(@PathVariable Long id){
        try {
            Light light = lightService.getLightById(id);
            String status = lightService.syncDown(light);

            if(status.charAt(0) == 'C'){
                return ResponseEntity.ok(status);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error receiving data: " + status);
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    /*
        Light Database Operations
     */

    @PostMapping("/api/v1/LED/new")
    public ResponseEntity<?> createNewLight(@RequestBody LightRequest lightRequest, Principal principal) {
        if(getCurrentUser(principal).isOwner() || getCurrentUser(principal).isAdmin()){
            try {
                Light light = new Light(lightRequest);
                Light savedLight = lightService.saveLight(light);
                return ResponseEntity.ok("Light added/updated successfully. Light ID: " + savedLight.getId());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding/updating light: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/Edit")
    public ResponseEntity<?> editLight(@RequestBody EditLightRequest lightRequest, Principal principal) {
        try {
            Light light = lightService.getLightById(lightRequest.getId());
            User user = getCurrentUser(principal);
            if(user.isOwner() || user.isAdmin() || (light.isPublic() && light.isEnabled())){
                light.setValues(lightRequest);
                light.setLastModified(LocalDateTime.now());
                Light savedLight = lightService.saveLight(light);

                //Upload data to light
                String status = lightService.syncUp(savedLight);

                if(status.charAt(0) == 'C'){
                    return ResponseEntity.ok("Light added/updated successfully. Light ID: " + savedLight.getId());
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error sending data: " + status);
                }
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding/updating light: " + e.getMessage());
        }
    }

    @PostMapping("/api/v1/LED/{id}/Delete")
    public ResponseEntity<String> deleteLight(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                lightService.deleteLightById(id);
                return ResponseEntity.ok("Light deleted successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting light: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/{id}/Enable")
    public ResponseEntity<String> enableLight(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                Light light = lightService.getLightById(id);
                light.setEnabled(true);
                lightService.saveLight(light);
                return ResponseEntity.ok("Light enabled successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error enabling light: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/{id}/Disable")
    public ResponseEntity<String> disableLight(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                Light light = lightService.getLightById(id);
                light.setEnabled(false);
                lightService.saveLight(light);
                return ResponseEntity.ok("Light disabled successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error disabling light: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/{id}/Publish")
    public ResponseEntity<String> publishLight(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                Light light = lightService.getLightById(id);
                light.setPublic(true);
                lightService.saveLight(light);
                return ResponseEntity.ok("Light published successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error publishing light: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/{id}/Private")
    public ResponseEntity<String> privateLight(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                Light light = lightService.getLightById(id);
                light.setPublic(false);
                lightService.saveLight(light);
                return ResponseEntity.ok("Light privated successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error privating light: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/{id}/Favorite")
    public ResponseEntity<String> favoriteLight(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                Light light = lightService.getLightById(id);
                light.setFavorite(true);
                lightService.saveLight(light);
                return ResponseEntity.ok("Light favorited successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing light: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/{id}/Disfavorite")
    public ResponseEntity<String> disfavoriteLight(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                Light light = lightService.getLightById(id);
                light.setFavorite(false);
                lightService.saveLight(light);
                return ResponseEntity.ok("Light disfavorited successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing light: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/DisableAll")
    public ResponseEntity<String> disableAll(Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                for(Light light : lightService.getEnabledLights(true)){
                    light.setEnabled(false);
                    lightService.saveLight(light);
                }
                return ResponseEntity.ok("Lights disabled successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing lights: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/v1/LED/PrivateAll")
    public ResponseEntity<String> privateAll(Principal principal) {
        User user = getCurrentUser(principal);
        if(user.getRole().equals(UserRole.ADMIN) || getCurrentUser(principal).getRole().equals(UserRole.OWNER)){
            try {
                for(Light light : lightService.getPublicLights(true)){
                    light.setPublic(false);
                    lightService.saveLight(light);
                }
                return ResponseEntity.ok("Lights privated successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing lights: " + e.getMessage());
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
