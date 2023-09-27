package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Service.AnnouncementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

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
    public ResponseEntity<?> getAnnouncementById(@RequestParam("id") Long id) {
        try {
            Announcement announcement = announcementService.findById(id);
            return ResponseEntity.ok(announcement);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Announcement not Found", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (AccessDeniedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Post not Public", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Getting Announcement", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }
}