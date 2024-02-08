package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.DynamicContent;
import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.AccountLogRepository;
import com.costi.csw9.Service.DynamicContentService;
import com.costi.csw9.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/content")
public class DynamicContentController {
    private DynamicContentService dynamicContentService;

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
