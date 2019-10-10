package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;


public abstract class ProblemSetRevisionSolutionMother {


    public static final Long EDIT_CODE = 89L;

    public static ProblemSetRevisionSolution validInstance(ProblemSetRevision problemSetRevision) {
        return new ProblemSetRevisionSolution(problemSetRevision, EDIT_CODE);
    }

    public static ProblemSetRevisionSolution mockInstance() {
        return validInstance(ProblemSetRevisionMother.mockInstance());
    }
}
