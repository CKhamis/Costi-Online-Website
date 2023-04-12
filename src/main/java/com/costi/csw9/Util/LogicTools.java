package com.costi.csw9.Util;

public class LogicTools {
    public static int clamp(int value, int min, int max){
        return Math.max(min, Math.min(max, value));
    }
}
