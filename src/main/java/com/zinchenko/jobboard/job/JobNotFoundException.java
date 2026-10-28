package com.zinchenko.jobboard.job;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long id) { super("Job with id " + id + " was not found"); }
}
