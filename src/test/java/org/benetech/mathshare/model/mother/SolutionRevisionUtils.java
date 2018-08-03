package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;

public abstract class SolutionRevisionUtils {

    public static final String DEFAULT_SHARE_CODE = "abc123XYZ";
    public static final String DEFAULT_SHARE_CODE_NEW_REV = "a7E3n7fQc";

    public static SolutionRevision createValidInstance() {
        ProblemSolution problemSolution = ProblemSolutionUtils.createValidInstance();
        return new SolutionRevision(DEFAULT_SHARE_CODE, problemSolution);
    }

    public static SolutionRevision createNewRevisionOfValidInstance(ProblemSolution problemSolution) {
        return new SolutionRevision(DEFAULT_SHARE_CODE_NEW_REV, problemSolution);
    }

}
