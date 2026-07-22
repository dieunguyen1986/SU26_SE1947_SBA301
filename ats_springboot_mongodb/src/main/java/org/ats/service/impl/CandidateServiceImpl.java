package org.ats.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ats.entity.Candidate;
import org.ats.repository.CandidateRepository;
import org.ats.service.CandidateService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateServiceImpl implements CandidateService {
    private  final CandidateRepository candidateRepository;

    @Override
    public Candidate create(Candidate candidate) {

        candidate.setAppliedAt(LocalDateTime.now());
       return candidateRepository.save(candidate);

    }

    @Override
    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    @Override
    public Candidate findById(String id) {
        return candidateRepository.findById(id).orElseThrow(()-> {
            throw new RuntimeException("Candidate not found");
        });
    }

    @Override
    public void delete(String id) {
        Candidate candidate = findById(id);

        candidateRepository.delete(candidate);
    }
}
