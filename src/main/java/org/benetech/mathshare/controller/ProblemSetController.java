package org.benetech.mathshare.controller;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.dto.ProblemStepDTO;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/problemSet")
public class ProblemSetController {

    private final Logger logger = LoggerFactory.getLogger(ProblemSetController.class);

    @Autowired
    private ProblemSetService problemSetService;

    @GetMapping("/revision/{shareCode}")
    ResponseEntity<ProblemSetDTO> getProblemSetRevision(@PathVariable String shareCode) {
        ProblemSetDTO body = problemSetService.findProblemsByUrlCode(shareCode);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSet with code {} wasn't found", shareCode);
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

    @PostMapping(path = "/")
    ResponseEntity<ProblemSetDTO> createProblemSet(
            @RequestBody ProblemSetDTO problemSet,
            @RequestHeader(value = "x-initiator", required = false) String initiator
    ) {
        ProblemSetRevision saved = problemSetService.saveNewProblemSet(problemSet, initiator);
        return new ResponseEntity<>(ProblemMapper.INSTANCE.toProblemSetDTO(saved), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{code}")
    ResponseEntity<ProblemSetDTO> createOrUpdateProblemSet(
            @PathVariable String code,
            @RequestBody ProblemSetDTO problemSet,
            @RequestHeader(value = "x-initiator", required = false) String initiator
    ) {
        Pair<Boolean, ProblemSetRevision> saved = problemSetService.createOrUpdateProblemSet(
                code, problemSet, initiator
        );
        HttpStatus status = saved.getFirst() ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity<>(ProblemMapper.INSTANCE.toProblemSetDTO(saved.getSecond()), status);
    }

    @GetMapping("/{code}")
    ResponseEntity<ProblemSetDTO> editProblemSet(@PathVariable String code) {
        ProblemSetDTO body = problemSetService.getLatestProblemSetForEditing(code);
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("ProblemSet with code {} wasn't found", code);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/exampleSets")
    ResponseEntity<List<ProblemSetDTO>> getExampleProblemSets() {
        List<ProblemSetDTO> result = problemSetService.findAllExampleProblems();
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            logger.error("Default problem set wasn't found");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{code}/steps/{problemId}")
    ResponseEntity<ProblemSetDTO> updateProblemSteps(
            @PathVariable String code,
            @PathVariable String problemId,
            @RequestBody List<ProblemStepDTO> problemSteps,
            @RequestHeader(value = "x-initiator", required = false) String initiator
    ) {
        ProblemSetRevision saved = problemSetService.updateProblemStepsInProblemSet(
            code, Integer.parseInt(problemId), problemSteps, initiator
        );
        return new ResponseEntity<>(ProblemMapper.INSTANCE.toProblemSetDTO(saved), HttpStatus.CREATED);
    }

    @GetMapping("/convert/{code}")
    ResponseEntity<Long> convertCode(@PathVariable String code) {
        Long result = UrlCodeConverter.fromUrlCode(code);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/convertX/{code}")
    ResponseEntity<String> convertCode(@PathVariable Long code) {
        String result = UrlCodeConverter.toUrlCode(code);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
