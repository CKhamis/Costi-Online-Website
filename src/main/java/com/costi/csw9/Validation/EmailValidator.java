package com.costi.csw9.Validation;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        //TODO: implement this!
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher("Visit W3Schools!");
        boolean matchFound = matcher.find();
        return true;
    }
}
