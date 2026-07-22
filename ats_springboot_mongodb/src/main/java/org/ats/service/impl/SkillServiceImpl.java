package org.ats.service.impl;

import lombok.RequiredArgsConstructor;
import org.ats.entity.Skill;
import org.ats.service.SkillService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final MongoTemplate mongoTemplate;

    @Override
    public Skill create(Skill skill) {
        return mongoTemplate.insert(skill);
    }

    @Override
    public List<Skill> getSkills() {
        return mongoTemplate.findAll(Skill.class);
    }

    @Override
    public Skill getSkillById(String id) {
        return mongoTemplate.findById(id, Skill.class);
    }

    @Override
    public List<Skill> getSkillsByCategory(String category) {
        Query query = new Query();
        query.addCriteria(Criteria.where("category").is(category));
        return mongoTemplate.find(query, Skill.class);
    }

    @Override
    public Skill delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findAndRemove(query, Skill.class);

    }

    @Override
    public List<Skill> saveAll(List<Skill> skills) {
        return (List<Skill>) mongoTemplate.insertAll(skills);
    }

    @Override
    public Skill update(Skill skill) {
        return null;
    }
}
