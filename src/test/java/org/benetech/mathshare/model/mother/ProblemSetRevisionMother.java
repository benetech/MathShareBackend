package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

public abstract class ProblemSetRevisionMother {

    public static ProblemSetRevision createValidInstance() {
        ProblemSet problemSet = ProblemSetMother.createValidInstance();
        return new ProblemSetRevision(problemSet);
    }

    public static ProblemSetRevision createNewRevisionOfValidInstance(ProblemSet problemSet) {
        return new ProblemSetRevision(problemSet);
    }
}
