package org.c8springzeebetestexample.workers;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

@Component
public class PaperReviewProcessWorkers {

    @JobWorker(type = "review-input")
    public void reviewInput() {
        System.out.println("Paper Review Input");
    }

    @JobWorker(type = "credibility-check")
    public void credibilityCheck() {
        System.out.println("Credibility Check Job Worker");
    }

    @JobWorker(type = "update-systems")
    public void updateSystems() {
        System.out.println("Update Systems Job Worker");
    }
}