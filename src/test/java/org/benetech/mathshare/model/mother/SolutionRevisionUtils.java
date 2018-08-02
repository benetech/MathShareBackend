package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;

public abstract class SolutionRevisionUtils {

    public static SolutionRevision createValidInstance() {
        ProblemSolution problemSolution = ProblemSolutionUtils.createValidInstance();
        return new SolutionRevision(problemSolution);
    }
}
