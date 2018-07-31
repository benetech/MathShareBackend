package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSolution;

public class ProblemSolutionMother {

    public static final String DEFAULT_EDIT_CODE = "bgk65qDFE";

    public static ProblemSolution validInstance() {
        Problem problem = ProblemMother.validInstance();
        return new ProblemSolution(problem, DEFAULT_EDIT_CODE);
    }
}
