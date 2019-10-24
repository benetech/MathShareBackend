package org.benetech.mathshare.service;

import org.benetech.mathshare.model.dto.UserInfoDTO;
import org.benetech.mathshare.model.entity.UserInfo;

public interface UserInfoService {

    UserInfo getUserInfoByEmail(String email);

    UserInfo saveNewUserInfo(UserInfoDTO payload) throws IllegalArgumentException;

    Integer setNotifyForMobile(String email, Integer notifyForMobile);
}
