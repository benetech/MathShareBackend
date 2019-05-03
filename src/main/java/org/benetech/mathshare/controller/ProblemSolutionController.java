package org.benetech.mathshare.controller;

import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionSetDTO;
import org.benetech.mathshare.model.dto.SolutionSetPublicDTO;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solution")
public class ProblemSolutionController {

    private final Logger logger = LoggerFactory.getLogger(ProblemSolutionController.class);

    @Autowired
    private ProblemSolutionService problemSolutionService;

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

    @PostMapping(path = "/review/{problemSetCode}")
    ResponseEntity<SolutionSetDTO> createReviewSolution(@PathVariable String problemSetCode) {
        SolutionSetDTO body = problemSolutionService.createReviewSolutions(problemSetCode);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSolution with code {} wasn't found", problemSetCode);
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
}
