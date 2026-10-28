package com.zinchenko.jobboard.job;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 100)
    private String company;
    @Column(nullable = false, length = 100)
    private String location;
    @Column(nullable = false, length = 4000)
    private String description;
    @Column(nullable = false)
    private int experienceYears;

    protected Job() {} // Required by JPA.

    public Job(JobRequest request) { update(request); }

    public void update(JobRequest request) {
        title = request.title().strip();
        company = request.company().strip();
        location = request.location().strip();
        description = request.description().strip();
        experienceYears = request.experienceYears();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public int getExperienceYears() { return experienceYears; }
}
