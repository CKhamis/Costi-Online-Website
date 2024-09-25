package com.costi.csw9.Cache;

import com.costi.csw9.Model.Ajax.GitHubProfileInfo;
import com.costi.csw9.Model.Ajax.ProjectInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Component
public class Projects {
    private static final String TOKEN = System.getenv("GHUB_TOKEN");
    private static final String USERNAME = "CKhamis";
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    public static HashMap<String, ProjectInfo> projectList = new HashMap<>();
    public static GitHubProfileInfo profileInfo;

    @Scheduled(cron = "0 0 6,18 * * ?")
    public static void refreshProjects() {
        System.out.println("Refreshing projects");
        updateCache();
    }

    private static void updateCache(){
        OkHttpClient client = new OkHttpClient();

        String url = String.format("https://api.github.com/users/%s/repos", USERNAME);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "token " + TOKEN)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONArray reposArray = new JSONArray(responseBody);

                // Loop through the array and collect repository names
                for (int i = 0; i < reposArray.length(); i++) {
                    JSONObject repo = reposArray.getJSONObject(i);
                    ProjectInfo newProject = new ProjectInfo();
                    newProject.setUpdated(LocalDateTime.now());
                    newProject.setName(repo.getString("name"));
                    newProject.setCreated(parseDate(repo.getString("created_at")));
                    newProject.setForks(repo.getInt("forks"));
                    newProject.setWatching(repo.getInt("watchers"));
                    newProject.setIssues(repo.getInt("open_issues"));
                    try{
                        newProject.setDescription(repo.getString("description"));
                    }catch (Exception e){
                        newProject.setDescription("no description");
                    }

                    newProject.setUrl(repo.getString("html_url"));

                    // convert json array into java array
                    JSONArray topics = repo.getJSONArray("topics");
                    ArrayList<String> topicList = new ArrayList<>();
                    for(int l = 0; l < topics.length(); l++){
                        topicList.add(topics.getString(l));
                    }
                    newProject.setTopics(topicList);

                    // Get readme file
                    String readmeRaw = fetchReadmeContents(client, repo.getString("name"));
                    newProject.setReadmeContent(readmeRaw);
                    newProject.setReadmeContentHTML("");
                    if(readmeRaw != null){
                        Node rm = MARKDOWN_PARSER.parse(readmeRaw);
                        newProject.setReadmeContentHTML(HTML_RENDERER.render(rm));
                    }

                    newProject.setLogo(fetchLogo(client, repo.getString("name")));
                    newProject.setImageLinks(fetchScreenshots(client, repo.getString("name")));
                    newProject.setCommits(fetchCommitCount(client, repo.getString("name")));
                    projectList.put(newProject.getName(), newProject);
                }

            } else {
                System.out.println("Failed to fetch repositories. Response code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> fetchScreenshots(OkHttpClient client, String repoName) {
        ArrayList<String> screenshots = new ArrayList<>();
        String contentsUrl = String.format("https://api.github.com/repos/%s/%s/contents/screenshots", USERNAME, repoName);

        Request contentsRequest = new Request.Builder()
                .url(contentsUrl)
                .addHeader("Authorization", "token " + TOKEN)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        try (Response contentsResponse = client.newCall(contentsRequest).execute()) {
            if (contentsResponse.isSuccessful() && contentsResponse.body() != null) {
                String contentsResponseBody = contentsResponse.body().string();
                JSONArray screenshotsArray = new JSONArray(contentsResponseBody);

                // List the URLs of the images
                for (int j = 0; j < screenshotsArray.length(); j++) {
                    JSONObject file = screenshotsArray.getJSONObject(j);
                    String downloadUrl = file.getString("download_url");
                    screenshots.add(downloadUrl);
                }
            } else {
                System.out.println("No screenshots folder or failed to fetch contents for " + repoName + ". Response code: " + contentsResponse.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return screenshots;
    }

    private static LocalDateTime parseDate(String dateStr) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr);
        return zonedDateTime.toLocalDateTime();
    }

    private static String fetchReadmeContents(OkHttpClient client, String repoName) {
        String mainUrl = String.format("https://raw.githubusercontent.com/%s/%s/master/README.md", USERNAME, repoName);

        Request readmeRequest = new Request.Builder()
                .url(mainUrl)
                .build();

        try (Response readmeResponse = client.newCall(readmeRequest).execute()) {
            if (readmeResponse.isSuccessful() && readmeResponse.body() != null) {
                return readmeResponse.body().string();
            } else {
                System.out.println("Failed to fetch README for " + repoName + ". Response code: " + readmeResponse.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String fetchLogo(OkHttpClient client, String repoName) {
        String mainUrl = String.format("https://raw.githubusercontent.com/%s/%s/master/Logo.svg", USERNAME, repoName);

        Request readmeRequest = new Request.Builder()
                .url(mainUrl)
                .build();

        try (Response readmeResponse = client.newCall(readmeRequest).execute()) {
            if (readmeResponse.isSuccessful() && readmeResponse.body() != null) {
                return mainUrl;
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static int fetchCommitCount(OkHttpClient client, String repoName) {
        String commitsUrl = String.format("https://api.github.com/repos/%s/%s/commits", USERNAME, repoName);

        Request commitsRequest = new Request.Builder()
                .url(commitsUrl)
                .addHeader("Authorization", "token " + TOKEN)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        try (Response commitsResponse = client.newCall(commitsRequest).execute()) {
            if (commitsResponse.isSuccessful() && commitsResponse.body() != null) {
                String commitsResponseBody = commitsResponse.body().string();
                JSONArray commitsArray = new JSONArray(commitsResponseBody);
                return commitsArray.length();
            } else {
                System.out.println("Failed to fetch commits for " + repoName + ". Response code: " + commitsResponse.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
