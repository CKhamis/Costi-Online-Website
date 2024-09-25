package com.costi.csw9.Model.Ajax;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class ProjectInfo {
    private String url; // hard coded key
    private Color color; // hard coded color
    private LocalDate lastUpdated; //transient value

    // from API
    private String logo;
    private String name;
    private String description;
    private ArrayList<String> imageLinks;
    private ArrayList<String> keywords;
    private LocalDate created;
    private LocalDate updated;

    private int commits;
    private int watching;
    private int forks;

}
