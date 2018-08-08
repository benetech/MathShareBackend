package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProblemMapper {

    ProblemMapper INSTANCE = Mappers.getMapper(ProblemMapper.class);

    @Mapping(source = "problemText", target = "text")
    ProblemDTO toDto(Problem problem);

    ProblemSet fromDto(ProblemSetDTO problemSet);

    ProblemSetDTO toDto(ProblemSet problemSet);

    @Mappings({
            @Mapping(source = "shareCode", target = "shareCode", qualifiedByName = "shareCode"),
            @Mapping(source = "problemSet.editCode", target = "editCode", qualifiedByName = "editCode")})
    ProblemSetDTO toProblemSetDTO(ProblemSetRevision revision);

    @Named("shareCode")
    default String mapShareCode(Long shareCode) {
        return shareCode == null ? null : UrlCodeConverter.toUrlCode(shareCode);
    }

    @Named("editCode")
    default String mapEditCode(Long editCode) {
        return editCode == null ? null : UrlCodeConverter.toUrlCode(editCode);
    }
}
