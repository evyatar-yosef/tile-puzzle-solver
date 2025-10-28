package com.evyatar.tilepuzzle.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TilePuzzleService {

    public List<String> solveFromInput(String input) {
        // This is where you'll integrate your real solver logic
        List<String> steps = new ArrayList<>();
        steps.add("RIGHT");
        steps.add("DOWN");
        steps.add("LEFT");
        return steps;
    }
}
