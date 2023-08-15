package com.costi.csw9.Controller.API;

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

    @GetMapping("/api/v1/LED/{id}/logs")
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
    @GetMapping("/api/v1/LED/{id}/sync-up")
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

    /*
        Light Database Operations
     */

    @PostMapping("/api/v1/LED/edit")
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
}
