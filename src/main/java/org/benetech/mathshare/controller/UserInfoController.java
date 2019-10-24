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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    private final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping(path = "/submit")
    ResponseEntity<UserInfoDTO> saveUserInfo(
            @RequestBody UserInfoDTO payload,
            @RequestHeader(value = "x-initiator-email") String email
    ) {
        payload.setEmail(email);
        UserInfo saved = userInfoService.saveNewUserInfo(payload);
        return new ResponseEntity<>(UserInfoMapper.INSTANCE.toDto(saved), HttpStatus.CREATED);
    }

    @GetMapping(path = "/fetch")
    ResponseEntity<UserInfoDTO> fetchUserInfo(@RequestHeader(value = "x-initiator-email") String email) {
        UserInfo body = userInfoService.getUserInfoByEmail(email);
        if (body != null) {
            return new ResponseEntity<>(UserInfoMapper.INSTANCE.toDto(body), HttpStatus.OK);
        } else {
            logger.error("User with email {} wasn't found", email);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/notifyForMobile")
    ResponseEntity<Integer> updateNotifyForMobile(
            @RequestBody UserInfoDTO payload,
            @RequestHeader(value = "x-initiator-email") String email
    ) {
        Integer body = userInfoService.setNotifyForMobile(email, payload.getNotifyForMobile());
        if (body != null) {
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            logger.error("Unable to update user with email {}", email);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
