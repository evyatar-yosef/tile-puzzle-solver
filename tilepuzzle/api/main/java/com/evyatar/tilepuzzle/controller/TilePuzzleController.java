package com.evyatar.tilepuzzle.controller;

import com.evyatar.tilepuzzle.service.TilePuzzleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puzzle")
public class TilePuzzleController {

    private final TilePuzzleService puzzleService;

    public TilePuzzleController(TilePuzzleService puzzleService) {
        this.puzzleService = puzzleService;
    }

    @PostMapping("/solve")
    public ResponseEntity<List<String>> solvePuzzle(@RequestBody String input) {
        List<String> solution = puzzleService.solveFromInput(input);
        return ResponseEntity.ok(solution);
    }
}
