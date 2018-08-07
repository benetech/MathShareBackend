package org.benetech.mathshare.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solution")
@SuppressFBWarnings(value = "SLF4J_FORMAT_SHOULD_BE_CONST",
        justification = "We need Logger to contain non-constant data to be valuable")
public class ProblemSolutionController {

    private final Logger logger = LoggerFactory.getLogger(ProblemSolutionController.class);

    @Autowired
    private ProblemSolutionService problemSolutionService;

    @PostMapping(path = "/new")
    ResponseEntity<SolutionDTO> createProblemSolution(@RequestBody SolutionDTO solution) {
        try {
            SolutionRevision saved = problemSolutionService.saveNewVersionOfSolution(
                    SolutionMapper.INSTANCE.fromDto(solution));
            return new ResponseEntity<>(SolutionMapper.INSTANCE.toSolutionDTO(saved), HttpStatus.CREATED);
        } catch (HttpMessageNotReadableException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
