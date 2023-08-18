package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.DTO.UserLightRequest;
import com.costi.csw9.Model.Light;
import com.costi.csw9.Service.LightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.rmi.ConnectIOException;
import java.util.*;

@Controller
@RequestMapping("/api/light")
public class LightController {
    private LightService lightService;

    public LightController(LightService lightService){
        this.lightService = lightService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Light>> getLights(){
        List<Light> lights = lightService.findAllLights();
        return ResponseEntity.ok(lights);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editLight(@RequestBody @Valid UserLightRequest request) {
        try {
            String result = lightService.saveLight(request);
            return ResponseEntity.ok(result);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConnectIOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding/updating light: " + e.getMessage());
        }
    }
}
