package com.costi.csw9.Model.Ajax;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProjectInfo {
    // key
    private String url;

    // value
    private String name;
    private String logo;
    private String description;
    private String readmeContent;
    private String readmeContentHTML;
    private ArrayList<String> imageLinks;
    private ArrayList<String> topics;
    private LocalDateTime created;
    private LocalDateTime updated;

    private int commits;
    private int watching;
    private int forks;
    private int issues;

}
