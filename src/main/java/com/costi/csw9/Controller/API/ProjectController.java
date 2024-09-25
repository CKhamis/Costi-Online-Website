package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.Ajax.ProjectInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.costi.csw9.Cache.Projects;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/api/project")
public class ProjectController {

    public ProjectController() {
        Projects.refreshProjects();
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ProjectInfo>> getProjects() {
        return ResponseEntity.ok(Projects.projectList.values());
    }

    @GetMapping("/view/{name}")
    public ResponseEntity<ProjectInfo> getProjects(@PathVariable String name) {
        return ResponseEntity.ok(Projects.projectList.get(name));
    }
}
