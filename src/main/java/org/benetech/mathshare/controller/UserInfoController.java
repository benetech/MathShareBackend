package org.benetech.mathshare.controller;

import org.benetech.mathshare.mappers.UserInfoMapper;
import org.benetech.mathshare.model.dto.UserInfoDTO;
import org.benetech.mathshare.model.entity.UserInfo;
import org.benetech.mathshare.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    private final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping(path = "/submit")
    ResponseEntity<UserInfoDTO> saveUserInfo(@RequestBody UserInfoDTO payload) {
        UserInfo saved = userInfoService.saveNewUserInfo(payload);
        return new ResponseEntity<>(UserInfoMapper.INSTANCE.toDto(saved), HttpStatus.CREATED);
    }

    @PostMapping(path = "/fetch")
    ResponseEntity<UserInfoDTO> fetchUserInfo(@RequestBody UserInfoDTO payload) {
        UserInfo body = userInfoService.getUserInfoByEmail(payload.getEmail());
        if (body != null) {
            return new ResponseEntity<>(UserInfoMapper.INSTANCE.toDto(body), HttpStatus.OK);
        } else {
            logger.error("User with email {} wasn't found", payload.getEmail());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
