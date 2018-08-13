package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
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
            @Mapping(source = "problem.problemText", target = "problem.text"),
            @Mapping(source = "problem.problemSetRevision.shareCode", target = "problem.problemSetRevisionShareCode",
                    qualifiedByName = "toCode")})
    SolutionDTO toDto(ProblemSolution solution);

    @Mappings({
        @Mapping(source = "problem.text", target = "problem.problemText"),
        @Mapping(source = "problem.problemSetRevisionShareCode", target = "problem.problemSetRevision.shareCode",
                qualifiedByName = "fromCode")})
    ProblemSolution fromDto(SolutionDTO solution);

    SolutionStepDTO toDto(SolutionStep solutionStep);

    @Mappings({
            @Mapping(source = "problemSolution.problem", target = "problem", qualifiedByName = "problem"),
            @Mapping(source = "shareCode", target = "shareCode", qualifiedByName = "shareCode"),
            @Mapping(source = "problemSolution.editCode", target = "editCode", qualifiedByName = "toCode")})
    SolutionDTO toSolutionDTO(SolutionRevision revision);

    @Named("toCode")
    default String toCode(Long editCode) {
        return editCode == null ? null : UrlCodeConverter.toUrlCode(editCode);
    }

    @Named("fromCode")
    default Long fromCode(String editCode) {
        return editCode == null ? null : UrlCodeConverter.fromUrlCode(editCode);
    }

    @Named("problem")
    default ProblemDTO mapProblem(Problem problem) {
        return ProblemMapper.INSTANCE.toDto(problem);
    }
}
