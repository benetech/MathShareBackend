package org.benetech.mathshare.controllers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.service.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/set")
@SuppressFBWarnings(value = "SLF4J_FORMAT_SHOULD_BE_CONST",
        justification = "We need Logger to contain not-constant data to be valuable")
public class ProblemController {

    private final Logger logger = LoggerFactory.getLogger(ProblemController.class);

    @Autowired
    private ProblemService problemService;

    @GetMapping("/view/{code}")
    ResponseEntity<ProblemSetDTO> getProblemSet(@PathVariable String code) {
        try {
            ProblemSetDTO body = problemService.findProblemsByUrlCode(code);
            if (body != null) {
                return new ResponseEntity<>(body, HttpStatus.OK);
            } else {
                logger.error("ProblemSet with code " + code + " wasn't found");
            }
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
