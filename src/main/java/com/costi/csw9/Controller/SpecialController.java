package com.costi.csw9.Controller;

import com.costi.csw9.Model.Axcel.GameProgress;
import com.costi.csw9.Model.Axcel.Sprite;
import com.costi.csw9.Model.Axcel.SpriteFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Controller
public class SpecialController {
    private static final HashSet<Sprite> preDefinedSprites = SpriteFactory.createSprites();
    private static final int requiredFinds = 0;

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
        String responseBody = "";

        // Start a new name if one was not already created
        if (gameProgress == null) {
            gameProgress = new GameProgress();
            session.setAttribute("gameProgress", gameProgress);
            responseBody += "Initializing new game.";
        }

        // Add if not in the current game progress
        if(!gameProgress.getSpriteNamesFound().contains(spriteName)){
            gameProgress.getSpriteNamesFound().add(spriteName);
        }else{
            responseBody += " Sprite already added.";
        }

        // Construct the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("success", true);
        jsonResponse.put("message", "Sprite found. " + responseBody);

        return jsonResponse;
    }

    @GetMapping("/games/Axcel/check-game-status")
    @ResponseBody
    public Map<String, Object> isGameStarted(HttpSession session) {
        GameProgress gameProgress = (GameProgress) session.getAttribute("gameProgress");
        boolean gameStarted = (gameProgress != null);
        LocalDateTime timeStart = (gameStarted ? gameProgress.getTimeStart() : null);

        // Construct the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("gameStarted", gameStarted);
        jsonResponse.put("timeStart", timeStart);

        //TODO: return the start time
        return jsonResponse;
    }

}
