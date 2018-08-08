package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionStepDTO;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.entity.SolutionStep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SolutionMapper {

    SolutionMapper INSTANCE = Mappers.getMapper(SolutionMapper.class);

    SolutionDTO toDto(ProblemSolution solution);

    ProblemSolution fromDto(SolutionDTO solution);

    SolutionStepDTO toDto(SolutionStep solutionStep);

    @Mappings({
            @Mapping(source = "problemSolution.problem", target = "problem"),
            @Mapping(source = "shareCode", target = "shareCode", qualifiedByName = "shareCode"),
            @Mapping(source = "problemSolution.editCode", target = "editCode", qualifiedByName = "editCode")})
    SolutionDTO toSolutionDTO(SolutionRevision revision);

    @Named("shareCode")
    default String mapShareCode(Long shareCode) {
        return shareCode == null ? null : UrlCodeConverter.toUrlCode(shareCode);
    }

    @Named("editCode")
    default String mapEditCode(Long editCode) {
        return editCode == null ? null : UrlCodeConverter.toUrlCode(editCode);
    }
}
