package org.benetech.mathshare.service.impl;

import org.benetech.mathshare.mappers.UserInfoMapper;
import org.benetech.mathshare.model.dto.UserInfoDTO;
import org.benetech.mathshare.model.entity.UserInfo;
import org.benetech.mathshare.repository.UserInfoRepository;
import org.benetech.mathshare.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public UserInfo getUserInfoByEmail(String email) {
        return userInfoRepository.findOneByEmail(email);
    }

    @Override
    public UserInfo saveNewUserInfo(UserInfoDTO payload) throws IllegalArgumentException {
        UserInfo userInfo = UserInfoMapper.INSTANCE.fromDto(payload);
        userInfoRepository.save(userInfo);
        em.refresh(userInfo);
        return userInfo;
    }

    @Override
    public UserInfo updateUserInfo(UserInfoDTO payload) throws IllegalArgumentException {
        UserInfo userInfoPayload = UserInfoMapper.INSTANCE.fromDto(payload);
        UserInfo existingUserInfo = userInfoRepository.findById(userInfoPayload.getId()).get();
        if (userInfoPayload.getInfoVersion() > existingUserInfo.getInfoVersion()) {
            userInfoRepository.save(userInfoPayload);
            em.refresh(userInfoPayload);
            return userInfoPayload;
        }
        return existingUserInfo;
    }

    @Override
    public Integer setNotifyForMobile(String email, Integer notifyForMobile) {
        return userInfoRepository.setNotifyForMobileInUserInfo(email, notifyForMobile);
    }
}
