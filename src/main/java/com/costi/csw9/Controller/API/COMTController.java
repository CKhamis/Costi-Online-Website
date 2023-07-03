package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/management")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ADMIN') or hasRole('OWNER')")
public class COMTController {
    private AnnouncementService announcementService;

    public COMTController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/all-announcements")
    public ResponseEntity<List<Announcement>> getAllAnnouncements(){

    }
}
