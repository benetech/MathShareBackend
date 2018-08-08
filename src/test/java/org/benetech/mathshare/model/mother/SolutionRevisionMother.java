package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;

public abstract class SolutionRevisionMother {

    public static SolutionRevision validInstance() {
        ProblemSolution problemSolution = ProblemSolutionMother.validInstance();
        return new SolutionRevision(problemSolution);
    }

    public static SolutionRevision createNewRevisionOfValidInstance(ProblemSolution problemSolution) {
        return new SolutionRevision(problemSolution);
    }

    public static SolutionRevision withShareCode(long code) {
        SolutionRevision result = validInstance();
        result.setShareCode(code);
        return result;
    }

    public static SolutionRevision withShareCodeAndEditCode(long shareCode, long editCode) {
        ProblemSolution problemSolution = ProblemSolutionMother.withEditCode(editCode);
        SolutionRevision result = new SolutionRevision(problemSolution);
        result.setShareCode(shareCode);
        return result;
    }

    public static SolutionRevision withSteps(int size) {
        SolutionRevision result = validInstance();
        result.setSteps(SolutionStepMother.createValidStepsList(size));
        return result;
    }
}
