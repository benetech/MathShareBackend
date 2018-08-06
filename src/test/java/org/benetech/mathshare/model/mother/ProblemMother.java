package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;

public abstract class ProblemMother {

    public static final String DEFAULT_PROBLEM_TEXT = "problem text";

    public static Problem createValidInstance() {
        ProblemSet problemSet = ProblemSetMother.createValidInstance();
        return new Problem(problemSet, DEFAULT_PROBLEM_TEXT);
    }
}
