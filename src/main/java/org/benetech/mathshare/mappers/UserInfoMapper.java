package org.benetech.mathshare.mappers;

import org.benetech.mathshare.model.dto.UserInfoDTO;
import org.benetech.mathshare.model.entity.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserInfoMapper {

    UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

    UserInfo fromDto(UserInfoDTO userInfo);

    UserInfoDTO toDto(UserInfo userInfo);
}
