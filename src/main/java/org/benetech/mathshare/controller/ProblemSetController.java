package org.benetech.mathshare.controller;

import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.service.ProblemSetService;
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
@RequestMapping("/problemSet")
public class ProblemSetController {

    private final Logger logger = LoggerFactory.getLogger(ProblemSetController.class);

    @Autowired
    private ProblemSetService problemSetService;

    @GetMapping("/view/{code}")
    ResponseEntity<ProblemSetDTO> getProblemSetRevision(@PathVariable String code) {
        ProblemSetDTO body = problemSetService.findProblemsByUrlCode(code);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSet with code {} wasn't found", code);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/defaultRevision")
    ResponseEntity<String> getDefaultProblemSetRevisionCode() {
        String result = problemSetService.getDefaultProblemSetRevisionCode();
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            logger.error("Default problem set revision wasn't found");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/default")
    ResponseEntity<String> getDefaultProblemSetCode() {
        String result = problemSetService.getDefaultProblemSetCode();
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            logger.error("Default problem set wasn't found");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/new")
    ResponseEntity<ProblemSetDTO> createProblemSet(@RequestBody ProblemSetDTO problemSet) {
        ProblemSetRevision saved = problemSetService.saveNewProblemSet(
                ProblemMapper.INSTANCE.fromDto(problemSet));
        return new ResponseEntity<>(ProblemMapper.INSTANCE.toProblemSetDTO(saved), HttpStatus.CREATED);
    }

    @PutMapping(path = "/")
    ResponseEntity<ProblemSetDTO> createOrUpdateProblemSet(@RequestBody ProblemSetDTO problemSet) {
        Pair<Boolean, ProblemSetRevision> saved = problemSetService.createOrUpdateProblemSet(problemSet);
        HttpStatus status = saved.getFirst() ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity<>(ProblemMapper.INSTANCE.toProblemSetDTO(saved.getSecond()), status);
    }

    @GetMapping("/edit/{code}")
    ResponseEntity<ProblemSetDTO> editProblemSet(@PathVariable String code) {
        ProblemSetDTO body = problemSetService.getLatestProblemSetForEditing(code);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSet with code {} wasn't found", code);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
