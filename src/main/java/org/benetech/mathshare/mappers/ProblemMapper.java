package org.benetech.mathshare.mappers;

import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProblemMapper {

    ProblemMapper INSTANCE = Mappers.getMapper(ProblemMapper.class);

    @Mapping(source = "problemText", target = "text")
    ProblemDTO toDto(Problem problem);
}
