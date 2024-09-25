package com.costi.csw9.Controller.API;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/project")
public class ProjectController {
    private static final String TOKEN = System.getenv("GHUB_TOKEN");
    private static final String REPO_OWNER = "CKhamis";
    private static final String REPO_NAME = "Costi-Online-Website";

    @GetMapping("/all")
    public String getProjects() {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("https://api.github.com/repos/%s/%s", REPO_OWNER, REPO_NAME);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "token " + TOKEN)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println(response.body().string());
            } else {
                System.out.println("Request failed. Response code: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "finished";
    }
}
