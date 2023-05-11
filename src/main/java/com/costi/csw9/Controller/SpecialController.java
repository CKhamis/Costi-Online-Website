package com.costi.csw9.Controller;

import com.costi.csw9.Model.Axcel.GameProgress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
public class SpecialController {
    private static final int requiredFinds = 5;

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

        return jsonResponse;
    }

    @GetMapping("/games/Axcel/check-game-status")
    @ResponseBody
    public Map<String, Object> isGameStarted(HttpSession session) {
        GameProgress gameProgress = (GameProgress) session.getAttribute("gameProgress");
        boolean gameStarted = (gameProgress != null);
        LocalDateTime timeStart = (gameStarted ? gameProgress.getTimeStart() : LocalDateTime.MIN);
        List<String> foundSprites = (gameStarted ? gameProgress.getSpriteNamesFound() : null);

        // Construct the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("gameStarted", gameStarted);
        jsonResponse.put("timeStart", timeStart);
        jsonResponse.put("foundSprites", foundSprites);

        return jsonResponse;
    }

}
