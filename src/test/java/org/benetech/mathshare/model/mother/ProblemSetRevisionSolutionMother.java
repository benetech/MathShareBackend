package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;


public abstract class ProblemSetRevisionSolutionMother {


    public static final Long EDIT_CODE = 89L;
    public static final String USER_ID = "aab63d78-cff6-11e9-83a9-935feb96b1df";

    public static ProblemSetRevisionSolution validInstance(ProblemSetRevision problemSetRevision) {
        return new ProblemSetRevisionSolution(problemSetRevision, EDIT_CODE, USER_ID);
    }

    public static ProblemSetRevisionSolution mockInstance() {
        return validInstance(ProblemSetRevisionMother.mockInstance());
    }
}
