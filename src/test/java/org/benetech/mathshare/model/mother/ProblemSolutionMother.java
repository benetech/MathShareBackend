package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSolution;

public abstract class ProblemSolutionMother {

    public static final Long EDIT_CODE = 89L;

    public static ProblemSolution validInstance(Problem problem) {
        return new ProblemSolution(problem);
    }

    public static ProblemSolution withEditCode(Problem problem, long editCode) {
        ProblemSolution result = validInstance(problem);
        result.setEditCode(editCode);
        return result;
    }

    public static ProblemSolution mockInstance() {
        return withEditCode(ProblemMother.mockInstance(), EDIT_CODE);
    }
}
