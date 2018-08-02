package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSolution;

public abstract class ProblemSolutionUtils {

    public static ProblemSolution createValidInstance() {
        Problem problem = ProblemUtils.createValidInstance();
        return new ProblemSolution(problem);
    }
}
