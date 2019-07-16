package org.benetech.mathshare.service;

import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionSetDTO;
import org.benetech.mathshare.model.dto.SolutionSetPublicDTO;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.springframework.data.util.Pair;

import java.util.List;

public interface ProblemSolutionService {

    SolutionRevision getLatestSolutionRevision(long editUrl);

    SolutionRevision getSolutionRevisionByShareUrl(long shareUrl);

    SolutionRevision saveNewVersionOfSolution(SolutionDTO solution);

    SolutionRevision saveNewVersionOfSolutionWithExistingEditCode(SolutionDTO solution);

    SolutionDTO findSolutionByUrlCode(String code) throws IllegalArgumentException;

    Pair<Boolean, SolutionRevision> createOrUpdateProblemSolution(String code, SolutionDTO solution);

    SolutionDTO getLatestProblemSolutionForEditing(String code);

    SolutionSetDTO createReviewSolutions(String code, List<SolutionDTO> solutionsDTO);

    SolutionSetDTO updateReviewSolutions(String code, List<SolutionDTO> solutionsDTO);

    SolutionSetDTO getProblemSetSolutions(String editCode);

    SolutionSetPublicDTO getReviewSolutions(String code);
}
