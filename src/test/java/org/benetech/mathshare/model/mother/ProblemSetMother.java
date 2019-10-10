package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

import java.util.Arrays;

public abstract class ProblemSetMother {

    public static final Long EDIT_CODE = 49L;
    public static final String PALETTE = "Geometry";
    public static final String USER_ID = "aab63d78-cff6-11e9-83a9-935feb96b1df";

    public static ProblemSet validInstance() {
        ProblemSet set = new ProblemSet();
        set.setPalettes(Arrays.asList(PALETTE));
        return set;
    }

    public static ProblemSet withEditCode(long code) {
        ProblemSet result = validInstance();
        result.setEditCode(code);
        return result;
    }

    public static ProblemSet withUserId(String id) {
        ProblemSet result = withEditCode(EDIT_CODE);
        result.setUserId(USER_ID);
        result.setLatestRevision(ProblemSetRevisionMother.validInstance(result));
        return result;
    }

    public static ProblemSet mockInstance() {
        return withUserId(USER_ID);
    }
}
