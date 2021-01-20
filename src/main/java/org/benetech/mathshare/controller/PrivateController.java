package org.benetech.mathshare.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
////import org.springframework.beans.factory.annotation.Autowired;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.dto.SolutionSetDTO;
import org.benetech.mathshare.service.ProblemSetService;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/private")
public class PrivateController {

//    private final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private ProblemSetService problemSetService;

    @Autowired
    private ProblemSolutionService problemSolutionService;

    @GetMapping(path = "/recent")
    ResponseEntity<List<ProblemSetDTO>> getRecentProblemSets(
            @RequestHeader(value = "x-initiator", required = false) String initiator,
            @RequestHeader(value = "x-archive-mode", required = false) String archiveMode,
            @RequestParam(value = "x-content-size", defaultValue = "15") String size
            ) {
        return new ResponseEntity<>(problemSetService.findLastNProblemSetsOfUser(
                initiator, archiveMode, Integer.parseInt(size)
        ), HttpStatus.OK);
    }

    @GetMapping(path = "/v2/recent")
    ResponseEntity<List<ProblemSetDTO>> getRecentProblemSetsLazyLoad(
            @RequestHeader(value = "x-initiator", required = true) String initiator,
            @RequestHeader(value = "x-archive-mode", required = false) String archiveMode,
            @RequestParam(value = "x-content-size", defaultValue = "15") String size,
            @RequestHeader(value = "x-offset", defaultValue = "-1") String offset
            ) {
        Integer pageCount = Integer.parseInt(size);
        Integer offsetInt = Integer.parseInt(offset);
        List<ProblemSetDTO> problemSets = problemSetService.getLatestProblemSetsForUsers(
            initiator, archiveMode, pageCount, offsetInt
        );
        String loadMore = "true";
        if (problemSets.size() < pageCount) {
            loadMore = "false";
        } else {
            Integer remainingCount = problemSetService.getRemainingCountOfLatestProblemSetsForUsers(
                initiator, archiveMode, offsetInt
            );
            if (remainingCount <= problemSets.size()) {
                loadMore = "false";
            }
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("x-load-more", loadMore);
        return new ResponseEntity<>(problemSets, responseHeaders, HttpStatus.OK);
    }

    @GetMapping(path = "/recentSolutions")
    ResponseEntity<List<SolutionSetDTO>> getRecentSolutionSets(
            @RequestHeader(value = "x-initiator", required = true) String initiator,
            @RequestHeader(value = "x-archive-mode", required = false) String archiveMode,
            @RequestHeader(value = "x-content-size", defaultValue = "15") String size
            ) {
        return new ResponseEntity<>(problemSolutionService.getProblemSetSolutionsForUsers(
                initiator, archiveMode, Integer.parseInt(size)
        ), HttpStatus.OK);
    }

    @GetMapping(path = "/v2/recentSolutions")
    ResponseEntity<List<SolutionSetDTO>> getRecentSolutionSetsLazyLoad(
            @RequestHeader(value = "x-initiator", required = true) String initiator,
            @RequestHeader(value = "x-archive-mode", required = false) String archiveMode,
            @RequestHeader(value = "x-content-size", defaultValue = "15") String size,
            @RequestHeader(value = "x-offset", defaultValue = "-1") String offset
            ) {
        Integer pageCount = Integer.parseInt(size);
        Integer offsetInt = Integer.parseInt(offset);
        List<SolutionSetDTO> problemSets = problemSolutionService.getLatestProblemSetSolutionsForUsers(
            initiator, archiveMode, pageCount, offsetInt
        );
        String loadMore = "true";
        if (problemSets.size() < pageCount) {
            loadMore = "false";
        } else {
            Integer remainingCount = problemSolutionService.getRemainingCountOfLatestProblemSetSolutionsForUsers(
                initiator, archiveMode, offsetInt
            );
            if (remainingCount <= problemSets.size()) {
                loadMore = "false";
            }
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("x-load-more", loadMore);
        return new ResponseEntity<>(problemSets, responseHeaders, HttpStatus.OK);
    }
}
