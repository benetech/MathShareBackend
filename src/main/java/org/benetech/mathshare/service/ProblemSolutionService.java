package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;

public interface ProblemSolutionService {

    SolutionRevision getLatestSolutionRevision(Long editUrl);

    SolutionRevision getSolutionRevisionByShareUrl(Long shareUrl);

    SolutionRevision saveNewVersionOfSolution(ProblemSolution problemSolution);
}
