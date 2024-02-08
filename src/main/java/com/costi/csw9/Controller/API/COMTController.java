package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.*;
import com.costi.csw9.Model.DTO.*;
import com.costi.csw9.Service.COMTService;
import com.costi.csw9.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.rmi.ConnectIOException;
import java.security.Principal;
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
    private final UserService userService;

    public COMTController(COMTService comtService, UserService userService) {
        this.comtService = comtService;
        this.userService = userService;
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
    public ResponseEntity<ResponseMessage> saveAnnouncement(@RequestBody Announcement announcement) {
        try {
            if(announcement.getId() > 0){
                comtService.saveAnnouncement(announcement);
            }else{
                comtService.saveAnnouncement(new Announcement(announcement));
            }
            return ResponseEntity.ok(new ResponseMessage("Announcement Saved", ResponseMessage.Severity.INFORMATIONAL, "Announcement settings are saved to Costi Online"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Saving Announcement", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/announcement/delete")
    public ResponseEntity<ResponseMessage> deleteAnnouncement(@RequestBody Long id) {
        try {
            comtService.deleteAnnouncement(id);
            return ResponseEntity.ok(new ResponseMessage("Announcement Deleted", ResponseMessage.Severity.INFORMATIONAL, "Announcement is no longer accessible nor recoverable."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Deleting Announcement", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    /*
        Newsroom
     */

    @GetMapping("/newsroom/view/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") Long id) {
        try {
            Post post = comtService.findPostById(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Post not Found", ResponseMessage.Severity.LOW, e.getMessage()));
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
    public ResponseEntity<ResponseMessage> deletePost(@RequestBody Long id) {
        try {
            comtService.deletePost(id);
            return ResponseEntity.ok(new ResponseMessage("Post Deleted", ResponseMessage.Severity.MEDIUM, "Post of ID " + id + " is no longer accessible nor recoverable."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Deleting Notification", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/newsroom/save")
    public ResponseEntity<ResponseMessage> savePost(@Valid @ModelAttribute Post post, BindingResult bindingResult, @RequestParam(value = "image", required = false) MultipartFile file) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            String errorMessages = fieldErrors.stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Creating Post", ResponseMessage.Severity.MEDIUM, errorMessages));
        }

        try {
            if (file != null && !file.isEmpty()) {
                comtService.savePost(post, file);
            } else {
                comtService.savePost(post);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Post Saved", ResponseMessage.Severity.INFORMATIONAL, "Post was saved to Costi Online."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Creating Post", ResponseMessage.Severity.MEDIUM, e.getMessage()));
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

    @GetMapping("/notifications/{id}")
    public ResponseEntity<?> getNotifications(@PathVariable Long id){
        try {
            List<AccountNotification> notifications = comtService.findAllNotificationsByUser(id);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Logs", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Retrieving Logs", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/notifications/save")
    public ResponseEntity<ResponseMessage> saveNotification(@RequestBody AccountNotificationRequest request) {
        try{
            comtService.saveNotification(request);
            return ResponseEntity.ok(new ResponseMessage("Notification Saved", ResponseMessage.Severity.INFORMATIONAL, "Post is now visible to user"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Saving Notification", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/notifications/delete")
    public ResponseEntity<ResponseMessage> deleteNotification(@RequestBody Long id) {
        try {
            comtService.deleteNotification(id);
            return ResponseEntity.ok(new ResponseMessage("Notification Deleted", ResponseMessage.Severity.INFORMATIONAL, "Notification is no longer accessible nor recoverable."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Deleting Notification", ResponseMessage.Severity.MEDIUM, e.getMessage()));
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

    @GetMapping("/light/{id}/logs")
    public ResponseEntity<?> getLogs(@PathVariable Long id){
        try{
            List<LightLog> lights = comtService.findLogs(id);
            return ResponseEntity.ok(lights);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Light Logs", ResponseMessage.Severity.LOW, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Retrieving Light Logs", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @PostMapping("/light/save")
    public ResponseEntity<ResponseMessage> saveLight(@RequestBody ModeratorLightRequest request) {
        try {
            comtService.saveLight(request);
            return ResponseEntity.ok(new ResponseMessage("Light Saved", ResponseMessage.Severity.INFORMATIONAL, "Light settings have been saved to Costi Online and uploaded to light"));
        } catch (ConnectIOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Saving Light", ResponseMessage.Severity.MEDIUM, "Light settings saved to Costi Online, but could not send data to Costi Online LED module"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Saving Light", ResponseMessage.Severity.LOW, "The specified ID of the request (" + request.getId() + ") could not be found on Costi Online."));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Light", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @PostMapping("/light/delete")
    public ResponseEntity<ResponseMessage> deleteLight(@RequestBody Long id) {
        try {
            comtService.deleteLight(id);
            return ResponseEntity.ok(new ResponseMessage("Light Deleted", ResponseMessage.Severity.INFORMATIONAL, "Light of id " + id + " is no longer accessible or recoverable."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Communication Unsuccessful", ResponseMessage.Severity.HIGH, e.getMessage()));
        }
    }

    @PostMapping("/light/sync-up")
    public ResponseEntity<ResponseMessage> syncLightUp(@RequestBody Long id) {
        try {
            comtService.syncUp(id);
            return ResponseEntity.ok(new ResponseMessage("Communication Successful", ResponseMessage.Severity.INFORMATIONAL, "Data sent to light of id " + id + " successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Communication Unsuccessful", ResponseMessage.Severity.HIGH, e.getMessage()));
        }
    }

    @PostMapping("/light/sync-down")
    public ResponseEntity<ResponseMessage> syncLightDown(@RequestBody Long id) {
        try {
            comtService.syncDown(id);
            return ResponseEntity.ok(new ResponseMessage("Communication Successful", ResponseMessage.Severity.INFORMATIONAL, "Data downloaded from light " + id + " successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Communication Unsuccessful", ResponseMessage.Severity.HIGH, e.getMessage()));
        }
    }

    /*
        Wiki
     */

    @GetMapping("wiki/all")
    public ResponseEntity<List<WikiPage>> getWikiPages(){
        return ResponseEntity.ok(comtService.findAllWikiPages());
    }

    @GetMapping("/wiki/{id}")
    public ResponseEntity<?> getWikiByUser(@PathVariable Long id){
        try {
            return ResponseEntity.ok(comtService.findAllWikiPagesByUser(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Logs", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Retrieving Logs", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/wiki/delete")
    public ResponseEntity<ResponseMessage> deleteWikiPage(@RequestBody Long id) {
        try {
            comtService.deleteWikiPage(id);
            return ResponseEntity.ok(new ResponseMessage("Wiki Page Deleted", ResponseMessage.Severity.INFORMATIONAL, "Page of id " + id + " is no longer accessible or recoverable."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error deleting wiki page", ResponseMessage.Severity.HIGH, e.getMessage()));
        }
    }

    @PostMapping("/wiki/save")
    public ResponseEntity<ResponseMessage> saveWikiPage(@RequestBody ModeratorWikiRequest wikiPage, Principal principal) {
        try {
            comtService.saveWikiPage(wikiPage, getCurrentUser(principal));
            return ResponseEntity.ok(new ResponseMessage("Wiki Page Saved", ResponseMessage.Severity.INFORMATIONAL, "Wiki Page has been saved to Costi Online"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Wiki Page", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    /*
        Account Logs
     */

    @GetMapping("/account-logs/all")
    public ResponseEntity<List<ModeratorAccountLogResponse>> getAccountLogs(){
        List<AccountLog> logs = comtService.findAllAccountLogs();
        List<ModeratorAccountLogResponse> response = logs.stream()
                .map(AccountLog::getModeratorView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account-logs/{id}")
    public ResponseEntity<?> getAccountLogsByUser(@PathVariable Long id){
        try {
            List<AccountLog> logs = comtService.findAllAccountLogsByUser(id);
            List<ModeratorAccountLogResponse> response = logs.stream()
                    .map(AccountLog::getModeratorView)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Logs", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Retrieving Logs", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/account-logs/delete")
    public ResponseEntity<ResponseMessage> deleteAccountLog(@RequestBody Long id) {
        try {
            comtService.deleteAccountLog(id);
            return ResponseEntity.ok(new ResponseMessage("Account Log Deleted", ResponseMessage.Severity.INFORMATIONAL, "Log of id " + id + " is no longer accessible or recoverable."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error deleting log", ResponseMessage.Severity.HIGH, e.getMessage()));
        }
    }

    @PostMapping("/account-logs/save")
    public ResponseEntity<ResponseMessage> saveAccountLog(@RequestBody @Valid ModeratorAccountLogRequest request) {
        try {
            comtService.saveAccountLog(request);
            return ResponseEntity.ok(new ResponseMessage("Wiki Page Saved", ResponseMessage.Severity.INFORMATIONAL, "Wiki Page has been saved to Costi Online"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Wiki Page", ResponseMessage.Severity.LOW, e.getMessage()));
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

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(comtService.findUserById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving User", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Retrieving User", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/users/delete")
    public ResponseEntity<ResponseMessage> deleteUser(@RequestBody Long id) {
        try {
            comtService.deleteUser(id);
            return ResponseEntity.ok(new ResponseMessage("User Deleted", ResponseMessage.Severity.INFORMATIONAL, "User of id " + id + ", is no longer accessible or recoverable. Wiki posts are re-assigned to owner account."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error deleting user", ResponseMessage.Severity.HIGH, e.getMessage()));
        }
    }

    @PostMapping("/users/save")
    public ResponseEntity<ResponseMessage> saveUser(@RequestBody @Valid ModeratorAccountRequest request) {
        try {
            comtService.saveUser(request);
            return ResponseEntity.ok(new ResponseMessage("User Saved", ResponseMessage.Severity.INFORMATIONAL, "User has been saved to Costi Online"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving User", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    private User getCurrentUser(Principal principal) throws Exception{
        if (principal == null) {
            throw new Exception("No user logged in");
        }
        String username = principal.getName();
        User u = userService.loadUserByUsername(username);
        return u;
    }

    /*
        Files
     */

    @GetMapping("/files/all")
    public ResponseEntity<List<Attachment>> getAllAttachments(){
        List<Attachment> allFiles = comtService.findAllAttachments();
        return ResponseEntity.ok(allFiles);
    }

    @PostMapping("/files/save")
    public ResponseEntity<ResponseMessage> saveFile(@RequestParam() MultipartFile file) {
        try {
            comtService.saveFile(file, false);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("File Saved", ResponseMessage.Severity.INFORMATIONAL, "File was saved to Costi Online."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Saving File", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/files/{id}/delete")
    public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String id) {
        try {
            comtService.deleteAttachment(id);
            return ResponseEntity.ok(new ResponseMessage("File Deleted", ResponseMessage.Severity.INFORMATIONAL, "File of id " + id + ", is no longer accessible or recoverable."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Unable to Delete File", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    /*
        Dynamic Content
     */

    @GetMapping("/content/all")
    public ResponseEntity<List<DynamicContent>> getAllDynamicContent(){
        List<DynamicContent> allContent = comtService.findAllDynamicContent();
        return ResponseEntity.ok(allContent);
    }

    @PostMapping("/content/save")
    public ResponseEntity<ResponseMessage> saveDynamicContent(@RequestBody @Valid DynamicContent content) {
        try {
            comtService.saveDynamicContent(content);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Dynamic Content Saved", ResponseMessage.Severity.INFORMATIONAL, "Dynamic content was saved to Costi Online."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Saving Content", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/content/delete")
    public ResponseEntity<ResponseMessage> deleteDynamicContent(@RequestBody Long id) {
        try {
            comtService.deleteDynamicContent(id);
            return ResponseEntity.ok(new ResponseMessage("Content Deleted", ResponseMessage.Severity.INFORMATIONAL, "Content of id " + id + ", is no longer accessible or recoverable."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Unable to Delete Content", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }
}
