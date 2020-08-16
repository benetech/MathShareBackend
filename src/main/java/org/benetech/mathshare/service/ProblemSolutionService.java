package org.benetech.mathshare.service;

import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionSetDTO;
import org.benetech.mathshare.model.dto.SolutionSetPublicDTO;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

public interface ProblemSolutionService {

    SolutionRevision getLatestSolutionRevision(long editUrl);

    SolutionRevision getSolutionRevisionByShareUrl(long shareUrl);

    SolutionRevision saveNewVersionOfSolution(SolutionDTO solution);

    SolutionRevision saveNewVersionOfSolutionWithExistingEditCode(SolutionDTO solution);

    SolutionDTO findSolutionByUrlCode(String code) throws IllegalArgumentException;

    Pair<Boolean, SolutionRevision> createOrUpdateProblemSolution(String code, SolutionDTO solution);

    SolutionDTO getLatestProblemSolutionForEditing(String code);

    SolutionSetDTO createReviewSolutions(String code, List<SolutionDTO> solutionsDTO, String initiator);

    SolutionSetDTO createReviewSolutionsFromShareCode(String code, Map<String, String> searchParameters, String initiator,
        String partnerCode);

    SolutionSetDTO updateReviewSolutions(String code, List<SolutionDTO> solutionsDTO);

    SolutionSetDTO getProblemSetSolutions(String editCode);

    SolutionSetPublicDTO getReviewSolutions(String code);

    SolutionSetDTO setArchiveMode(String code, String userId, String role, String archiveMode);

    List<SolutionSetDTO> getProblemSetSolutionsForUsers(String userId, String archiveMode, int n);
}
