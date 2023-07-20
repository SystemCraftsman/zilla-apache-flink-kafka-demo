package com.systemcraftsman.demo.service;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfanityService implements Serializable {

    private final List<String> badWords = new ArrayList<>();

    public ProfanityService() {
        badWords.add("shit");
        badWords.add("bastard");
        badWords.add("ass");
        badWords.add("fuck");
        badWords.add("bitch");
    }

    public List<String> getBadWords() {
        return badWords;
    }

}
