package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ProblemMother {

    public static final String DEFAULT_PROBLEM_TEXT = "problem text";

    public static final String DEFAULT_PROBLEM_TITLE = "title";

    public static Problem validInstance(ProblemSetRevision revision) {
        return withText(revision, DEFAULT_PROBLEM_TEXT);
    }

    public static Problem withText(ProblemSetRevision revision, String text) {
        return new Problem(revision, text, DEFAULT_PROBLEM_TITLE);
    }

    public static List<Problem> createValidProblemsList(ProblemSetRevision revision, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> withText(revision, String.valueOf(i)))
                .collect(Collectors.toList());
    }

    public static Problem mockInstance() {
        ProblemSetRevision revision = ProblemSetRevisionMother.mockInstance();
        return validInstance(revision);
    }
}
