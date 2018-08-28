package org.benetech.mathshare.mappers;

import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionStepDTO;
import org.benetech.mathshare.model.entity.Problem;
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

    @Mappings({
            @Mapping(source = "problem", target = "problem", qualifiedByName = "toProblemDto"),
            @Mapping(source = "editCode", target = "editCode", qualifiedByName = "toCode")})
        SolutionDTO toDto(ProblemSolution solution);

    @Mappings({
            @Mapping(source = "problem", target = "problem", qualifiedByName = "fromProblemDto"),
            @Mapping(source = "editCode", target = "editCode", qualifiedByName = "fromCode")})
    ProblemSolution fromDto(SolutionDTO solution);

    SolutionStepDTO toDto(SolutionStep solutionStep);

    SolutionStep fromDto(SolutionStepDTO solutionStep);

    @Mappings({
            @Mapping(source = "problemSolution.problem", target = "problem", qualifiedByName = "problem"),
            @Mapping(source = "shareCode", target = "shareCode", qualifiedByName = "toCode"),
            @Mapping(source = "problemSolution.editCode", target = "editCode", qualifiedByName = "toCode")})
    SolutionDTO toSolutionDTO(SolutionRevision revision);

    @Named("toCode")
    default String toCode(Long shareCode) {
        return MapperUtils.toCode(shareCode);
    }

    @Named("fromCode")
    default Long fromCode(String editCode) {
        return MapperUtils.fromCode(editCode);
    }

    @Named("toProblemDto")
    default ProblemDTO toProblemDto(Problem problem) {
        return ProblemMapper.INSTANCE.toDto(problem);
    }

    @Named("fromProblemDto")
    default Problem fromProblemDto(ProblemDTO problem) {
        return ProblemMapper.INSTANCE.fromDto(problem);
    }
}
