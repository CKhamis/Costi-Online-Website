package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Service.AnnouncementService;
import com.costi.csw9.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/announcements")
public class AnnouncementController {
    private AnnouncementService announcementService;

    /**
     * Constructs an instance of AnnouncementController.
     *
     * @param announcementService the AnnouncementService used for retrieving announcements
     * @param userService the UserService used for retrieving user information
     */
    public AnnouncementController(AnnouncementService announcementService, UserService userService) {
        this.announcementService = announcementService;
    }

    /**
     * Retrieves a list of public announcements.
     *
     * @return the ResponseEntity containing the list of public announcements
     */
    @GetMapping("/public-announcements")
    public ResponseEntity<List<Announcement>> getPublicAnnouncements() {
        return ResponseEntity.ok(announcementService.findByApproval(true));
    }

    /**
     * Retrieves an announcement by its ID.
     *
     * @param id the ID of the announcement
     * @return the ResponseEntity containing the announcement if found and enabled, or not found if not found or not enabled
     */
    @GetMapping("/{id}")
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.findById(id);
            if (announcement.isEnable()) {
                return ResponseEntity.ok(announcement);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}