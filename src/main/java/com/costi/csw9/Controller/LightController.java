package com.costi.csw9.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class LightController {
    @PostMapping("/update-light")
    @ResponseBody
    public String handlePostRequest(@RequestBody String requestData, Principal principal) {
        // Process the received data
        System.out.println("Received data: " + requestData);

        // Perform necessary operations and return a response
        String response = "Data received successfully";
        return response;
    }
}
