package org.ats.controller;

import lombok.RequiredArgsConstructor;
import org.ats.entity.Skill;
import org.ats.service.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/save-all")
    public ResponseEntity<?> saveAll(@RequestBody List<Skill> skills) {
        List<Skill> result = skillService.saveAll(skills);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "List of skill are saved!", "result", result));
    }


    @PostMapping
    public ResponseEntity<?> saveSkill(Skill skill) {

        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.create(skill));
    }
}
