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

    @Mappings({
            @Mapping(source = "problemText", target = "text"),
            @Mapping(source = "problemSetRevision.shareCode", target = "problemSetRevisionShareCode",
                    qualifiedByName = "toCode")})
    ProblemDTO toDto(Problem problem);

    @Mappings({
            @Mapping(source = "text", target = "problemText"),
            @Mapping(source = "problemSetRevisionShareCode", target = "problemSetRevision.shareCode",
                    qualifiedByName = "fromCode")})
    Problem fromDto(ProblemDTO problem);

    @Mappings({
            @Mapping(source = "editCode", target = "editCode", qualifiedByName = "fromCode")})
    ProblemSet fromDto(ProblemSetDTO problemSet);

    @Mappings({
            @Mapping(source = "editCode", target = "editCode", qualifiedByName = "toCode")})
    ProblemSetDTO toDto(ProblemSet problemSet);

    @Mappings({
            @Mapping(source = "shareCode", target = "shareCode", qualifiedByName = "toCode"),
            @Mapping(source = "problemSet.editCode", target = "editCode", qualifiedByName = "toCode")})
    ProblemSetDTO toProblemSetDTO(ProblemSetRevision revision);

    @Named("toCode")
    default String toCode(Long shareCode) {
        return shareCode == null ? null : UrlCodeConverter.toUrlCode(shareCode);
    }

    @Named("fromCode")
    default Long fromCode(String editCode) {
        return editCode == null ? null : UrlCodeConverter.fromUrlCode(editCode);
    }
}
