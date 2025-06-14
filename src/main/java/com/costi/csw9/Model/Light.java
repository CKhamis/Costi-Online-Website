package com.costi.csw9.Model;

import com.costi.csw9.Model.DTO.LightRequest;
import com.costi.csw9.Model.DTO.ModeratorLightRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@NoArgsConstructor
@Table(name = "light")
public class Light {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false, unique = true)
    private String label;
    @Column(nullable = false, unique = true)
    private String address;
    @Column(nullable = false)
    private LocalDateTime dateAdded;
    @Column(nullable = false)
    private LocalDateTime lastModified;
    private LocalDateTime lastConnected;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private String pattern;
    @Column(nullable = false)
    private boolean isFavorite;
    @Column(nullable = false)
    private boolean isPublic;
    @Column(nullable = false)
    private boolean isEnabled;
    @JsonIgnore
    @OneToMany(mappedBy = "light", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LightLog> logs = new ArrayList<>();

    @Transient
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/y hh:mm a");

    public Light(String address, String label, String color, String pattern) {
        this.address = address;
        this.label = label;
        this.dateAdded = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.status = "New";
        this.color = color;
        this.pattern = pattern;
        this.isEnabled = false;
        this.isFavorite = false;
        this.isPublic = false;
    }

    public Light(LightRequest request){
        this.address = request.getAddress();
        this.label = request.getLabel();
        this.dateAdded = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.color = request.getColor();
        this.pattern = request.getPattern();
        this.isEnabled = false;
        this.isFavorite = false;
        this.isPublic = false;
        this.status = "New";
    }

    public LightRequest getRequest(){
        return new LightRequest(address, label, color, pattern);
    }

    public void setValues(LightRequest request){
        this.label = request.getLabel();
        this.color = request.getColor();
        this.pattern = request.getPattern();
        this.address = request.getAddress();
        this.status = "Recently Edited";
    }

    public void setValues(ModeratorLightRequest request){
        this.label = request.getLabel();
        this.color = request.getColor();
        this.pattern = request.getPattern();
        this.address = request.getAddress();
        this.isEnabled = request.isEnabled();
        this.isPublic = request.isPublic();
        this.isFavorite = request.isFavorite();
        this.status = "Recently Edited";
    }

    public List<LightLog> getLogs() {
        Collections.sort(logs, Comparator.comparing(LightLog::getDateCreated));
        Collections.reverse(logs);
        return logs;
    }

    public String getlastModified(){
        return lastModified.getMonthValue() + "/" + lastModified.getDayOfMonth() + "/" + lastModified.getYear();
    }

    @Transient
    public String getFormattedLastConnected(){
        return (lastConnected != null) ? lastConnected.format(formatter) : LocalDateTime.MIN.format(formatter);
    }

    @Transient
    public String getFormattedModified(){
        return (lastModified != null) ? lastModified.format(formatter) : LocalDateTime.MIN.format(formatter);
    }

    @Transient
    public String getFormattedAdded(){
        return (dateAdded != null) ? dateAdded.format(formatter) : LocalDateTime.MIN.format(formatter);
    }

    @Transient
    public String getTimeSinceLastConnected() {
        if(lastConnected == null){
            return "never used";
        }

        String unit = "";
        LocalDateTime now = LocalDateTime.now();
        long diff;
        if((diff = ChronoUnit.SECONDS.between(lastConnected,now)) < 60){
            unit = "seconds";
        } else if ((diff = ChronoUnit.MINUTES.between(lastConnected,now)) < 60) {
            unit = "minutes";
        } else if ((diff = ChronoUnit.HOURS.between(lastConnected,now)) < 24) {
            unit = "hours";
        } else if ((diff = ChronoUnit.DAYS.between(lastConnected,now)) < 30) {
            unit = "days";
        } else if ((diff = ChronoUnit.MONTHS.between(lastConnected,now)) < 12) {
            unit = "months";
        } else{
            diff = ChronoUnit.YEARS.between(lastConnected,now);
        }
        return String.format("%d %s",diff,unit);
    }

    @Transient
    @JsonIgnore
    public String getCurrentStatus() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + address;

        try {
            ResponseEntity<LightRequest> response = restTemplate.exchange(url, HttpMethod.GET, null, LightRequest.class);
            LightRequest lightRequest = response.getBody();
            lastConnected = LocalDateTime.now();
            status = "Active";
            setValues(lightRequest);
            return "Connection Successful: " + lightRequest; //First letter of output is used in controller
        } catch (Exception e) {
            //e.printStackTrace();
            status = "Error";
            return "Error updating status of light: " + e.getMessage();
        }
    }

    @Transient
    @JsonIgnore
    public String sendLightRequest() {
        String urlString = "http://" + address;
        LightRequest lightRequest = this.getRequest();

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String json = new ObjectMapper().writeValueAsString(lightRequest);
            byte[] out = json.getBytes(StandardCharsets.UTF_8);

            conn.setFixedLengthStreamingMode(out.length);
            conn.connect();

            try (OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }

            String response = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            lastConnected = LocalDateTime.now();
            status = "Active";
            return "Connection Successful: " + response;
        } catch (Exception e) {
            e.printStackTrace();
            status = "Error";
            return "Error sending light request: " + e.getMessage();
        }
    }

}
