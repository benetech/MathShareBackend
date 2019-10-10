package org.benetech.mathshare.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
////import org.springframework.beans.factory.annotation.Autowired;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.service.ProblemSetService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(path = "/recent")
    ResponseEntity<List<ProblemSetDTO>> getRecentProblemSets(
            @RequestHeader(value = "x-initiator", required = false) String initiator,
            @RequestParam(value = "x-content-size", defaultValue = "15") String size
            ) {
        return new ResponseEntity<>(problemSetService.findLastNProblemSetsOfUser(
                initiator, Integer.parseInt(size)
        ), HttpStatus.OK);
    }
}
