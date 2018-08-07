package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;

public interface ProblemSolutionService {

    SolutionRevision getLatestSolutionRevision(long editUrl);

    SolutionRevision getSolutionRevisionByShareUrl(long shareUrl);

    SolutionRevision saveNewVersionOfSolution(ProblemSolution problemSolution);
}
