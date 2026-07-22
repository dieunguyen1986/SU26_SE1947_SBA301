package org.ats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ats.entity.Candidate;
import org.ats.service.CandidateService;
import org.ats.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/candidates")
public class CandidateController {
    private final CandidateService candidateService;
    private final FileService fileService;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody Candidate candidate) {

        candidateService.create(candidate);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Candidate created"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        Map<String, String> result = fileService.uploadFile(file);
        return ResponseEntity.ok(result);
    }
}
