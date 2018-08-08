package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSolution;

public abstract class ProblemSolutionMother {

    public static ProblemSolution validInstance() {
        Problem problem = ProblemMother.validInstance();
        return new ProblemSolution(problem);
    }

    public static ProblemSolution withEditCode(long code) {
        ProblemSolution result = validInstance();
        result.setEditCode(code);
        return result;
    }
}
