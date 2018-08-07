package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

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

    public static ProblemSetRevision withShareCode() {
        ProblemSetRevision result = validInstance();
        result.setShareCode(UrlCodeConverter.fromUrlCode(VALID_CODE));
        return result;
    }
}
