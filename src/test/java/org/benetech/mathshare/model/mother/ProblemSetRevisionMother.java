package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

import java.util.List;

public abstract class ProblemSetRevisionMother {

    public static final String VALID_CODE = "777777V4A43EE";

    public static final String INVALID_CODE = "1777777V4A43EE";

    public static ProblemSetRevision validInstance() {
        ProblemSet problemSet = ProblemSetMother.validInstance();
        return validInstance(problemSet);
    }

    public static ProblemSetRevision validInstance(ProblemSet problemSet) {
        return new ProblemSetRevision(problemSet);
    }

    public static ProblemSetRevision createNewRevisionOfValidInstance(ProblemSet problemSet) {
        return new ProblemSetRevision(problemSet);
    }

    public static ProblemSetRevision withShareCode(long code) {
        ProblemSetRevision result = validInstance();
        result.setShareCode(code);
        return result;
    }

    public static ProblemSetRevision withProblems(int size) {
        ProblemSetRevision result = validInstance();
        List<Problem> problems = ProblemMother.createValidProblemsList(result, size);
        result.setProblems(problems);
        return result;
    }
}
