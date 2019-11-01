package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    UserInfo findOneByEmail(String email);

    @Modifying
    @Query("update UserInfo u set u.notifyForMobile = ?2 where u.email = ?1")
    Integer setNotifyForMobileInUserInfo(String email, Integer notifyForMobile);
}
