package org.benetech.mathshare.mappers;

import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.dto.ProblemStepDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemStep;
import org.benetech.mathshare.model.entity.Scratchpad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ProblemMapper {

    ProblemMapper INSTANCE = Mappers.getMapper(ProblemMapper.class);

    @Mappings({
            @Mapping(source = "scratchpad", target = "scratchpad", qualifiedByName = "fromScratchpad"),
            @Mapping(source = "problemText", target = "text"),
            @Mapping(source = "steps", target = "steps", qualifiedByName = "toStepDto"),
            @Mapping(source = "problemSetRevision.shareCode", target = "problemSetRevisionShareCode",
                    qualifiedByName = "toCode")})
    ProblemDTO toDto(Problem problem);

    @Mappings({
            @Mapping(source = "scratchpad", target = "scratchpad", qualifiedByName = "toScratchpad"),
            @Mapping(source = "text", target = "problemText"),
            @Mapping(source = "problemSetRevisionShareCode", target = "problemSetRevision.shareCode",
                    qualifiedByName = "fromCode")})
    Problem fromDto(ProblemDTO problem);

    @Mapping(source = "scratchpad", target = "scratchpad", qualifiedByName = "fromScratchpad")
    ProblemStepDTO toStepDto(ProblemStep problemStep);

    @Mapping(source = "scratchpad", target = "scratchpad", qualifiedByName = "toScratchpad")
    ProblemStep fromStepDto(ProblemStepDTO problemStep);

    @Mapping(source = "editCode", target = "editCode", qualifiedByName = "fromCode")
    ProblemSet fromDto(ProblemSetDTO problemSet);

    @Mappings({
            @Mapping(source = "editCode", target = "editCode", qualifiedByName = "toCode"),
            @Mapping(source = "latestRevision.title", target = "title", qualifiedByName = "title"),
            @Mapping(source = "latestRevision.shareCode", target = "shareCode", qualifiedByName = "toCode"),
            @Mapping(source = "latestRevision.problems", target = "problemCount", qualifiedByName = "countOfProblems"),
            @Mapping(source = "latestRevision.problems", target = "problems", qualifiedByName = "sortProblems"),
    })
    ProblemSetDTO toDto(ProblemSet problemSet);

    @Mappings({
            @Mapping(source = "problems", target = "problems", qualifiedByName = "sortProblems"),
            @Mapping(source = "shareCode", target = "shareCode", qualifiedByName = "toCode"),
            @Mapping(source = "problemSet.editCode", target = "editCode", qualifiedByName = "toCode"),
            @Mapping(source = "palettes", target = "palettes", qualifiedByName = "palettes"),
            @Mapping(source = "problems", target = "problemCount", qualifiedByName = "countOfProblems")
    })
    ProblemSetDTO toProblemSetDTO(ProblemSetRevision revision);

    @Named("toCode")
    default String toCode(Long shareCode) {
        return MapperUtils.toCode(shareCode);
    }

    @Named("fromCode")
    default Long fromCode(String editCode) {
        return MapperUtils.fromCode(editCode);
    }

    @Named("fromScratchpad")
    default String fromScratchpad(Scratchpad scratchpad) {
        return MapperUtils.fromScratchpad(scratchpad);
    }

    @Named("toScratchpad")
    default Scratchpad toScratchpad(String content) {
        return MapperUtils.toScratchpad(content);
    }

    @Named("sortProblems")
    default List<ProblemDTO> sortProblems(List<Problem> problems) {
        if (problems == null) {
            return new ArrayList<ProblemDTO>();
        }
        return problems.stream().sorted(
            Comparator.comparing(Problem::getPosition, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(
                    Comparator.comparing(Problem::getId, Comparator.nullsLast(Comparator.naturalOrder()))
                )
        ).map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Named("countOfProblems")
    default int countOfProblems(List<Problem> problems) {
        if (problems == null) {
            return 0;
        }
        return problems.size();
    }
}
