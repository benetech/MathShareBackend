package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

public abstract class ProblemSetRevisionUtils {

    public static final String DEFAULT_SHARE_CODE = "4g5DA4vsf";

    public static ProblemSetRevision createValidInstance() {
        ProblemSet problemSet = ProblemSetUtils.createValidInstance();
        return new ProblemSetRevision(problemSet, DEFAULT_SHARE_CODE);
    }
}
