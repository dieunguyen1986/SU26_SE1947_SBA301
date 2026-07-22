package org.ats.service;

import org.ats.entity.Candidate;

import java.util.List;

public interface CandidateService {
    Candidate create(Candidate candidate);

    List<Candidate> findAll();

    Candidate findById(String id);

    void delete(String id);
}
