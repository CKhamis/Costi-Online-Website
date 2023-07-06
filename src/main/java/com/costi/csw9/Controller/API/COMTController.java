package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Service.COMTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/management")
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
public class COMTController {
    private final COMTService comtService;

    public COMTController(COMTService comtService) {
        this.comtService = comtService;
    }

    /*
        Announcements
     */

    @GetMapping("/announcement/view")
    public ResponseEntity<Announcement> getAnnouncementById(@RequestParam("id") Long id) {
        try {
            Announcement announcement = comtService.findAnnouncementById(id);
            return ResponseEntity.ok(announcement);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //FIXME: the newest date thing doesn't work
    @GetMapping("/announcement/analytics")
    @ResponseBody
    public Map<String, Object> getAnnouncementAnalytics(){
        Map<String, Object> jsonResponse = new HashMap<>();
        List<Announcement> allAnnouncements = comtService.findAllAnnouncements();
        jsonResponse.put("totalAnnouncements", allAnnouncements.size());

        int numEnabled = 0, numDisabled = 0, numBodyCharacters = 0;
        LocalDateTime oldest = LocalDateTime.MAX, newest = LocalDateTime.MIN;
        for(Announcement announcement : allAnnouncements){
            if(announcement.isEnable()){
                numEnabled++;
            }else{
                numDisabled++;
            }

            if(announcement.getDate().isBefore(oldest)){
                oldest = announcement.getDate();
            }

            if(announcement.getDate().isAfter(newest)){
                newest = announcement.getDate();
            }

            numBodyCharacters += announcement.getBody().length();
        }

        jsonResponse.put("enabledCount", numEnabled);
        jsonResponse.put("disabledCount", numDisabled);


        if(allAnnouncements.size() == 0){
            jsonResponse.put("averageBodyLength", "?");
            jsonResponse.put("oldestAnnouncement", "?");
            jsonResponse.put("newestAnnouncement", "?");
        }else{
            jsonResponse.put("averageBodyLength", numBodyCharacters / allAnnouncements.size());
            jsonResponse.put("oldestAnnouncement", ChronoUnit.DAYS.between(oldest, LocalDateTime.now()) + "d");
            jsonResponse.put("newestAnnouncement", ChronoUnit.DAYS.between(newest, LocalDateTime.now()) + "d");
        }

        return jsonResponse;
    }

    @GetMapping("/announcement/all")
    public ResponseEntity<List<Announcement>> getAnnouncementsByApproval() {
        List<Announcement> announcements = comtService.findAllAnnouncements();
        return ResponseEntity.ok(announcements);
    }

    @PostMapping("/announcement/save")
    public ResponseEntity<String> saveAnnouncement(@RequestBody Announcement announcement) {
        try {
            if(announcement.getId() > 0){
                comtService.saveAnnouncement(announcement);
            }else{
                comtService.saveAnnouncement(new Announcement(announcement));
            }
            return ResponseEntity.ok("Announcement saved successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving announcement: " + e.getMessage());
        }
    }

    @PostMapping("/announcement/delete")
    public ResponseEntity<String> deleteAnnouncement(@RequestBody Long id) {
        try {
            comtService.deleteAnnouncement(id);
            return ResponseEntity.ok("Announcement deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting announcement: " + e.getMessage());
        }
    }
}
