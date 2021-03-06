package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

import java.util.List;

public abstract class ProblemSetRevisionMother {

    public static final String VALID_CODE = "777777V4A43EE";

    public static final String REVISION_CODE = "AAAAAABAKTWFG";

    public static final String INVALID_CODE = "1777777V4A43EE";

    public static final String TITLE = "test";

    public static ProblemSetRevision validInstance(ProblemSet problemSet) {
        ProblemSetRevision result = new ProblemSetRevision(problemSet, TITLE, false, false, problemSet.getPalettes());
        result.setShareCode(UrlCodeConverter.fromUrlCode(VALID_CODE));
        return result;
    }

    public static ProblemSetRevision revisionOf(ProblemSet problemSet) {
        return new ProblemSetRevision(problemSet, TITLE, false, false, problemSet.getPalettes());
    }

    public static ProblemSetRevision withShareCode(ProblemSet problemSet, long code) {
        ProblemSetRevision result = validInstance(problemSet);
        result.setShareCode(code);
        return result;
    }

    public static ProblemSetRevision withProblems(ProblemSet problemSet, int size) {
        ProblemSetRevision result = validInstance(problemSet);
        List<Problem> problems = ProblemMother.createValidProblemsList(result, size);
        result.setProblems(problems);
        return result;
    }

    public static ProblemSetRevision mockInstance() {
        ProblemSetRevision result = validInstance(ProblemSetMother.mockInstance());
        return result;
    }
}
