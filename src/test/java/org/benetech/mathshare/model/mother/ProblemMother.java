package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ProblemMother {

    public static final String DEFAULT_PROBLEM_TEXT = "problem text";

    public static Problem validInstance(ProblemSet problemSet, String text) {
        return new Problem(problemSet, text);
    }

    public static Problem validInstance() {
        return validInstance(DEFAULT_PROBLEM_TEXT);
    }

    public static Problem validInstance(String text) {
        ProblemSet problemSet = ProblemSetMother.validInstance();
        return validInstance(problemSet, text);
    }

    public static Problem validInstance(ProblemSet problemSet) {
        return validInstance(problemSet, DEFAULT_PROBLEM_TEXT);
    }

    public static List<Problem> createValidProblemsList(ProblemSet problemSet, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> validInstance(problemSet, String.valueOf(i)))
                .collect(Collectors.toList());
    }

    public static List<Problem> createValidProblemsList(int size) {
        return createValidProblemsList(ProblemSetMother.validInstance(), size);
    }
}
