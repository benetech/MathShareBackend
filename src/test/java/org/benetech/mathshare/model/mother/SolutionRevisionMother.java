package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;

public class SolutionRevisionMother {

    public static final String DEFAULT_SHARE_CODE = "abc123XYZ";

    public static SolutionRevision validInstance() {
        ProblemSolution problemSolution = ProblemSolutionMother.validInstance();
        return new SolutionRevision(DEFAULT_SHARE_CODE, problemSolution);
    }
}
