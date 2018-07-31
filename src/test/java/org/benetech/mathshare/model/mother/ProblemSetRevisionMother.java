package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

public class ProblemSetRevisionMother {

    public static final String DEFAULT_SHARE_CODE = "4g5DA4vsf";

    public static ProblemSetRevision validInstance() {
        ProblemSet problemSet = ProblemSetMother.validInstance();
        return new ProblemSetRevision(problemSet, DEFAULT_SHARE_CODE);
    }
}
