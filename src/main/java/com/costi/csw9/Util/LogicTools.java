package com.costi.csw9.Util;

public class LogicTools {
    public static final String POST_IMAGE_PATH = "/Downloads/Uploads/";

    // Flash Messages
    public static final String INVALID_PERMISSIONS_MESSAGE = "Current account does not have sufficient permissions.";
    public static final String NOT_FOUND_MESSAGE = " was not found in database.";

    public static int clamp(int value, int min, int max){
        return Math.max(min, Math.min(max, value));
    }
}
