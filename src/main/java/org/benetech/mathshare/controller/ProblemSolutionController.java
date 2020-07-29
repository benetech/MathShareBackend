package org.benetech.mathshare.controller;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionSetDTO;
import org.benetech.mathshare.model.dto.SolutionSetPublicDTO;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/solution")
public class ProblemSolutionController {

    private final Logger logger = LoggerFactory.getLogger(ProblemSolutionController.class);

    @Autowired
    private ProblemSolutionService problemSolutionService;

    private String watsonApiGrant = "urn:ibm:params:oauth:grant-type:apikey";

    @Value("${ibm-watson.api-key}")
    private String watsonAPiKey;

    @GetMapping("/revision/{shareCode}")
    ResponseEntity<SolutionDTO> getProblemSolution(@PathVariable String shareCode) {
        SolutionDTO body = problemSolutionService.findSolutionByUrlCode(shareCode);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSet with code {} wasn't found", shareCode);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/")
    ResponseEntity<SolutionDTO> createProblemSolution(@RequestBody SolutionDTO solution) {
        SolutionRevision saved = problemSolutionService.saveNewVersionOfSolution(solution);
        return new ResponseEntity<>(SolutionMapper.INSTANCE.toSolutionDTO(saved), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{code}")
    ResponseEntity<SolutionDTO> createOrUpdateSolution(@PathVariable String code, @RequestBody SolutionDTO solution) {
        Pair<Boolean, SolutionRevision> saved = problemSolutionService.createOrUpdateProblemSolution(
                code, solution);
        HttpStatus status = saved.getFirst() ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity<>(SolutionMapper.INSTANCE.toSolutionDTO(saved.getSecond()), status);
    }

    @GetMapping("/{code}")
    ResponseEntity<SolutionDTO> editProblemSolution(@PathVariable String code) {
        SolutionDTO body = problemSolutionService.getLatestProblemSolutionForEditing(code);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSolution with code {} wasn't found", code);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{code}/archive")
    ResponseEntity<SolutionSetDTO> archiveProblemSet(
        @PathVariable String code,
        @RequestBody SolutionSetDTO solutionSetDTO,
        @RequestHeader(value = "x-initiator", required = true) String initiator,
        @RequestHeader(value = "x-role", required = false) String role
    ) {
        SolutionSetDTO body = problemSolutionService.setArchiveMode(code, initiator, role, solutionSetDTO.getArchiveMode());
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSet with code {} wasn't found", code);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/create/{problemSetCode}")
    ResponseEntity<SolutionSetDTO> createReviewSolutionsFromShareCode(
            @PathVariable String problemSetCode,
            @RequestParam Map<String, String> searchParameters,
            @RequestHeader(value = "x-initiator", required = false) String initiator,
            @RequestHeader(value = "x-partner-code", required = false) String partnerCode
    ) {
        SolutionSetDTO body = problemSolutionService.createReviewSolutionsFromShareCode(
            problemSetCode, searchParameters, initiator, partnerCode
        );
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("SolutionSet with code {} wasn't found", problemSetCode);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/review/{problemSetCode}")
    ResponseEntity<SolutionSetDTO> createReviewSolution(
            @PathVariable String problemSetCode, @RequestBody List<SolutionDTO> solutions,
            @RequestHeader(value = "x-initiator", required = false) String initiator
    ) {
        SolutionSetDTO body = problemSolutionService.createReviewSolutions(problemSetCode, solutions, initiator);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("SolutionSet with code {} wasn't found", problemSetCode);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/solve/{editCode}")
    ResponseEntity<SolutionSetDTO> updateProblemSetSolution(@PathVariable String editCode,
                                                               @RequestBody List<SolutionDTO> solutions) {
        SolutionSetDTO body = problemSolutionService.updateReviewSolutions(editCode, solutions);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSetSolution with code {} wasn't found", editCode);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/solve/{editCode}")
    ResponseEntity<SolutionSetDTO> getProblemSetEditableSolution(@PathVariable String editCode) {
        SolutionSetDTO body = problemSolutionService.getProblemSetSolutions(editCode);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSetSolution with code {} wasn't found", editCode);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/review/{reviewCode}")
    ResponseEntity<SolutionSetPublicDTO> getReviewSolution(@PathVariable String reviewCode) {
        SolutionSetPublicDTO body = problemSolutionService.getReviewSolutions(reviewCode);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSolution with code {} wasn't found", reviewCode);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/s2t/token", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getSpeechToTextToken() {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType,
                    "grant_type=" + URLEncoder.encode(this.watsonApiGrant, "UTF-8")
                            + "&apikey=" + this.watsonAPiKey);
            Request request = new Request.Builder()
                    .url("https://iam.bluemix.net/identity/token")
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            return new ResponseEntity<>(response.body().string(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
