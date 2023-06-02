package com.costi.csw9.Util;

import com.costi.csw9.Model.Ajax.ProjectInfo;

import java.util.ArrayList;
import java.util.List;

public class InfoInitializer {
    public static List<ProjectInfo> initializeProjects() {
        List<ProjectInfo> objects = new ArrayList<>();

        // Create some sample objects
        ProjectInfo costiOnline = new ProjectInfo("/images/webpageImages/CostiOnline.jpg", "2022", "Costi Online is a website that is designed to show off various projects, services, and media I made. This project initially began as a website that covers my 9th Minecraft survival world. It had links to participate in elections, polls, and the history of the previous worlds. After it was published on costionline.com, I decided to make it more general. The aforementioned feature has been moved to the <a href=\"/Minecraft\">Minecraft</a> section.", "interdimensional", new String[]{"https://github.com/CKhamis/Costi-Online-Website"});

        // Add objects to the list
        objects.add(costiOnline);

        return objects;
    }
}
