package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.DynamicContent;
import com.costi.csw9.Service.DynamicContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/content")
public class DynamicContentController {
    private final DynamicContentService dynamicContentService;

    public DynamicContentController(DynamicContentService dynamicContentService){
        this.dynamicContentService = dynamicContentService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getDynamicContent() {
        try{
            DynamicContent content = dynamicContentService.getContent();
            return ResponseEntity.ok(content);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error getting dynamic content", ResponseMessage.Severity.LOW, "Error retrieving content: " + e.getMessage()));
        }
    }
}
