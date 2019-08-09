package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.ReviewSolutionRevision;
import org.benetech.mathshare.model.entity.SolutionRevision;

public abstract class ReviewSolutionRevisionMother {

    public static final Long REVIEW_CODE = 89L;

    public static ReviewSolutionRevision validInstance(SolutionRevision solutionRevision,
                                                       ProblemSetRevisionSolution problemSetRevisionSolution) {
        return new ReviewSolutionRevision(solutionRevision, REVIEW_CODE, problemSetRevisionSolution);
    }

    public static ReviewSolutionRevision mockInstance() {
        return validInstance(
                SolutionRevisionMother.mockInstance(),
                ProblemSetRevisionSolutionMother.mockInstance()
        );
    }
}
