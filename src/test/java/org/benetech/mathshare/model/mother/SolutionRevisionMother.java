package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;

public abstract class SolutionRevisionMother {

    public static SolutionRevision validInstance(ProblemSolution problemSolution) {
        return new SolutionRevision(problemSolution);
    }

    public static SolutionRevision revisionOf(ProblemSolution problemSolution) {
        return new SolutionRevision(problemSolution);
    }

    public static SolutionRevision withShareCode(ProblemSolution problemSolution, long code) {
        SolutionRevision result = validInstance(problemSolution);
        result.setShareCode(code);
        return result;
    }

    public static SolutionRevision withSteps(ProblemSolution problemSolution, int size) {
        SolutionRevision result = validInstance(problemSolution);
        result.setSteps(SolutionStepMother.createValidStepsList(result, size));
        return result;
    }

    public static SolutionRevision mockInstance() {
        return validInstance(ProblemSolutionMother.mockInstance());
    }
}
