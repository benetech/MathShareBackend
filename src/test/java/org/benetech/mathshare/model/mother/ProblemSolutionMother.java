package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSolution;

public abstract class ProblemSolutionMother {

    public static ProblemSolution createValidInstance() {
        Problem problem = ProblemMother.createValidInstance();
        return new ProblemSolution(problem);
    }
}
