package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ProblemMother {

    public static final String DEFAULT_PROBLEM_TEXT = "problem text";

    public static Problem validInstance(ProblemSetRevision problemSetRevision, String text) {
        return new Problem(problemSetRevision, text);
    }

    public static Problem validInstance() {
        return validInstance(DEFAULT_PROBLEM_TEXT);
    }

    public static Problem validInstance(String text) {
        ProblemSetRevision problemSetRevision = ProblemSetRevisionMother.validInstance();
        return validInstance(problemSetRevision, text);
    }

    public static Problem validInstance(ProblemSetRevision problemSetRevision) {
        return validInstance(problemSetRevision, DEFAULT_PROBLEM_TEXT);
    }

    public static List<Problem> createValidProblemsList(ProblemSetRevision problemSetRevision, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> validInstance(problemSetRevision, String.valueOf(i)))
                .collect(Collectors.toList());
    }

    public static List<Problem> createValidProblemsList(int size) {
        return createValidProblemsList(ProblemSetRevisionMother.validInstance(), size);
    }
}
