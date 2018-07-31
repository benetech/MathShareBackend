package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;

public class ProblemMother {

    public static final String DEFAULT_PROBLEM_TEXT = "problem text";

    public static Problem validInstance() {
        ProblemSet problemSet = ProblemSetMother.validInstance();
        return new Problem(problemSet, DEFAULT_PROBLEM_TEXT);
    }
}
