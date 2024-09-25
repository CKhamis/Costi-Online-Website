package com.costi.csw9.Controller.API;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.costi.csw9.Cache.Projects;

import java.util.List;

@Controller
@RequestMapping("/api/project")
public class ProjectController {
    @GetMapping("/all")
    public ResponseEntity<List<String>> getProjects() {
        Projects.refreshProjects();
        return null;
    }
}
