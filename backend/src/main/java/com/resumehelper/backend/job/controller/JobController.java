package com.resumehelper.backend.job.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumehelper.backend.coverletter.service.OpenClawService;
import com.resumehelper.backend.job.entity.Job;
import com.resumehelper.backend.job.service.JobService;
import com.resumehelper.backend.resume.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs") // clearer base path
public class JobController {

    private final JobService jobService;
    private final ResumeService resumeService;
    private final OpenClawService openClawService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobController(JobService jobService, ResumeService resumeService, OpenClawService openClawService) {
        this.jobService = jobService;
        this.resumeService = resumeService;
        this.openClawService = openClawService;
    }

    // GET /api/jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    // POST /api/jobs/scrape
    @PostMapping("/scrape")
    public ResponseEntity<String> scrapeJobs() {
        jobService.scrapeAndSaveJobs();
        return ResponseEntity.ok("Jobs scraped and saved successfully!");
    }

    // POST /api/jobs/{id}/rank
    // Body: { "resumeId": 1 }
    @PostMapping("/{id}/rank")
    public ResponseEntity<Job> rankJob(@PathVariable Long id, @RequestBody Map<String, Object> body) throws IOException {
        Long resumeId = Long.valueOf(body.get("resumeId").toString());
        Job job = jobService.getById(id);
        String resumeText = resumeService.parseText(resumeId);

        String aiResult = openClawService.rankResumeAgainstJob(resumeText, job.getDescription());
        JsonNode json = objectMapper.readTree(aiResult);

        job.setMatchScore(json.get("score").asInt());
        job.setMatchExplanation(json.get("explanation").asText());

        return ResponseEntity.ok(jobService.save(job));
    }
}