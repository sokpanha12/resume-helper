package com.resumehelper.backend.job.service;

import com.resumehelper.backend.job.entity.Job;
import com.resumehelper.backend.job.repository.JobRepository;
import com.resumehelper.backend.job.scraper.JobScraper;
import com.resumehelper.backend.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final List<JobScraper> scrapers;
    private final NotificationService notificationService;

    public JobService(JobRepository jobRepository, List<JobScraper> scrapers, NotificationService notificationService) {
        this.jobRepository = jobRepository;
        this.scrapers = scrapers;
        this.notificationService = notificationService;
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public void scrapeAndSaveJobs() {
        int newJobsCount = 0;
        for (JobScraper scraper : scrapers) {
            List<Job> jobs = scraper.scrape();
            for (Job job : jobs) {
                if (jobRepository.findByUrl(job.getUrl()).isEmpty()) {
                    jobRepository.save(job);
                    newJobsCount++;
                    notificationService.sendDiscordNotification("New Job Found: **" + job.getTitle() + "** at **" + job.getCompany() + "**\n" + job.getUrl());
                }
            }
        }
        if (newJobsCount > 0) {
            notificationService.sendDiscordNotification("Scrape complete. Found " + newJobsCount + " new jobs.");
        }
    }

    public Job getById(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found: " + id));
    }

    public Job save(Job job) {
        return jobRepository.save(job);
    }
}
