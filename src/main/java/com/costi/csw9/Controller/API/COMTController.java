package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.*;
import com.costi.csw9.Model.Temp.AccountNotificationRequest;
import com.costi.csw9.Service.COMTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.rmi.ConnectIOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<Announcement>> getAnnouncements() {
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

    /*
        Newsroom
     */

    @GetMapping("/newsroom/view/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id) {
        try {
            Post post = comtService.findPostById(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/newsroom/all")
    public ResponseEntity<List<Post>> getPosts(){
        List<Post> posts = comtService.findAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/newsroom/analytics")
    @ResponseBody
    public Map<String, Object> getPostAnalytics(){
        Map<String, Object> jsonResponse = new HashMap<>();
        List<Post> allPosts = comtService.findAllPosts();
        jsonResponse.put("totalPosts", allPosts.size());

        int numViews = 0, numEnabled = 0, numDisabled = 0, numPublic = 0, bodyLength = 0;
        LocalDateTime oldestPost = oldestPost = LocalDateTime.MAX;

        for(Post post : allPosts){
            numViews += post.getViews();
            bodyLength += post.getBody().length();

            if(post.isEnabled()){
                numEnabled ++;
            }else{
                numDisabled ++;
            }

            if(post.isPublic()){
                numPublic ++;
            }

            if(post.getLastEdited().isBefore(oldestPost)){
                oldestPost = post.getLastEdited();
            }
        }

        if(allPosts.size() == 0){
            jsonResponse.put("oldestPost", "?");
        }else{
            jsonResponse.put("oldestPost", ChronoUnit.DAYS.between(oldestPost, LocalDateTime.now()) + "d");
        }

        jsonResponse.put("totalViews", numViews);
        jsonResponse.put("totalEnabled", numEnabled);
        jsonResponse.put("totalDisabled", numDisabled);
        jsonResponse.put("totalPublic", numPublic);

        if(allPosts.size() == 0){
            jsonResponse.put("averageBodyLength", "?");
        }else{
            jsonResponse.put("averageBodyLength", bodyLength / allPosts.size());
        }

        return jsonResponse;
    }

    @PostMapping("/newsroom/delete")
    public ResponseEntity<String> deletePost(@RequestBody Long id) {
        try {
            comtService.deletePost(id);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting post: " + e.getMessage());
        }
    }

    @PostMapping("/newsroom/save")
    public ResponseEntity<?> savePost(@Valid @ModelAttribute Post post, BindingResult bindingResult, @RequestParam(value = "image", required = false) MultipartFile file) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            String errorMessages = fieldErrors.stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation errors: " + errorMessages);
        }

        try {
            if (file != null && !file.isEmpty()) {
                comtService.savePost(post, file);
            } else {
                comtService.savePost(post);
            }
            return ResponseEntity.status(HttpStatus.OK).body("Post Saved");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving post: " + e.getMessage());
        }
    }

    /*
        Notifications
     */

    @GetMapping("/notifications/all")
    public ResponseEntity<List<AccountNotification>> getNotifications(){
        List<AccountNotification> notifications = comtService.findAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/notifications/save")
    public ResponseEntity<String> saveNotification(@RequestBody AccountNotificationRequest request) {
        try{
            comtService.saveNotification(request);
            return ResponseEntity.ok("Notification saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving notification: " + e.getMessage());
        }
    }

    @PostMapping("/notifications/delete")
    public ResponseEntity<String> deleteNotification(@RequestBody Long id) {
        try {
            comtService.deleteNotification(id);
            return ResponseEntity.ok("Notification deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting notification: " + e.getMessage());
        }
    }

    /*
        Lights
     */

    @GetMapping("/light/all")
    public ResponseEntity<List<Light>> getLights(){
        List<Light> lights = comtService.findAllLights();
        return ResponseEntity.ok(lights);
    }

    @PostMapping("/light/save")
    public ResponseEntity<String> saveLight(@RequestBody Light light) {
        try {
            String result = comtService.saveLight(light);
            return ResponseEntity.ok(result);
        } catch (ConnectIOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding/updating light: " + e.getMessage());
        }
    }

    @PostMapping("/light/delete")
    public ResponseEntity<String> deleteLight(@RequestBody Long id) {
        try {
            comtService.deleteLight(id);
            return ResponseEntity.ok("Light deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting light: " + e.getMessage());
        }
    }

    @PostMapping("/light/sync-up")
    public ResponseEntity<String> syncLightUp(@RequestBody Light light) {
        try {
            comtService.syncUp(light);
            return ResponseEntity.ok("Light saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving settings to light: " + e.getMessage());
        }
    }

    @PostMapping("/light/sync-down")
    public ResponseEntity<String> syncLightDown(@RequestBody Light light) {
        try {
            comtService.syncDown(light);
            return ResponseEntity.ok("Light saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving settings to light: " + e.getMessage());
        }
    }

    /*
        Users
     */

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getUsers(){
        List<User> users = comtService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}
