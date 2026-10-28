package com.zinchenko.jobboard.job;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class JobService {
    private final JobRepository repository;

    public JobService(JobRepository repository) { this.repository = repository; }

    public List<Job> findAll(String query) {
        if (query == null || query.isBlank()) {
            return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        String term = query.strip();
        return repository.findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrderByIdDesc(term, term);
    }

    public Job findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new JobNotFoundException(id));
    }

    @Transactional
    public Job create(JobRequest request) { return repository.save(new Job(request)); }

    @Transactional
    public Job update(Long id, JobRequest request) {
        Job job = findById(id);
        job.update(request);
        return repository.save(job);
    }

    @Transactional
    public void delete(Long id) { repository.delete(findById(id)); }
}
