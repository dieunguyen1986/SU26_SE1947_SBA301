package org.ats.service;

import org.ats.entity.Skill;

import java.util.List;

public interface SkillService {
    Skill create(Skill skill);
    List<Skill> getSkills();

    Skill getSkillById(String id);

    List<Skill> getSkillsByCategory(String category);

    Skill delete(String id);

    List<Skill> saveAll(List<Skill> skills);
    Skill update(Skill skill);
}
