package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;

import java.util.List;

public abstract class ProblemSetMother {

    public static ProblemSet validInstance() {
        return new ProblemSet();
    }

    public static ProblemSet withProblems(int size) {
        ProblemSet result = validInstance();
        List<Problem> problems = ProblemMother.createValidProblemsList(result, size);
        result.setProblems(problems);
        return result;
    }
}
