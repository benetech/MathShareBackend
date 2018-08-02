package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

public abstract class ProblemSetRevisionUtils {

    public static ProblemSetRevision createValidInstance() {
        ProblemSet problemSet = ProblemSetUtils.createValidInstance();
        return new ProblemSetRevision(problemSet);
    }
}
