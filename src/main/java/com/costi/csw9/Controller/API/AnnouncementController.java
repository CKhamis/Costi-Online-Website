package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Service.AnnouncementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/announcements")
public class AnnouncementController {
    private AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Announcement>> getPublicAnnouncements() {
        return ResponseEntity.ok(announcementService.findAllAnnouncements());
    }

    @GetMapping("/view")
    public ResponseEntity<Announcement> getAnnouncementById(@RequestParam("id") Long id) {
        try {
            Announcement announcement = announcementService.findById(id);
            if (announcement.isEnable()) {
                return ResponseEntity.ok(announcement);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}