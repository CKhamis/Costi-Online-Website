package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/management")
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
public class COMTController {
    private AnnouncementService announcementService;

    public COMTController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    // Announcements

    @GetMapping("/announcement/{id}")
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.findById(id);
            return ResponseEntity.ok(announcement);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/announcement/get-announcements")
    public ResponseEntity<List<Announcement>> findByApproval(@RequestParam("enabled") boolean enabled) {
        // To get all enabled announcements: GET /api/announcements/announcement?enabled=true
        List<Announcement> announcements = announcementService.findByApproval(enabled);
        return ResponseEntity.ok(announcements);
    }

//    @PostMapping("announcement/enable")
//    public ResponseEntity<?> editAnnouncement(@RequestBody Announcement announcement){
//        announcementService.save(announcement);
//    }
}
