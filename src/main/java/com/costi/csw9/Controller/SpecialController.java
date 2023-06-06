package com.costi.csw9.Controller;

import com.costi.csw9.Model.Ajax.MediaInfo;
import com.costi.csw9.Model.Ajax.ProjectInfo;
import com.costi.csw9.Model.Axcel.GameProgress;
import com.costi.csw9.Util.InfoInitializer;
import org.apache.tomcat.jni.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class SpecialController {
    private static final int requiredFinds = 7;
    private List<ProjectInfo> projects = InfoInitializer.initializeProjects();
    private List<MediaInfo> mediaProjects = InfoInitializer.initializeMedia();

    /*
        JSON responses
     */
    @GetMapping("/get-media")
    public ResponseEntity<List<MediaInfo>> getMedia() {
        return ResponseEntity.ok(mediaProjects);
    }

    @GetMapping("/get-media-analytics")
    @ResponseBody
    public Map<String, Object> getMediaAnalytics() {
        // Construct the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("totalProjects", mediaProjects.size());

        int numVideos = 0, numFlyers = 0, numLogos = 0, numLiveAction = 0, numAnimation = 0;
        for(MediaInfo project : mediaProjects){
            String type = project.getType();

            if(type.equals("live-action")){
                numVideos++;
                numLiveAction++;
            }else if(type.equals("flyer"))
        }

        jsonResponse.put("repos", );

        return jsonResponse;
    }

    @GetMapping("/get-projects")
    public ResponseEntity<List<ProjectInfo>> getProjects() {
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/get-project-analytics")
    @ResponseBody
    public Map<String, Object> getProjectAnalytics() {
        // Construct the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("totalProjects", projects.size());

        int numActive = 0, numDiscontinued = 0, numJava = 0, numPython = 0, numOther = 0, numWeb = 0, numRepos = 0;
        for(ProjectInfo project : projects){
            if(project.isDiscontinued()){
                numDiscontinued++;
            }else{
                numActive++;
            }

            if(project.getType().equals("java")){
                numJava++;
            }else if(project.getType().equals("web")){
                numWeb++;
            }else if(project.getType().equals("python")){
                numPython++;
            }else{
                numOther++;
            }

            numRepos += project.getRepositoryLinks().length;
        }

        jsonResponse.put("active", numActive);
        jsonResponse.put("discontinued", numDiscontinued);
        jsonResponse.put("java", numJava);
        jsonResponse.put("python", numPython);
        jsonResponse.put("other", numOther);
        jsonResponse.put("web", numWeb);
        jsonResponse.put("repos", numRepos);

        return jsonResponse;
    }

    /*
        Axcel Game
     */
    @PostMapping("/games/Axcel/start-game")
    @ResponseBody
    public Map<String, Object> startGame(HttpSession session) {
        GameProgress gameProgress = (GameProgress) session.getAttribute("gameProgress");
        String responseBody = "Game started";

        // Start a new name if one was not already created
        if (gameProgress == null) {
            gameProgress = new GameProgress();
            session.setAttribute("gameProgress", gameProgress);
        }else{
            responseBody += "Game started already";
        }

        // Construct the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("success", true);
        jsonResponse.put("message", responseBody);

        return jsonResponse;
    }

    @PostMapping("/games/Axcel/found-sprite")
    @ResponseBody
    public Map<String, Object> handleFoundSprite(@RequestParam String spriteName, HttpSession session) {
        GameProgress gameProgress = (GameProgress) session.getAttribute("gameProgress");
        if (gameProgress == null) {
            gameProgress = new GameProgress();
            session.setAttribute("gameProgress", gameProgress);
        }

        Map<String, Object> jsonResponse = new HashMap<>();
        List<String> foundSprites = (gameProgress.getSpriteNamesFound());

        // Add the found sprite name to the game progress
        if (!gameProgress.getSpriteNamesFound().stream().anyMatch(s -> s.equals(spriteName))) {
            gameProgress.getSpriteNamesFound().add(spriteName);

            if (gameProgress.getSpriteNamesFound().size() == requiredFinds) {
                // All sprites found
                gameProgress.setTimeEnd(LocalDateTime.now());
            }

            jsonResponse.put("success", true);
            jsonResponse.put("message", "Sprite found!");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Sprite already found!");
        }
        jsonResponse.put("foundSprites", foundSprites);

        return jsonResponse;
    }

    @GetMapping("/games/Axcel/check-game-status")
    @ResponseBody
    public Map<String, Object> isGameStarted(HttpSession session) {
        GameProgress gameProgress = (GameProgress) session.getAttribute("gameProgress");
        boolean gameStarted = (gameProgress != null);
        LocalDateTime timeStart = (gameStarted ? gameProgress.getTimeStart() : LocalDateTime.MIN);
        LocalDateTime timeEnd = (gameStarted ? gameProgress.getTimeEnd() : LocalDateTime.MAX);

        List<String> foundSprites = (gameStarted ? gameProgress.getSpriteNamesFound() : null);

        // Construct the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("gameStarted", gameStarted);
        jsonResponse.put("timeStart", timeStart);
        jsonResponse.put("timeEnd", timeEnd);
        jsonResponse.put("timeNow", LocalDateTime.now());
        jsonResponse.put("foundSprites", foundSprites);
        jsonResponse.put("quota", requiredFinds);

        return jsonResponse;
    }

    @PostMapping("/games/Axcel/end-game")
    @ResponseBody
    public Map<String, Object> endGame(HttpSession session, HttpServletResponse response) {
        GameProgress gameProgress = (GameProgress) session.getAttribute("gameProgress");
        if (gameProgress != null) {
            // Update the game progress in the session
            session.setAttribute("gameProgress", gameProgress);

            // Invalidate the session to delete the session cookie
            session.invalidate();

            // Construct the JSON response
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Game ended successfully");
            return jsonResponse;
        } else {
            // Game progress not found, handle the error case
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Game progress not found");
            return jsonResponse;
        }
    }
}
