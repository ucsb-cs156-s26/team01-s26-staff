package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** This is a REST controller for RecommendationRequests */
@Tag(name = "RecommendationRequests")
@RequestMapping("/api/RecommendationRequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

  @Autowired RecommendationRequestRepository recommendationRequestRepository;

  /**
   * List all RecommendationRequests
   *
   * @return an iterable of RecommendationRequest
   */
  @Operation(summary = "List all recommendation requests")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/all")
  public Iterable<RecommendationRequest> allRecommendationRequests() {
    Iterable<RecommendationRequest> recommendationRequests =
        recommendationRequestRepository.findAll();
    return recommendationRequests;
  }

  /**
   * Create a new RecommendationRequest
   *
   * @param requesterEmail the email of the person requesting the recommendation
   * @param professorEmail the email of the professor receiving the request
   * @param explanation explanation of what the recommendation is for
   * @param dateRequested the date and time the recommendation was requested
   * @param dateNeeded the date and time by which the recommendation is needed
   * @param done whether the recommendation request has been completed
   * @return the saved RecommendationRequest
   */
  @Operation(summary = "Create a new recommendation request")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/post")
  public RecommendationRequest postRecommendationRequest(
      @Parameter(name = "requesterEmail", description = "Email address of the requester")
          @RequestParam
          String requesterEmail,
      @Parameter(name = "professorEmail", description = "Email address of the professor")
          @RequestParam
          String professorEmail,
      @Parameter(name = "explanation", description = "Explanation of the recommendation request")
          @RequestParam
          String explanation,
      @Parameter(
              name = "dateRequested",
              description = "Date and time requested in ISO format, e.g. 2022-05-20T00:00:00")
          @RequestParam("dateRequested")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateRequested,
      @Parameter(
              name = "dateNeeded",
              description = "Date and time needed in ISO format, e.g. 2022-11-15T00:00:00")
          @RequestParam("dateNeeded")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateNeeded,
      @Parameter(name = "done", description = "Whether the recommendation request is completed")
          @RequestParam
          boolean done)
      throws JsonProcessingException {

    RecommendationRequest recommendationRequest = new RecommendationRequest();
    recommendationRequest.setRequesterEmail(requesterEmail);
    recommendationRequest.setProfessorEmail(professorEmail);
    recommendationRequest.setExplanation(explanation);
    recommendationRequest.setDateRequested(dateRequested);
    recommendationRequest.setDateNeeded(dateNeeded);
    recommendationRequest.setDone(done);

    RecommendationRequest savedRecommendationRequest =
        recommendationRequestRepository.save(recommendationRequest);

    return savedRecommendationRequest;
  }
}
