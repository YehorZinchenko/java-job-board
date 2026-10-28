package com.zinchenko.jobboard.job;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService service;

    public JobController(JobService service) { this.service = service; }

    @GetMapping
    public List<Job> list(@RequestParam(required = false) String q) { return service.findAll(q); }

    @GetMapping("/{id}")
    public Job get(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public ResponseEntity<Job> create(@Valid @RequestBody JobRequest request) {
        Job job = service.create(request);
        return ResponseEntity.created(URI.create("/api/jobs/" + job.getId())).body(job);
    }

    @PutMapping("/{id}")
    public Job update(@PathVariable Long id, @Valid @RequestBody JobRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
